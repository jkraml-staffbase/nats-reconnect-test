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
import io.nats.client.Nats
import io.nats.client.Options

class NatsLooperWithOwnConn(
    private val connBuilder: Options.Builder,
    private val id: String,
) {
    private var connection: Connection? = null

    fun start() {
        val listener = EverythingListener(id)
        val adaptedOpts = Options.Builder(connBuilder.build())
            .connectionListener(listener)
            .errorListener(listener)
            .connectionName(id)
            .build()

        connection = Nats.connectReconnectOnConnect(adaptedOpts)
        println("$id Connected to NATS server: ${connection?.connectedUrl}")
    }

    fun stop() {
        connection?.close()
        println("$id NATS connection closed")
    }

}