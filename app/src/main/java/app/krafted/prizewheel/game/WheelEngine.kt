package app.krafted.prizewheel.game

import androidx.compose.ui.graphics.Color
import app.krafted.prizewheel.R
import kotlin.random.Random

enum class WheelSegment(
    val colour: Color,
    val coinReward: Int,
    val weight: Int,
    val symbolRes: Int
) {
    DIAMOND(Color(0xFF00E5FF), coinReward = 500, weight = 5, symbolRes = R.drawable.plin_sym_1),
    CROWN(Color(0xFFFFD700), coinReward = 200, weight = 10, symbolRes = R.drawable.plin_sym_2),
    STAR(Color(0xFFFF6B6B), coinReward = 100, weight = 15, symbolRes = R.drawable.plin_sym_3),
    BELL(Color(0xFF4CAF50), coinReward = 75, weight = 20, symbolRes = R.drawable.plin_sym_4),
    CLOVER(Color(0xFF9C27B0), coinReward = 50, weight = 20, symbolRes = R.drawable.plin_sym_5),
    COIN(Color(0xFFFF9800), coinReward = 25, weight = 20, symbolRes = R.drawable.plin_sym_6),
    SPARK(Color(0xFF607D8B), coinReward = 10, weight = 10, symbolRes = R.drawable.plin_sym_7)
}

object WheelEngine {
    private val totalWeight = WheelSegment.entries.sumOf { it.weight }

    fun spinWheel(): WheelSegment {
        val roll = Random.nextInt(totalWeight)
        var cumulative = 0
        for (segment in WheelSegment.entries) {
            cumulative += segment.weight
            if (roll < cumulative) return segment
        }
        return WheelSegment.SPARK
    }

    fun getSegmentAngle(segment: WheelSegment): Float {
        val segmentAngle = 360f / WheelSegment.entries.size
        return WheelSegment.entries.indexOf(segment) * segmentAngle + segmentAngle / 2f
    }
}
