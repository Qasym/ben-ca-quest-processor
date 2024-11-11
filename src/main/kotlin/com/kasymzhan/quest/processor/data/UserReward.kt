package com.kasymzhan.quest.processor.data

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "user_rewards")
data class UserReward(
    @Id
    val id: ObjectId,
    val userId: String,
    val questId: String,
    val status: Status,
    val dateReceived: Date
)

enum class Status(name: String) {
    CLAIMED("CLAIMED"),
    NOT_CLAIMED("NOT_CLAIMED");

    fun getName() = name
}
