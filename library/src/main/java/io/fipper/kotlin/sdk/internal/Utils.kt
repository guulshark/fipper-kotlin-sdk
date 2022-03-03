package io.fipper.kotlin.sdk.internal

import android.util.Base64
import java.util.zip.GZIPInputStream

fun ByteArray.ungzip(): String {
    return GZIPInputStream(inputStream())
        .bufferedReader()
        .use { it.readText() }
}

fun String.b64decode(): ByteArray {
    return Base64.decode(
        toByteArray(),
        Base64.NO_PADDING or Base64.NO_WRAP
    )
}