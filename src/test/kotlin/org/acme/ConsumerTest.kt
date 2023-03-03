package org.acme

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import javax.inject.Inject

@QuarkusTest
class ConsumerTest {

    @Inject
    lateinit var consumer: Consumer

    @Test
    fun testConsumer() {
        assertEquals("cool", consumer.resolver.getInfo())
    }
}