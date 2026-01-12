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

class NatsLooper(
    private val connOpts: Options,
    private val id: String,
) {
    private var connection: Connection? = null

    fun start() {
        val adaptedOpts = Options.Builder(connOpts)
            .connectionListener(object : ConnectionListener {
                override fun connectionEvent(
                    conn: Connection?,
                    type: ConnectionListener.Events?,
                    time: Long?,
                    uriDetails: String?
                ) {
                    this@NatsLooper.connectionEvent(conn, type, time, uriDetails)
                }

                @Deprecated("Deprecated in Java")
                override fun connectionEvent(
                    conn: Connection?,
                    type: ConnectionListener.Events?
                ) {}
            })
            .build()

        while (true) {
            // handle starting this program before NATS server is available
            try {
                connection = Nats.connect(adaptedOpts)
                break
            } catch (e: Exception) {
                println("$id Failed to connect to NATS server: ${e.message}. Retrying in 1s...")
                Thread.sleep(1000)
            }
        }
        println("$id Connected to NATS server: ${connection?.connectedUrl}")
    }

    fun stop() {
        connection?.close()
        println("$id NATS connection closed")
    }

    private fun connectionEvent(
        conn: Connection?,
        type: ConnectionListener.Events?,
        time: Long?,
        uriDetails: String?
    ) {
        println("$id Connection Event: time: $time, type: '$type', uriDetails: $uriDetails")
        when (type) {
            CONNECTED -> {
                println("$id Connecting to NATS server successful. ConnectedURL: ${conn?.connectedUrl}")
            }

            RECONNECTED -> {
                println("$id Reconnecting to NATS server successful. ConnectedURL: ${conn?.connectedUrl}")
            }

            DISCONNECTED -> {
                println("$id NATS connection disconnected. ConnectedURL: ${conn?.connectedUrl}")
            }

            DISCOVERED_SERVERS -> {
                println("$id Discovered new NATS servers. ConnectedURL: ${conn?.connectedUrl}")
            }

            RESUBSCRIBED -> {
                println("$id Resubscribed to NATS subjects. ConnectedURL: ${conn?.connectedUrl}")
            }

            LAME_DUCK -> {
                println("$id NATS server is in lame duck mode. ConnectedURL: ${conn?.connectedUrl}")
            }

            CLOSED -> {
                println("$id The NATS connection was closed unrecoverably. ConnectedURL: ${conn?.connectedUrl}")
            }

            else -> {
                println("$id WTF")
            }
        }
    }
}