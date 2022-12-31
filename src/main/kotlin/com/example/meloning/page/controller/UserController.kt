package com.example.meloning.page.controller

import com.example.meloning.page.dto.UserDto
import com.example.meloning.page.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/users")
    fun getUsers(@PageableDefault(page = 0, size = 5) pageable: Pageable): ResponseEntity<Page<UserDto>> {
        val result = userService.getUsers(pageable)
        return ResponseEntity.ok(result)
    }
}
