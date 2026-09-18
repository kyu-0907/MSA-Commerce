package com.example.user.exception

class UserProfileNotFoundException(authUserId: Long) :
    RuntimeException("사용자 프로필을 찾을 수 없습니다: $authUserId")

class UserProfileAlreadyExistsException(authUserId: Long) :
    RuntimeException("이미 프로필이 존재하는 사용자입니다: $authUserId")

class NicknameAlreadyExistsException(nickname: String) :
    RuntimeException("이미 사용 중인 닉네임입니다: $nickname")
