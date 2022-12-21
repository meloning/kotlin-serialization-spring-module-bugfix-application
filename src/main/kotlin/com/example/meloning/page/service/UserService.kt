package com.example.meloning.page.service

import com.example.meloning.page.dto.UserDto
import com.example.meloning.page.repository.UserEntityRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userEntityRepository: UserEntityRepository
) {

    @Transactional(readOnly = true)
    fun getUsers(pageable: Pageable): Page<UserDto> {
        val result = userEntityRepository.findAll(pageable).map { UserDto.from(it) }
        return result
    }
}