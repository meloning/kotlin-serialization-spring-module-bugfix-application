package com.example.meloning.page.config

import com.example.meloning.page.util.DomainSerializerFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PolymorphicKind.OPEN
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializerOrNull
import org.springframework.context.annotation.Configuration
import org.springframework.core.GenericTypeResolver
import org.springframework.core.ResolvableType
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractGenericHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.util.ConcurrentReferenceHashMap
import org.springframework.util.StreamUtils
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurer {

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.addAll(listOf(CustomKotlinSerializationJsonHttpMessageConverter(), MappingJackson2HttpMessageConverter()))
//        converters.addAll(listOf(KotlinSerializationJsonHttpMessageConverter(), MappingJackson2HttpMessageConverter()))
        println(converters)
    }

    abstract class CustomAbstractKotlinSerializationHttpMessageConverter<T : SerialFormat>(
        private var format: T, vararg supportedMediaTypes: MediaType
    ) : AbstractGenericHttpMessageConverter<Any>(*supportedMediaTypes) {

        override fun supports(clazz: Class<*>): Boolean {
            return serializer(clazz) != null
        }

        override fun canRead(type: Type, contextClass: Class<*>?, mediaType: MediaType?): Boolean {
            return if (serializer(GenericTypeResolver.resolveType(type, contextClass)) != null) {
                canRead(mediaType)
            } else {
                false
            }
        }

        override fun canWrite(type: Type?, clazz: Class<*>, mediaType: MediaType?): Boolean {
            return if (serializer(if (type != null) GenericTypeResolver.resolveType(type, clazz) else clazz) != null) {
                canWrite(mediaType)
            } else {
                false
            }
        }

        @Throws(IOException::class, HttpMessageNotReadableException::class)
        override fun read(type: Type, contextClass: Class<*>?, inputMessage: HttpInputMessage): Any {
            val resolvedType = GenericTypeResolver.resolveType(type, contextClass)
            val serializer = serializer(resolvedType)
                    ?: throw HttpMessageNotReadableException("Could not find KSerializer for $resolvedType", inputMessage)
            return readInternal(serializer, format, inputMessage)
        }

        @Throws(IOException::class, HttpMessageNotReadableException::class)
        override fun readInternal(clazz: Class<*>, inputMessage: HttpInputMessage): Any {
            val serializer = serializer(clazz)
                    ?: throw HttpMessageNotReadableException("Could not find KSerializer for $clazz", inputMessage)
            return readInternal(serializer, format, inputMessage)
        }

        /**
         * Reads the given input message with the given serializer and format.
         */
        @Throws(IOException::class, HttpMessageNotReadableException::class)
        protected abstract fun readInternal(serializer: KSerializer<Any>, format: T, inputMessage: HttpInputMessage): Any

        @Throws(IOException::class, HttpMessageNotWritableException::class)
        override fun writeInternal(`object`: Any, type: Type?, outputMessage: HttpOutputMessage) {
            val resolvedType = type?.let { ResolvableType.forType(it).type } ?: `object`.javaClass
            val serializer = serializer(resolvedType)
                    ?: throw HttpMessageNotWritableException("Could not find KSerializer for $resolvedType")
            writeInternal(`object`, serializer, format, outputMessage)
        }

        /**
         * Write the given object to the output message with the given serializer and format.
         */
        @Throws(IOException::class, HttpMessageNotWritableException::class)
        protected abstract fun writeInternal(`object`: Any, serializer: KSerializer<Any>, format: T,
                                             outputMessage: HttpOutputMessage)

        /**
         * Tries to find a serializer that can marshall or unmarshall instances of the given type
         * using kotlinx.serialization. If no serializer can be found, `null` is returned.
         *
         * Resolved serializers are cached and cached results are returned on successive calls.
         * @param type the type to find a serializer for
         * @return a resolved serializer for the given type, or `null`
         */
        private fun serializer(type: Type): KSerializer<Any>? {
            var serializer = serializerCache[type]
            if (serializer == null) {
                try {
                    serializer = serializerInternal(type)
                } catch (ignored: IllegalArgumentException) {
                }
                if (serializer != null) {
                    if (hasPolymorphism(serializer.descriptor, HashSet())) {
                        return null
                    }
                    serializerCache[type] = serializer
                }
            }
            return serializer
        }

        @Throws(IllegalArgumentException::class)
        protected abstract fun serializerInternal(type: Type): KSerializer<Any>?

        private fun hasPolymorphism(descriptor: SerialDescriptor, alreadyProcessed: MutableSet<String>): Boolean {
            alreadyProcessed.add(descriptor.serialName)
            if (descriptor.kind == OPEN) {
                return true
            }
            for (i in 0 until descriptor.elementsCount) {
                val elementDescriptor = descriptor.getElementDescriptor(i)
                if (!alreadyProcessed.contains(elementDescriptor.serialName) && hasPolymorphism(elementDescriptor, alreadyProcessed)) {
                    return true
                }
            }
            return false
        }

        companion object {
            private val serializerCache: MutableMap<Type, KSerializer<Any>> = ConcurrentReferenceHashMap()
        }
    }

    class CustomKotlinSerializationJsonHttpMessageConverter
        : CustomAbstractKotlinSerializationHttpMessageConverter<Json>(
            format = Json,
            supportedMediaTypes = arrayOf(MediaType.APPLICATION_JSON, MediaType("application", "*+json"))
        )
    {
        override fun readInternal(serializer: KSerializer<Any>, format: Json, inputMessage: HttpInputMessage): Any {
            val charset = charset(inputMessage.headers.contentType)
            val s = StreamUtils.copyToString(inputMessage.body, charset!!)
            return try {
                format.decodeFromString(serializer, s)
            } catch (ex: SerializationException) {
                throw HttpMessageNotReadableException("Could not read " + format + ": " + ex.message, ex,
                        inputMessage)
            }
        }

        override fun writeInternal(`object`: Any, serializer: KSerializer<Any>, format: Json, outputMessage: HttpOutputMessage) {
            try {
                val s = format.encodeToString(serializer, `object`)
                val charset = charset(outputMessage.headers.contentType)
                outputMessage.body.write(s.toByteArray(charset!!))
                outputMessage.body.flush()
            } catch (ex: SerializationException) {
                throw HttpMessageNotWritableException("Could not write " + format + ": " + ex.message, ex)
            }
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun serializerInternal(type: Type): KSerializer<Any>? {
            return when(type) {
                is ParameterizedType -> {
                    val rootClass = (type.rawType as Class<*>)
                    val args = (type.actualTypeArguments)
                    val argsSerializers = args.map { serializerOrNull(it) }
                    if (argsSerializers.isEmpty() && argsSerializers.first() == null) null
                    DomainSerializerFactory.create(rootClass, argsSerializers)
                }
                else -> serializerOrNull(type)
            }
        }

        companion object {
            @JvmStatic
            private fun charset(contentType: MediaType?): Charset? {
                return if (contentType != null && contentType.charset != null) {
                    contentType.charset
                } else StandardCharsets.UTF_8
            }
        }
    }
}
