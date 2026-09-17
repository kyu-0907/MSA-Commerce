package com.example.auth.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = e.bindingResult.fieldErrors.firstOrNull()?.defaultMessage
            ?: "요청값이 올바르지 않습니다."
        return ResponseEntity.badRequest().body(ErrorResponse("VALIDATION_ERROR", message))
    }

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExists(e: EmailAlreadyExistsException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse("EMAIL_ALREADY_EXISTS", e.message.orEmpty()))

    @ExceptionHandler(InvalidOrExpiredCodeException::class)
    fun handleInvalidCode(e: InvalidOrExpiredCodeException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest().body(ErrorResponse("INVALID_OR_EXPIRED_CODE", e.message.orEmpty()))

    @ExceptionHandler(EmailNotVerifiedException::class)
    fun handleEmailNotVerified(e: EmailNotVerifiedException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest().body(ErrorResponse("EMAIL_NOT_VERIFIED", e.message.orEmpty()))

    @ExceptionHandler(MailException::class)
    fun handleMailException(e: MailException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("MAIL_SEND_FAILED", "인증 메일 발송에 실패했습니다."))
}
