package com.example.meloning.page.dto

import com.example.meloning.page.entity.UserEntity
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val name: String,
    val phone: String,
    @SerialName("isActive")
    @JsonProperty("isActiveJackson")
    val active: Boolean,
    val createdAt: String?,
    val updatedAt: String?
) {
    companion object {
        @JvmStatic
        fun from(userEntity: UserEntity) =
            with(userEntity) {
                UserDto(
                    id = id!!,
                    name = name,
                    phone = phone,
                    active = active,
                    createdAt = createdAt?.toString(),
                    updatedAt = updatedAt?.toString()
                )
            }
    }
}