package com.kasymzhan.quest.processor.data

data class Quest(
    val id: String,
    val rewardId: String,
    val autoClaim: Boolean,
    var streak: Int,
    var duplication: Int,
    val name: String,
    val description: String,
) {
    val initialStreak: Int = streak

    fun claim(onClaim: () -> Unit) {
        if (streak == 0 || duplication == 0)
            return
        streak--
        if (streak == 0) {
            if (duplication > 0) {
                duplication--
                streak = initialStreak
            }
            onClaim()
        }
    }
}
