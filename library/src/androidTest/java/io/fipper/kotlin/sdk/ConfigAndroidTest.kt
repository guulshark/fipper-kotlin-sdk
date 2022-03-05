package io.fipper.kotlin.sdk

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.fipper.kotlin.sdk.internal.ConfigResponse
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConfigAndroidTest {

    private val payload = """
    {
        "config": {
            "production": "H4sIAKw1V2EC/6tWSssvLVKyUqhWKi5JLEkFskqKSlN1FJRKKgtAPBMDILssMacUxFGqjlEqSS0uiQGyDY2Ma5VqQQozilJT8ZhgjGJCCFA7RFt5Ph5NRsiaDE1AGkoL0osSU/DZZIisCSRVWwsA+36n0OAAAAA="
        },
        "eTag": "e1d186b87842e449a2f0eea5d9f205f9460ad09b"
    }
    """

    private val failedPayload = """
    {
        "eTag": "e1d186b87842e449a2f0eea5d9f205f9460ad09b"
    }
    """

    @Test
    fun succeedParseConfig() {
        runCatching {
            ConfigResponse.fromJson(payload)
        }.onSuccess {
            Assert.assertEquals(it.eTag, "e1d186b87842e449a2f0eea5d9f205f9460ad09b")
            Assert.assertNotNull(it.configs["production"])
            Assert.assertNull(it.configs["development"])

            val config = it.configs["production"]!!
            config.forEach { flag ->
                when (flag.name) {
                    "upgrade" -> {
                        Assert.assertTrue("Invalid flag type", flag is Flag.BoolFlag)
                        (flag as Flag.BoolFlag).run {
                            Assert.assertEquals(available, true)
                            Assert.assertEquals(value, true)
                        }
                    }
                    "two" -> {
                        Assert.assertTrue("Invalid flag type", flag is Flag.IntFlag)
                        (flag as Flag.IntFlag).run {
                            Assert.assertEquals(available, true)
                            Assert.assertEquals(value, 14)
                        }
                    }
                    "three" -> {
                        Assert.assertTrue("Invalid flag type", flag is Flag.StrFlag)
                        (flag as Flag.StrFlag).run {
                            Assert.assertEquals(available, true)
                            Assert.assertEquals(value, "Test")
                        }
                    }
                    "four" -> {
                        Assert.assertTrue("Invalid flag type", flag is Flag.JsonFlag)
                        (flag as Flag.JsonFlag).run {
                            Assert.assertEquals(available, true)
                            Assert.assertEquals(value, "{\"test\": 123}")
                        }
                    }
                }
            }
        }.onFailure {
            Assert.fail("Invalid payload")
        }
    }

    @Test
    fun failedParseConfig() {
        runCatching {
            ConfigResponse.fromJson(failedPayload)
        }.onFailure {
            Assert.assertTrue("Invalid failure type", it is FipperFailure)
        }.onSuccess {
            Assert.fail("Invalid payload")
        }
    }
}