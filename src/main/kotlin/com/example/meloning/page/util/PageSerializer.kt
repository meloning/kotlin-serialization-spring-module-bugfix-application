package com.example.meloning.page.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl

class PageSerializer<T>(
    private val dataSerializer: KSerializer<List<T>>
) : KSerializer<Page<T>> {
    override val descriptor: SerialDescriptor = dataSerializer.descriptor
    override fun serialize(encoder: Encoder, value: Page<T>) = dataSerializer.serialize(encoder, value.content)
    override fun deserialize(decoder: Decoder) = PageImpl(dataSerializer.deserialize(decoder))
}
