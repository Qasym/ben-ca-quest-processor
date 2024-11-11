package com.kasymzhan.quest.processor.data

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "user_rewards")
data class UserReward(
    @Id
    val id: ObjectId,
    val userId: String,
    val quest: Quest,
    @field:JsonProperty("status")  // Ensures proper deserialization
    private var _status: Status,
    var dateReceived: Date? = null,
) {
    val status: Status
        get() = _status

    fun receiveReward() {
        _status = Status.CLAIMED
        dateReceived = Date()
    }
}

enum class Status(name: String) {
    CLAIMED("CLAIMED"),
    NOT_CLAIMED("NOT_CLAIMED");

    fun getName() = name
}
