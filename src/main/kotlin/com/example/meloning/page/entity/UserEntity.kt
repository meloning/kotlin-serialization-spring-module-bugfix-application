package com.example.meloning.page.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import org.hibernate.annotations.DynamicUpdate
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "user")
@DynamicUpdate
class UserEntity
private constructor(
    name: String,
    phone: String,
    active: Boolean
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "name", nullable = false)
    var name: String = name
        protected set

    @Column(name = "phone", nullable = false)
    var phone: String = phone
        protected set

    @Column(name = "active", nullable = false)
    var active: Boolean = active
        protected set

    companion object {
        @JvmStatic
        fun create(
            name: String,
            phone: String,
            active: Boolean = true
        ) = UserEntity(
            name = name,
            phone = phone,
            active = active
        )
    }
}