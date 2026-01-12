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

class NatsRpcWithSharedConn(
    private val id: String,
    private val connection: Connection
) {
    private var dispatcher: Dispatcher? = null

    fun start() {
        println("$id start")
        dispatcher = connection.createDispatcher(this@NatsRpcWithSharedConn::handleRpcCall)
        dispatcher?.subscribe(RPC_SUBJECT)
    }

    fun stop() {
        println("$id stop")
        dispatcher?.unsubscribe(RPC_SUBJECT)
    }

    private fun handleRpcCall(msg: Message) {
        println("$id RPC call received on subject: ${msg.subject}, replyTo: ${msg.replyTo}, data: ${String(msg.data)}")
        connection.publish(msg.replyTo, "Hi!".toByteArray())
    }

    companion object {
        const val RPC_SUBJECT = "rpc_call"
    }
}