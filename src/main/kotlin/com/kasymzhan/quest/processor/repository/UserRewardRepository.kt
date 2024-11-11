package com.kasymzhan.quest.processor.repository

import com.kasymzhan.quest.processor.data.UserReward
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRewardRepository : MongoRepository<UserReward, ObjectId> {}