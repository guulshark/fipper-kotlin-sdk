package io.fipper.kotlin.sdk.internal

import io.fipper.kotlin.sdk.FipperFailure
import io.fipper.kotlin.sdk.Flag
import org.json.JSONException
import org.json.JSONObject
import kotlin.jvm.Throws

internal class ConfigResponse(
    val configs: Map<String, List<Flag>>,
    val eTag: String
) {

    companion object {

        @Throws(FipperFailure::class)
        fun fromJson(json: String): ConfigResponse {
            return runCatching {
                val jsonObject = JSONObject(json)
                val configsObject = jsonObject.getJSONObject("config")
                val configNames = configsObject.keys()
                val configs = mutableMapOf<String, List<Flag>>()
                while (configNames.hasNext()) {
                    val configName = configNames.next()
                    val ungzippedValue = configsObject.getString(configName)
                        .b64decode()
                        .ungzip()
                    val flagsObject = JSONObject(ungzippedValue)
                    val flagNames = JSONObject(ungzippedValue).keys()
                    val flags = mutableListOf<Flag>()
                    while (flagNames.hasNext()) {
                        val flagName = flagNames.next()
                        val flagObject = flagsObject.getJSONObject(flagName)
                        val flag = when (flagObject.getInt("type")) {
                            10 -> Flag.BoolFlag(
                                name = flagName,
                                available = flagObject.getBoolean("state"),
                                value = flagObject.getBoolean("value")
                            )
                            20 -> Flag.IntFlag(
                                name = flagName,
                                available = flagObject.getBoolean("state"),
                                value = flagObject.getInt("value")
                            )
                            30 -> Flag.StrFlag(
                                name = flagName,
                                available = flagObject.getBoolean("state"),
                                value = flagObject.getString("value")
                            )
                            40 -> Flag.JsonFlag(
                                name = flagName,
                                available = flagObject.getBoolean("state"),
                                value = flagObject.getString("value")
                            )
                            else -> throw JSONException("Unknown flag type")
                        }
                        flags.add(flag)
                    }
                    configs[configName] = flags
                }
                ConfigResponse(
                    configs = configs,
                    eTag = jsonObject.getString("eTag")
                )
            }.getOrElse {
                throw FipperFailure.InvalidJson
            }
        }
    }
}
