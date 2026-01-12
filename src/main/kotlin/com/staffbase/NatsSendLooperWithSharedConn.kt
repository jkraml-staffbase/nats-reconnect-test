package com.staffbase

import io.nats.client.Connection
import io.nats.client.ConnectionListener
import io.nats.client.ConnectionListener.Events.CLOSED
import io.nats.client.ConnectionListener.Events.CONNECTED
import io.nats.client.ConnectionListener.Events.DISCONNECTED
import io.nats.client.ConnectionListener.Events.DISCOVERED_SERVERS
import io.nats.client.ConnectionListener.Events.LAME_DUCK
import io.nats.client.ConnectionListener.Events.RECONNECTED
import io.nats.client.ConnectionListener.Events.RESUBSCRIBED
import io.nats.client.Dispatcher
import io.nats.client.Message
import io.nats.client.Nats
import io.nats.client.Options
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class NatsSendLooperWithSharedConn(
    private val id: String,
    private val connection: Connection
) {

    private val threadPool = Executors.newScheduledThreadPool(1)
    private var future: ScheduledFuture<*>? = null

    fun start() {
        println("$id start")
         future = threadPool.scheduleWithFixedDelay({
            connection.publish(SUBJECT, "Hello from $id".toByteArray())
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