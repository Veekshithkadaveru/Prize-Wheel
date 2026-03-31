package app.krafted.prizewheel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.prizewheel.data.INITIAL_COIN_BALANCE
import app.krafted.prizewheel.data.WalletEntity
import app.krafted.prizewheel.data.WheelDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CoinViewModel(private val wheelDao: WheelDao) : ViewModel() {
    val wallet: StateFlow<WalletEntity?> = wheelDao.getWallet()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val coinBalance: StateFlow<Int> = wallet
        .map { it?.coinBalance ?: INITIAL_COIN_BALANCE }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), INITIAL_COIN_BALANCE)

    init {
        viewModelScope.launch {
            val existing = wheelDao.getWallet().first()
            if (existing == null) {
                wheelDao.upsertWallet(WalletEntity())
            }
        }
    }
}
