package io.fipper.kotlin.sdk

interface FipperCallback {
    fun onSuccess(flags: List<Flag>)
    fun onFailure(failure: FipperFailure)
}