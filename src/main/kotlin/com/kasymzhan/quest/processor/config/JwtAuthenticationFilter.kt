package com.kasymzhan.quest.processor.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import com.kasymzhan.quest.processor.service.JwtTokenService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource

@Component
class JwtAuthenticationFilter(private val tokenService: JwtTokenService) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = tokenService.tryParseToken(request)
        if (tokenService.isValid(token)) {
            val claims = tokenService.getAllClaims(token!!)
            val roles = token.getRoles()
            val auth = UsernamePasswordAuthenticationToken(claims.subject, null, roles)
            auth.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = auth
        }
        filterChain.doFilter(request, response)
    }

    private fun String.getRoles(): List<SimpleGrantedAuthority> {
        val claims = tokenService.getAllClaims(this)
        return try {
            val roles = claims["roles"] as List<*>
            roles.mapNotNull {
                val roleName = (it as? Map<*, *>)?.get("authority") as? String
                roleName?.let { SimpleGrantedAuthority("$roleName") }
            }
        } catch (e: Exception) {
            println("exception: $e")
            emptyList()
        }
    }
}