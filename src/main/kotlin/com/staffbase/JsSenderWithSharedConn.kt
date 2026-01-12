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
import io.nats.client.JetStream
import io.nats.client.Message
import io.nats.client.Nats
import io.nats.client.Options
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class JsSenderWithSharedConn(
    private val id: String,
    private val js: JetStream,
    private val msgs: List<Pair<String, String>>,
    private val sleepMs: Long = 1000L,
) {

    private val threadPool = Executors.newScheduledThreadPool(1)

    fun start() {
        println("$id start")
        threadPool.execute {
            sendAll()
        }
    }

    private fun sendAll() {
        for (msg in msgs) {
            try {
                js.publish(msg.first, msg.second.toByteArray())
                println("$id sent message to subject: ${msg.first}, data: ${msg.second}")
            } catch (e: Exception) {
                println("$id Failed to publish message to subject: ${msg.first}, data: ${msg.second}, error: ${e.message}")
            }
            Thread.sleep(sleepMs)
        }
    }

    fun stop() {
        println("$id stop")
    }
}