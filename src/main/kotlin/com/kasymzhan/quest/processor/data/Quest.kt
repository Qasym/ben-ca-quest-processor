package com.kasymzhan.quest.processor.data

import kotlin.reflect.KFunction2

data class Quest(
    val id: String,
    val reward: Reward,
    val autoClaim: Boolean,
    var streak: Int,
    var duplication: Int,
    val name: String,
    val description: String,
    private val initialStreak: Int = streak,
) {
    fun claim(userId: String, onClaim: KFunction2<String, Reward, Unit>) {
        if (streak == 0 || duplication == 0)
            return
        streak--
        if (streak == 0) {
            if (duplication > 0) {
                duplication--
                streak = initialStreak
            }
            onClaim(userId, reward)
        }
    }
}
