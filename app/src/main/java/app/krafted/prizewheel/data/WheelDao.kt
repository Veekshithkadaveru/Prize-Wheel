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

    @Query("SELECT * FROM spin_results ORDER BY coinsWon DESC LIMIT :limit")
    fun getRecentLeaderboardResults(limit: Int = 20): Flow<List<SpinResultEntity>>

    @Query("SELECT playerName, SUM(coinsWon) as totalCoins FROM spin_results GROUP BY playerName ORDER BY totalCoins DESC LIMIT 20")
    fun getLeaderboardEntries(): Flow<List<LeaderboardEntry>>

    @Query("UPDATE spin_results SET playerName = :name WHERE sessionId = :sessionId")
    suspend fun updateSessionPlayerName(sessionId: Long, name: String)

    @Query("UPDATE wallet SET lastRefillTimestamp = :timestamp WHERE id = 1")
    suspend fun updateLastRefillTimestamp(timestamp: Long)
}
