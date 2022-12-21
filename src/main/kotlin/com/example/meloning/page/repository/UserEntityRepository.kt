package com.example.meloning.page.repository

import com.example.meloning.page.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserEntityRepository : JpaRepository<UserEntity, Long>
