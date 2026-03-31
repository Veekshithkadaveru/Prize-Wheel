package app.krafted.prizewheel.data

import androidx.room.Entity
import androidx.room.PrimaryKey

const val INITIAL_COIN_BALANCE = 1000

@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey val id: Int = 1,
    val coinBalance: Int = INITIAL_COIN_BALANCE
)
