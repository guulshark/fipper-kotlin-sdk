package io.fipper.kotlin.sdk

import android.os.Handler
import android.os.Looper
import io.fipper.kotlin.sdk.internal.ApiService
import io.fipper.kotlin.sdk.internal.ConfigResponse
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

class Fipper(
    private val rate: Rate,
    token: String,
    projectId: Int,
    private val environment: String
) {

    private val service = ApiService(token, projectId)
    private val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    @Volatile
    private var cachedConfigResponse: ConfigResponse? = null
    @Volatile
    private var cachedTimeMillis: Long = 0L

    @Throws(FipperFailure::class)
    suspend fun getConfig(): List<Flag> {
        cachedConfigResponse?.let {
            val elapsed = System.currentTimeMillis() - cachedTimeMillis
            if (elapsed < rate.timeMillis) {
                return it.configs[environment] ?: emptyList()
            }

            runCatching {
                if (service.checkHash(it.eTag)) {
                    return it.configs[environment] ?: emptyList()
                }
            }
        }

        return runCatching {
            service.fetchConfig()
        }.mapCatching {
            cachedConfigResponse = it
            cachedTimeMillis = System.currentTimeMillis()
            it.configs[environment]  ?: throw FipperFailure.ConfigNotFound
        }.getOrThrow()
    }

    fun getConfig(callback: FipperCallback) {
        val callbackRef = WeakReference(callback)
        coroutineScope.launch {
            try {
                getConfig().let { flags ->
                    if (isActive) {
                        callbackRef.get()?.let {
                            handler.post {
                                it.onSuccess(flags)
                            }
                        }
                    }
                }
            } catch (ex: FipperFailure) {
                callbackRef.get()?.let {
                    handler.post {
                        it.onFailure(ex)
                    }
                }
            }
        }
    }

    fun release() {
        coroutineScope.cancel()
    }
}