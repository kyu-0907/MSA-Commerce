package com.example.user.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

    @ExceptionHandler(UserProfileNotFoundException::class)
    fun handleNotFound(e: UserProfileNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse("USER_PROFILE_NOT_FOUND", e.message.orEmpty()))

    @ExceptionHandler(UserProfileAlreadyExistsException::class)
    fun handleAlreadyExists(e: UserProfileAlreadyExistsException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse("USER_PROFILE_ALREADY_EXISTS", e.message.orEmpty()))

    @ExceptionHandler(NicknameAlreadyExistsException::class)
    fun handleNicknameAlreadyExists(e: NicknameAlreadyExistsException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse("NICKNAME_ALREADY_EXISTS", e.message.orEmpty()))
}
