package com.example.auth.service

import com.example.auth.exception.InvalidOrExpiredCodeException
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.time.Duration
import kotlin.random.Random

@Service
class EmailVerificationService(
    private val redisTemplate: StringRedisTemplate,
    private val mailSender: JavaMailSender,
    @Value("\${mail.verification.code-ttl-seconds}") private val codeTtlSeconds: Long,
    @Value("\${mail.verification.verified-ttl-seconds}") private val verifiedTtlSeconds: Long
) {

    fun sendVerificationCode(email: String) {
        val code = generateCode()
        redisTemplate.opsForValue().set(codeKey(email), code, Duration.ofSeconds(codeTtlSeconds))

        mailSender.send(
            SimpleMailMessage().apply {
                setTo(email)
                subject = "[MSA-Commerce] 이메일 인증 코드"
                text = "인증 코드는 $code 입니다. ${codeTtlSeconds / 60}분 이내에 입력해주세요."
            }
        )
    }

    fun verifyCode(email: String, code: String) {
        val savedCode = redisTemplate.opsForValue().get(codeKey(email))
            ?: throw InvalidOrExpiredCodeException()

        if (savedCode != code) {
            throw InvalidOrExpiredCodeException()
        }

        redisTemplate.delete(codeKey(email))
        redisTemplate.opsForValue().set(verifiedKey(email), "true", Duration.ofSeconds(verifiedTtlSeconds))
    }

    fun isVerified(email: String): Boolean =
        redisTemplate.hasKey(verifiedKey(email))

    fun clearVerified(email: String) {
        redisTemplate.delete(verifiedKey(email))
    }

    private fun generateCode(): String = Random.nextInt(100000, 1000000).toString()

    private fun codeKey(email: String) = "email-verification:$email"

    private fun verifiedKey(email: String) = "email-verified:$email"
}
