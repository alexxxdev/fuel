import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.fuel.core.ResponseDeserializable
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class CoroutinesTest {

    init {
        FuelManager.instance.basePath = "https://httpbin.org"
        Fuel.testMode {
            timeout = 30000
        }
    }

    @Test
    fun testAwaitResponseReturnByteArray() = runBlocking {
        Fuel.get("/ip").awaitResponse().third
                .fold({ data ->
                    assertTrue(data.isNotEmpty())
                }, { error ->
                    fail("This test should pass but got an error: ${error.message}")
                })
    }

    @Test
    fun testAwaitResponseDoesNotThrowException() = runBlocking {
        try {
            Fuel.get("/error/404").awaitResponse().third.fold({
                fail("This should not be called")
            }, {

            })
        } catch (exception: Exception) {
            fail("This test should fail as exception should be caught")
        }
    }

    @Test
    fun testAwaitStringSuccess() = runBlocking {
        try {
            Fuel.get("/uuid").awaitStringResponse().third
                    .fold({ data ->
                        assertTrue(data.isNotEmpty())
                        assertTrue(data.contains("uuid"))
                    }, { error ->
                        fail("This test should pass but got an error: ${error.message}")
                    })
        } catch (exception: Exception) {
            fail("When using awaitString errors should be folded instead of thrown.")
        }
    }

    @Test
    fun testAwaitStringErrorDueToNetwork() = runBlocking {
        try {
            Fuel.get("/not/found/address").awaitStringResult().fold({
                fail("This test should fail due to HTTP status code.")
            }, { error ->
                assertTrue(error.exception is HttpException)
                assertTrue(error.message.orEmpty().contains("HTTP Exception 404"))
            })
        } catch (exception: HttpException) {
            fail("When using awaitString errors should be folded instead of thrown.")
        }
    }

    @Test
    fun testAwaitStringResponseDoesNotThrowException() = runBlocking {
        try {
            Fuel.get("/not/found/address").awaitStringResponse().third.fold({
                fail("This should not be called")
            }, {

            })
        } catch (exception: Exception) {
            fail("This test should fail as exception should be caught")
        }
    }

    @Test
    fun testAwaitForByteArrayResult() = runBlocking {
        Fuel.get("/ip").awaitByteArrayResult()
                .fold({ data ->
                    assertTrue(data.isNotEmpty())
                }, { error ->
                    fail("This test should pass but got an error: ${error.message}")
                })
    }

    @Test
    fun testAwaitResponseSuccess() = runBlocking {
        try {
            Fuel.get("/ip").awaitResponse().third
                    .fold({ data ->
                        assertTrue(data.isNotEmpty())
                    }, { error ->
                        fail("This test should pass but got an error: ${error.message}")
                    })
        } catch (exception: Exception) {
            fail("When using awaitResponse errors should be folded instead of thrown.")
        }
    }


    @Test
    fun testItCanAwaitString() = runBlocking {
        Fuel.get("/uuid").awaitStringResponse().third
                .fold({ data ->
                    assertTrue(data.isNotEmpty())
                    assertTrue(data.contains("uuid"))
                }, { error ->
                    fail("This test should pass but got an error: ${error.message}")
                })
    }

    @Test
    fun testAwaitResponseErrorDueToNetwork() = runBlocking {
        try {
            Fuel.get("/invalid/url").awaitResponse().third.fold({
                fail("This test should fail due to HTTP status code.")
            }, { error ->
                assertTrue(error.exception is HttpException)
                assertTrue(error.message!!.contains("HTTP Exception 404"))
            })
        } catch (exception: HttpException) {
            fail("When using awaitResponse errors should be folded instead of thrown.")
        }
    }

    @Test
    fun testItCanAwaitAnyObject() = runBlocking {
        Fuel.get("/uuid").awaitObjectResponse(UUIDResponseDeserializer).third
                .fold({ data ->
                    assertTrue(data.uuid.isNotEmpty())
                }, { error ->
                    fail("This test should pass but got an error: ${error.message}")
                })
    }

    @Test
    fun testItCanAwaitStringResult() = runBlocking {
        Fuel.get("/uuid").awaitStringResult()
                .fold({ data ->
                    assertTrue(data.isNotEmpty())
                    assertTrue(data.contains("uuid"))
                }, { error ->
                    fail("This test should pass but got an error: ${error.message}")
                })
    }

    @Test
    fun testItCanAwaitForObjectResult() = runBlocking {
        assertTrue(Fuel.get("/uuid").awaitObject(UUIDResponseDeserializer).uuid.isNotEmpty())
    }

    @Test
    fun testItCanAwaitResponseResult() = runBlocking {
        assertTrue(Fuel.get("/uuid").awaitByteArray().isNotEmpty())
    }


    @Test
    fun testAwaitObjectSuccess() = runBlocking {
        try {
            Fuel.get("/uuid").awaitObjectResult(UUIDResponseDeserializer)
                    .fold({ data ->
                        assertTrue(data.uuid.isNotEmpty())
                    }, { error ->
                        fail("This test should pass but got an error: ${error.message}")
                    })
        } catch (exception: HttpException) {
            fail("When using awaitObject network errors should be folded instead of thrown.")
        }
    }

    @Test
    fun testAwaitObjectErrorDueToNetwork() = runBlocking {
        try {
            Fuel.get("/not/uuid/endpoint").awaitObjectResult(UUIDResponseDeserializer).fold({
                fail("This test should fail due to HTTP status code.")
            }, { error ->
                assertTrue(error.exception is HttpException)
                assertTrue(error.message!!.contains("HTTP Exception 404"))
            })
        } catch (exception: HttpException) {
            fail("When using awaitObject errors should be folded instead of thrown.")
        }
    }

    @Test
    fun testAwaitObjectDueToDeserialization() = runBlocking {
        try {
            Fuel.get("/uuid").awaitStringResult().fold({
                fail("This test should fail because uuid property should be a String.")
            }, {
                fail("When using awaitObject serialization/deserialization errors are thrown.")
            })
        } catch (exception: JsonMappingException) {
            assertNotNull(exception)
        }
    }

    @Test
    fun testAwaitStringResultSuccess() = runBlocking {
        try {
            val data = Fuel.get("/uuid").awaitString()
            assertTrue(data.contains("uuid"))
        } catch (exception: Exception) {
            fail("This test should pass but got an exception: ${exception.message}")
        }
    }

    @Test
    fun testAwaitResponseResultSuccess() = runBlocking {
        try {
            val data = Fuel.get("/uuid").awaitByteArray()
            assertTrue(data.isNotEmpty())
        } catch (exception: Exception) {
            fail("This test should pass but got an exception: ${exception.message}")
        }
    }

    @Test
    fun testAwaitObjectResultSuccess() = runBlocking {
        try {
            val data = Fuel.get("/uuid").awaitObject(UUIDResponseDeserializer)
            assertTrue(data.uuid.isNotEmpty())
        } catch (exception: Exception) {
            fail("This test should pass but got an exception: ${exception.message}")
        }
    }

    @Test
    fun testAwaitObjectResultExceptionDueToNetwork() = runBlocking {
        try {
            Fuel.get("/some/invalid/path").awaitObject(UUIDResponseDeserializer)
            fail("This test should raise an exception due to invalid URL")
        } catch (exception: Exception) {
            assertNotNull(exception as? HttpException)
            assertTrue(exception.message.orEmpty().contains("404"))
        }
    }

    @Test
    fun testAwaitObjectResultExceptionDueToDeserialization() = runBlocking {
        try {
            Fuel.get("/uuid").awaitObject(UUIDResponseDeserializer)
            fail("This test should fail because uuid property should be a String.")
        } catch (exception: JsonMappingException) {
            assertNotNull(exception)
        }
    }

    @Test
    fun testItCanAwaitForStringResultCanThrowException() = runBlocking {
        try {
            Fuel.get("/error/404").awaitString()
            fail("This test should fail due to status code 404")
        } catch (exception: Exception) {
            assertNotNull(exception)
        }
    }

    @Test
    fun testAwaitForObjectResultPassesObject() = runBlocking {
        Fuel.get("/uuid").awaitObjectResult(UUIDResponseDeserializer)
                .fold({ data ->
                    assertTrue(data.uuid.isNotEmpty())
                }, { error ->
                    fail("This test should pass but got an error: ${error.message}")
                })
    }

    @Test
    fun testAwaitForObjectResultCatchesError() = runBlocking {
        try {
            Fuel.get("/error/404").awaitObjectResult(UUIDResponseDeserializer)
                    .fold({ _ ->
                        fail("This is an error case!")
                    }, { error ->
                        assertTrue(error.exception is HttpException)
                    })
        } catch (exception: Exception) {
            fail("When using awaitSafelyObjectResult errors should be folded instead of thrown.")
        }
    }

    @Test
    fun testAwaitForObjectResultCatchesDeserializeError() = runBlocking {
        try {
            Fuel.get("/ip").awaitObjectResult(UUIDResponseDeserializer)

                    .fold({ _ ->
                        fail("This is an error case!")

                    }, { error ->
                        assertNotNull(error)
                        assertTrue(error.exception is JsonMappingException)
                    })
        } catch (exception: Exception) {
            fail("When using awaitSafelyObjectResult errors should be folded instead of thrown.")
        }
    }


    private data class UUIDResponse(val uuid: String)

    private object UUIDResponseDeserializer : ResponseDeserializable<UUIDResponse> {
        override fun deserialize(content: String) =
                jacksonObjectMapper().readValue<UUIDResponse>(content)
    }
}

