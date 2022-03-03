package io.fipper.kotlin.sdk

sealed class FipperFailure : Exception() {

    object ConfigNotFound : FipperFailure()

    data class NetworkFailure(
        override val message: String,
        val code: Int = 0
    ) : FipperFailure()

    object InvalidJson : FipperFailure()
}