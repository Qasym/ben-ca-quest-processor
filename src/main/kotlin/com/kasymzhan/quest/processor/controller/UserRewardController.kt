package com.kasymzhan.quest.processor.controller

import com.kasymzhan.quest.processor.data.Quest
import com.kasymzhan.quest.processor.data.Status
import com.kasymzhan.quest.processor.data.UserReward
import com.kasymzhan.quest.processor.repository.UserRewardRepository
import jakarta.servlet.http.HttpServletRequest
import org.bson.types.ObjectId
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@RestController
@RequestMapping("/track")
class UserRewardController(
    private val userRewardRepository: UserRewardRepository,
    val webClient: WebClient,
) {
    @PostMapping("/registration/{id}")
    fun trackNewUser(@PathVariable id: String, http: HttpServletRequest): ResponseEntity<String> {
        val token = getToken(http)
        val quests = getQuests(token)
        val userRewards = quests.map {
            UserReward(
                id = ObjectId(),
                userId = id,
                questId = it.id,
                _status = Status.NOT_CLAIMED,
            )
        }
        userRewards.forEach { userRewardRepository.save(it) }
        return ResponseEntity("Registered new user $id", HttpStatus.OK)
    }

    @PostMapping("/user/{id}/{action}")
    fun trackAction(@PathVariable id: String, @PathVariable action: String): ResponseEntity<String> {
        return ResponseEntity("successfully tracked $action", HttpStatus.OK)
    }

    private fun getQuests(token: String): List<Quest> =
        webClient.get().uri("http://localhost:2003/quests/get/all")
            .header("Authorization", "Bearer $token")
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<Quest>>() {})
            .block() ?: emptyList()

    private fun getToken(http: HttpServletRequest): String {
        return ""
    }
}