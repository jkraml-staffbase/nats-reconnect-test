package com.staffbase

import io.nats.client.JetStream
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class JsSendLooperWithSharedConn(
    private val id: String,
    private val js: JetStream
) {

    private val threadPool = Executors.newScheduledThreadPool(1)
    private var future: ScheduledFuture<*>? = null

    fun start() {
        println("$id start")
        future = threadPool.scheduleWithFixedDelay({
            try {
                js.publish(SUBJECT, "Hello from $id".toByteArray())
            } catch (e: Exception) {
                println("$id Failed to publish message: ${e.message}")
            }
        }, 0, 1, TimeUnit.SECONDS)
    }

    fun stop() {
        println("$id stop")
        future?.cancel(false)
    }

    companion object {
        const val SUBJECT = "foo"
    }
}