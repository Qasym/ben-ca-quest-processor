package com.kasymzhan.quest.processor.controller

import com.kasymzhan.quest.processor.data.Quest
import com.kasymzhan.quest.processor.data.Reward
import com.kasymzhan.quest.processor.data.Status
import com.kasymzhan.quest.processor.data.UserReward
import com.kasymzhan.quest.processor.repository.UserRewardRepository
import com.kasymzhan.quest.processor.service.JwtTokenService
import jakarta.servlet.http.HttpServletRequest
import org.bson.types.ObjectId
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@RestController
@RequestMapping
class UserRewardController(
    private val userRewardRepository: UserRewardRepository,
    val webClient: WebClient,
    val tokenService: JwtTokenService,
) {
    @PostMapping("/track/registration/{id}")
    fun trackNewUser(@PathVariable id: String, http: HttpServletRequest): ResponseEntity<String> {
        val token = getToken(http)
        if (!tokenService.isValid(token))
            return ResponseEntity("Invalid token!", HttpStatus.FORBIDDEN)
        var quests = getQuests(token!!)
        val allUserRewards = userRewardRepository.findByUserId(id)
        quests = quests.filter { quest -> // filter quests that user(id) does not have
            !allUserRewards.any { it.quest.id == quest.id }
        }
        if (quests.isEmpty())
            return ResponseEntity("User already has all quests", HttpStatus.OK)
        val userRewards = quests.map {
            UserReward(
                id = ObjectId(),
                userId = id,
                quest = it,
                _status = Status.NOT_CLAIMED,
            )
        }
        userRewards.forEach { userRewardRepository.save(it) }
        trackAction(id, "registration")
        return ResponseEntity("Registered new user $id", HttpStatus.OK)
    }

    @PostMapping("/track/{id}/{action}")
    fun trackAction(@PathVariable id: String, @PathVariable action: String): ResponseEntity<String> {
        val userQuests = userRewardRepository.findByUserId(id).map { it.quest }
        if (userQuests.isEmpty())
            return ResponseEntity("No quests for this user", HttpStatus.I_AM_A_TEAPOT)
        userQuests.forEach {
            if (it.name == action) {
                println("Supposed to reward user!!! >Not Yet Implemented<")
//                it.claim(id, ::rewardUser) // read rewardUser method comments
            }
        }
        return ResponseEntity("successfully tracked $action", HttpStatus.OK)
    }

    @PostMapping("/link/{userId}/{questId}")
    fun linkQuestUser(@PathVariable userId: String, @PathVariable questId: String): ResponseEntity<String> {
        return ResponseEntity("Linked $userId to $questId", HttpStatus.OK)
    }

    @GetMapping("/users/{id}")
    fun getUserRewards(@PathVariable id: String): List<UserReward> =
        userRewardRepository.findByUserId(id)

    @GetMapping("/users/all")
    fun getAllUsersRewards(): List<UserReward> =
        userRewardRepository.findAll()

    private fun getQuests(token: String): List<Quest> =
        webClient.get().uri("http://localhost:2003/quests")
            .header("Authorization", "Bearer $token")
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<Quest>>() {})
            .block() ?: emptyList()

    private fun getToken(http: HttpServletRequest): String? =
        tokenService.tryParseToken(http)

    // This function should not be used
    // as this microservice doesn't have access to username
    // which is necessary for proper token generation
    // todo: Improve on microservices design and predict this kind of situations before they happen
    private fun rewardUser(userId: String, reward: Reward) {
        val url = "http://localhost:2001/reward/$userId"
        val roles = mapOf("roles" to listOf("ADMIN")) // generate admin role
        val token = tokenService // token lives 10 seconds
            .generate(userId, Date(Date().time + 10000), roles)
        webClient.post()
            .uri(url)
            .header("Authorization", "Bearer $token")
            .bodyValue(reward)
            .retrieve()
            .toBodilessEntity()
            .subscribe()
    }
}