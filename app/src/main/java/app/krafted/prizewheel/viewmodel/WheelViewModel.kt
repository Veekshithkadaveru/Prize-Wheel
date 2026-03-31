package app.krafted.prizewheel.viewmodel

import androidx.compose.animation.core.Animatable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.prizewheel.R
import app.krafted.prizewheel.data.INITIAL_COIN_BALANCE
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
    val currentBackground: Int = R.drawable.plin_back_5
)

data class SpinEvent(val targetRotation: Float, val result: WheelSegment)

class WheelViewModel(private val wheelDao: WheelDao) : ViewModel() {
    val rotation = Animatable(0f)

    private val _uiState = MutableStateFlow(WheelUiState())
    val uiState: StateFlow<WheelUiState> = _uiState.asStateFlow()

    private val _spinEvents = MutableSharedFlow<SpinEvent>()
    val spinEvents: SharedFlow<SpinEvent> = _spinEvents.asSharedFlow()

    val spinHistory: StateFlow<List<SpinResultEntity>> = wheelDao.getRecentSpinResults()
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
                _uiState.update { state ->
                    if (state.isSpinning) {
                        state
                    } else {
                        state.copy(
                            coins = balance,
                            canSpin = balance >= SPIN_COST
                        )
                    }
                }
            }
        }
    }

    fun spin() {
        val state = _uiState.value
        if (!state.canSpin || state.isSpinning) return
        if (state.coins < SPIN_COST) return

        val result = WheelEngine.spinWheel()
        val targetAngle = WheelEngine.getSegmentAngle(result)
        val target = rotation.value + (FULL_ROTATIONS * 360f) + landingRotation(targetAngle)

        val deductedCoins = state.coins - SPIN_COST
        _uiState.update {
            it.copy(
                coins = deductedCoins,
                isSpinning = true,
                canSpin = false
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

    fun refill() {
        _uiState.update {
            it.copy(
                coins = INITIAL_COIN_BALANCE,
                canSpin = true
            )
        }
        viewModelScope.launch {
            wheelDao.updateCoinBalance(INITIAL_COIN_BALANCE)
        }
    }

    private suspend fun onSpinComplete(result: WheelSegment) {
        val newBalance = _uiState.value.coins + result.coinReward
        wheelDao.updateCoinBalance(newBalance)
        wheelDao.insertSpinResult(
            SpinResultEntity(
                segmentName = result.name,
                coinsWon = result.coinReward,
                timestamp = System.currentTimeMillis()
            )
        )
        _uiState.update {
            it.copy(
                coins = newBalance,
                isSpinning = false,
                lastResult = result,
                showWinBanner = true,
                canSpin = newBalance >= SPIN_COST,
                currentBackground = resolveBackground(result)
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
        val pointerAngle = 270f
        return normalizeDegrees(pointerAngle - targetAngle)
    }

    private fun normalizeDegrees(value: Float): Float {
        val normalized = value % 360f
        return if (normalized < 0f) normalized + 360f else normalized
    }

    companion object {
        const val SPIN_COST = 10
        private const val FULL_ROTATIONS = 5
        private const val SPIN_DURATION_MS = 3_500
        private const val WIN_BANNER_DURATION_MS = 2_000L
    }
}
