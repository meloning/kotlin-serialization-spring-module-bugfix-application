package com.example.meloning.page.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import org.springframework.data.domain.Page

object DomainSerializerFactory {

    @Suppress("UNCHECKED_CAST")
    fun create(rootClass: Class<*>, argsSerializers: List<KSerializer<Any>?>): KSerializer<Any>? {
        return when {
            Page::class.java.isAssignableFrom(rootClass) ->
                PageSerializer(ListSerializer(argsSerializers.first()!!.nullable)) as KSerializer<Any>?
            else -> null
        }
    }
}