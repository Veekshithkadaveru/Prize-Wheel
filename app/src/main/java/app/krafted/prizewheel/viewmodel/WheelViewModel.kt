package app.krafted.prizewheel.viewmodel

import androidx.compose.animation.core.Animatable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.prizewheel.R
import app.krafted.prizewheel.data.INITIAL_COIN_BALANCE
import app.krafted.prizewheel.data.LeaderboardEntry
import app.krafted.prizewheel.data.SpinResultEntity
import app.krafted.prizewheel.data.WalletEntity
import app.krafted.prizewheel.data.WheelDao
import app.krafted.prizewheel.game.WheelEngine
import app.krafted.prizewheel.game.WheelSegment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WheelUiState(
    val coins: Int = INITIAL_COIN_BALANCE,
    val isSpinning: Boolean = false,
    val lastResult: WheelSegment? = null,
    val showWinBanner: Boolean = false,
    val canSpin: Boolean = true,
    val currentBackground: Int = R.drawable.plin_back_5,
    val paidSpinsUsed: Int = 0,
    val sessionComplete: Boolean = false,
    val playerName: String = "Player",
    val sessionCoinsWon: Int = 0,
    val dailyRefillClaimed: Boolean = false,
    val lastRefillTimestamp: Long = 0L
)

data class SpinEvent(val targetRotation: Float, val result: WheelSegment)

class WheelViewModel(private val wheelDao: WheelDao) : ViewModel() {
    val rotation = Animatable(0f)
    private var currentSessionId = System.currentTimeMillis()

    private val _uiState = MutableStateFlow(WheelUiState())
    val uiState: StateFlow<WheelUiState> = _uiState.asStateFlow()

    private val _spinEvents = MutableSharedFlow<SpinEvent>()
    val spinEvents: SharedFlow<SpinEvent> = _spinEvents.asSharedFlow()

    val spinLeaderboard: StateFlow<List<SpinResultEntity>> = wheelDao.getRecentLeaderboardResults()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val leaderboardEntries: StateFlow<List<LeaderboardEntry>> = wheelDao.getLeaderboardEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            val existing = wheelDao.getWallet().first()
            if (existing == null) {
                wheelDao.upsertWallet(WalletEntity())
            }
        }

        viewModelScope.launch {
            wheelDao.getWallet().collect { wallet ->
                val balance = wallet?.coinBalance ?: INITIAL_COIN_BALANCE
                val ts = wallet?.lastRefillTimestamp ?: 0L
                val claimed = isWithin24Hours(ts)
                _uiState.update { state ->
                    if (state.isSpinning) {
                        state
                    } else {
                        state.copy(
                            coins = balance,
                            canSpin = canSpinCheck(balance, state.paidSpinsUsed, state.sessionComplete),
                            dailyRefillClaimed = claimed,
                            lastRefillTimestamp = ts
                        )
                    }
                }
            }
        }
    }

    private fun canSpinCheck(balance: Int, paidSpinsUsed: Int, sessionComplete: Boolean): Boolean {
        if (sessionComplete) return false
        if (paidSpinsUsed >= MAX_SPINS_PER_SESSION) return false
        return balance >= SPIN_COST
    }

    fun spin() {
        val state = _uiState.value
        if (!state.canSpin || state.isSpinning) return
        if (state.sessionComplete) return
        if (state.paidSpinsUsed >= MAX_SPINS_PER_SESSION) return
        if (state.coins < SPIN_COST) return

        val result = WheelEngine.spinWheel()
        val targetAngle = WheelEngine.getSegmentAngle(result)
        val currentMod = normalizeDegrees(rotation.value)
        val absoluteTarget = landingRotation(targetAngle)
        val rotationNeeded = normalizeDegrees(absoluteTarget - currentMod)
        val target = rotation.value + (FULL_ROTATIONS * 360f) + rotationNeeded

        val deductedCoins = state.coins - SPIN_COST
        val newSpinsUsed = state.paidSpinsUsed + 1
        val sessionDone = newSpinsUsed >= MAX_SPINS_PER_SESSION

        _uiState.update {
            it.copy(
                coins = deductedCoins,
                isSpinning = true,
                canSpin = false,
                paidSpinsUsed = newSpinsUsed
            )
        }
        viewModelScope.launch {
            wheelDao.updateCoinBalance(deductedCoins)
            _spinEvents.emit(SpinEvent(target, result))
        }
    }

    fun onAnimationComplete(result: WheelSegment) {
        viewModelScope.launch { onSpinComplete(result) }
    }

    fun startNewSession() {
        currentSessionId = System.currentTimeMillis()
        _uiState.update {
            WheelUiState(
                coins = INITIAL_COIN_BALANCE,
                canSpin = true,
                paidSpinsUsed = 0,
                sessionComplete = false,
                sessionCoinsWon = 0
            )
        }
        viewModelScope.launch {
            wheelDao.updateCoinBalance(INITIAL_COIN_BALANCE)
        }
    }

    fun refill() {
        if (_uiState.value.dailyRefillClaimed) return
        val now = System.currentTimeMillis()
        val newBalance = _uiState.value.coins + DAILY_REFILL_AMOUNT
        _uiState.update {
            it.copy(
                coins = newBalance,
                canSpin = canSpinCheck(newBalance, it.paidSpinsUsed, it.sessionComplete),
                dailyRefillClaimed = true,
                lastRefillTimestamp = now
            )
        }
        viewModelScope.launch {
            wheelDao.updateCoinBalance(newBalance)
            wheelDao.updateLastRefillTimestamp(now)
        }
    }

    fun updatePlayerName(name: String) {
        _uiState.update { it.copy(playerName = name) }
    }

    fun submitSession(name: String) {
        viewModelScope.launch {
            wheelDao.updateSessionPlayerName(currentSessionId, name)
            startNewSession()
        }
    }

    private suspend fun onSpinComplete(result: WheelSegment) {
        val state = _uiState.value
        val newBalance = state.coins + result.coinReward
        val sessionDone = state.paidSpinsUsed >= MAX_SPINS_PER_SESSION
        val newSessionCoins = state.sessionCoinsWon + result.coinReward

        wheelDao.updateCoinBalance(newBalance)
        wheelDao.insertSpinResult(
            SpinResultEntity(
                playerName = "Anonymous", // Placeholder until session submission
                segmentName = result.name,
                coinsWon = result.coinReward,
                timestamp = System.currentTimeMillis(),
                sessionId = currentSessionId
            )
        )
        _uiState.update {
            it.copy(
                coins = newBalance,
                isSpinning = false,
                lastResult = result,
                showWinBanner = true,
                canSpin = if (sessionDone) false else canSpinCheck(newBalance, it.paidSpinsUsed, sessionDone),
                currentBackground = resolveBackground(result),
                sessionComplete = sessionDone,
                sessionCoinsWon = newSessionCoins
            )
        }
        delay(WIN_BANNER_DURATION_MS)
        _uiState.update { it.copy(showWinBanner = false) }
    }

    private fun resolveBackground(result: WheelSegment): Int = when (result) {
        WheelSegment.DIAMOND -> R.drawable.plin_back_1
        WheelSegment.CROWN, WheelSegment.STAR -> R.drawable.plin_back_2
        WheelSegment.BELL -> R.drawable.plin_back_3
        WheelSegment.CLOVER, WheelSegment.COIN -> R.drawable.plin_back_4
        WheelSegment.SPARK -> R.drawable.plin_back_5
    }

    private fun landingRotation(targetAngle: Float): Float {
        // PrizeWheel draws segment 0 starting at -90 degrees (top pointer).
        // To place 'targetAngle' under the pointer, we rotate backwards by targetAngle.
        return normalizeDegrees(360f - targetAngle)
    }

    private fun normalizeDegrees(value: Float): Float {
        val normalized = value % 360f
        return if (normalized < 0f) normalized + 360f else normalized
    }

    private fun isWithin24Hours(timestamp: Long): Boolean {
        if (timestamp == 0L) return false
        return (System.currentTimeMillis() - timestamp) < 24 * 3_600_000L
    }

    companion object {
        const val SPIN_COST = 50
        const val MAX_SPINS_PER_SESSION = 10
        const val DAILY_REFILL_AMOUNT = 100
        private const val FULL_ROTATIONS = 5
        private const val WIN_BANNER_DURATION_MS = 2_000L
    }
}
