package com.example.auth.exception

class EmailAlreadyExistsException(email: String) : RuntimeException("이미 가입된 이메일입니다: $email")

class InvalidOrExpiredCodeException : RuntimeException("인증 코드가 올바르지 않거나 만료되었습니다.")

class EmailNotVerifiedException(email: String) : RuntimeException("이메일 인증을 먼저 완료해주세요: $email")
