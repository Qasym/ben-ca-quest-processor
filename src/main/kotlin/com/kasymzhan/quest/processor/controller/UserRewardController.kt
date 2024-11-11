package com.kasymzhan.quest.processor.controller

import com.kasymzhan.quest.processor.repository.UserRewardRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/track")
class UserRewardController(
    private val userRewardRepository: UserRewardRepository,
) {
    @PostMapping("/registration/{id}")
    fun trackNewUser(@PathVariable id: String): ResponseEntity<String> {
        // todo:
        // 1. get all quests
        // 2. per each quest:
        //  2-1. create UserReward entry
        return ResponseEntity("Registered new user $id", HttpStatus.OK)
    }

    @PostMapping("/user/{id}/{action}")
    fun trackAction(@PathVariable id: String, @PathVariable action: String): ResponseEntity<String> {
        return ResponseEntity("successfully tracked $action", HttpStatus.OK)
    }
}