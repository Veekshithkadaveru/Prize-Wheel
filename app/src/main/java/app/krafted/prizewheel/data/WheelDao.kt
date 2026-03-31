package app.krafted.prizewheel.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WheelDao {
    @Query("SELECT * FROM wallet WHERE id = 1")
    fun getWallet(): Flow<WalletEntity?>

    @Upsert
    suspend fun upsertWallet(wallet: WalletEntity)

    @Query("UPDATE wallet SET coinBalance = :balance WHERE id = 1")
    suspend fun updateCoinBalance(balance: Int)

    @Insert
    suspend fun insertSpinResult(result: SpinResultEntity)

    @Query("SELECT * FROM spin_results ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSpinResults(limit: Int = 20): Flow<List<SpinResultEntity>>
}
