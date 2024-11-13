package com.kasymzhan.quest.processor.service

import com.kasymzhan.quest.processor.config.JwtConfig
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtTokenService(private val jwtConfig: JwtConfig) {
    private val secretKey = Keys.hmacShaKeyFor(
        jwtConfig.secret.toByteArray()
    )

    private val expirationDate: Date
        get() = Date(Date().time + jwtConfig.expiration)

    fun generate(
        userDetails: UserDetails, expirationDate: Date, additionalClaims: Map<String, Any> = emptyMap()
    ): String = Jwts.builder()
        .claims()
        .subject(userDetails.username)
        .issuedAt(currentTime())
        .expiration(expirationDate)
        .add(additionalClaims)
        .and().signWith(secretKey).compact()

    fun generate(userId: String, expirationDate: Date, additionalClaims: Map<String, Any> = emptyMap()): String =
        Jwts.builder()
            .claims()
            .subject(userId)
            .issuedAt(currentTime())
            .expiration(expirationDate)
            .add(additionalClaims)
            .and().signWith(secretKey).compact()

    fun generate(userDetails: UserDetails, additionalClaims: Map<String, Any> = emptyMap()) =
        generate(userDetails, expirationDate, additionalClaims)

    fun generate(userId: String, additionalClaims: Map<String, Any> = emptyMap()) =
        generate(userId, expirationDate, additionalClaims)

    fun tryParseToken(request: HttpServletRequest): String? {
        val authHeader: String? = request.getHeader("Authorization")
        if (!authHeader.containsToken())
            return null
        val token = authHeader!!.extractToken()
        return token
    }

    fun isValid(token: String?): Boolean {
        if (token.isNullOrBlank())
            return false
        try {
            getAllClaims(token)
            return true
        } catch (e: Exception) {
            println("exception: $e")
            return false
        }
    }

    fun getAllClaims(token: String): Claims {
        val decoder = Jwts.parser().verifyWith(secretKey).build()
        return decoder.parseSignedClaims(token).payload
    }

    private fun currentTime() = Date()

    private fun String?.containsToken() =
        this != null && this.startsWith("Bearer ")

    private fun String.extractToken(): String =
        this.substringAfter("Bearer ")
}