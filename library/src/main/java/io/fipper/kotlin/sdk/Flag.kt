package io.fipper.kotlin.sdk

sealed class Flag {

    abstract val name: String
    abstract val available: Boolean

    data class BoolFlag(
        override val name: String,
        override val available: Boolean,
        val value: Boolean
    ) : Flag()

    data class IntFlag(
        override val name: String,
        override val available: Boolean,
        val value: Int
    ) : Flag()

    data class StrFlag(
        override val name: String,
        override val available: Boolean,
        val value: String
    ) : Flag()

    data class JsonFlag(
        override val name: String,
        override val available: Boolean,
        val value: String
    ) : Flag()
}