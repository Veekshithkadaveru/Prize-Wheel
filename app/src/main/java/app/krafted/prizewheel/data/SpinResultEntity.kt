package app.krafted.prizewheel.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spin_results")
data class SpinResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerName: String = "Player",
    val segmentName: String,
    val coinsWon: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionId: Long = 0L
)
