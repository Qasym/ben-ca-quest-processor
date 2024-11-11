package com.kasymzhan.quest.processor.data

data class Quest(
    val id: String,
    val rewardId: String,
    val autoClaim: Boolean,
    val streak: Int,
    val duplication: Int,
    val name: String,
    val description: String,
)
