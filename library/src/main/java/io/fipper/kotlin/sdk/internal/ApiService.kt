package io.fipper.kotlin.sdk.internal

import io.fipper.kotlin.sdk.FipperFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

internal class ApiService(
    private val token: String,
    private val projectId: Int
) {

    private val client = OkHttpClient()

    @Throws(FipperFailure::class)
    suspend fun fetchConfig(): ConfigResponse {
        return withContext(Dispatchers.IO) {
            val url = HttpUrl.Builder()
                .scheme("https")
                .host("sync2.fipper.io")
                .addPathSegment("config")
                .addQueryParameter("apiToken", token)
                .addQueryParameter("item", projectId.toString())
                .build()

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            runCatching {
                client.newCall(request).execute()
            }.mapCatching {
                if (!it.isSuccessful) {
                    throw FipperFailure.NetworkFailure(it.message, it.code)
                }

                it.body?.string() ?: throw FipperFailure.ConfigNotFound
            }.mapCatching {
                ConfigResponse.fromJson(it)
            }.getOrElse {
                when (it) {
                    is FipperFailure -> throw it
                    else -> throw FipperFailure.NetworkFailure(it.message ?: it.toString())
                }
            }
        }
    }

    @Throws(FipperFailure::class)
    suspend fun checkHash(eTag: String): Boolean {
        return withContext(Dispatchers.IO) {
            val url = HttpUrl.Builder()
                .scheme("https")
                .host("sync2.fipper.io")
                .addPathSegment("hash")
                .addQueryParameter("apiToken", token)
                .addQueryParameter("item", projectId.toString())
                .addQueryParameter("eTag", eTag)
                .build()

            val request = Request.Builder()
                .url(url)
                .head()
                .build()

            runCatching {
                client.newCall(request).execute()
            }.mapCatching {
                it.code == 304
            }.getOrElse {
                throw FipperFailure.NetworkFailure(it.message ?: it.toString())
            }
        }
    }
}