package io.fipper.kotlin.sdk

enum class Rate(val timeMillis: Long) {
    FREQUENTLY(3000),
    NORMAL(7000),
    RARELY(15000),
}