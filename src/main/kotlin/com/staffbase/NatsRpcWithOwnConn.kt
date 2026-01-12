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
import sun.tools.jconsole.Messages.CONNECTION

class NatsRpcWithOwnConn(
    private val connBuilder: Options.Builder,
    private val id: String,
    private val nkey: String?,
) {
    private var connection: Connection? = null
    private var dispatcher: Dispatcher? = null

    fun start() {
        val listener = EverythingListener(id)
        val adaptedBuilder = Options.Builder(connBuilder.build())
            .connectionListener(listener)
            .errorListener(listener)
        if (nkey != null) {
            val nkeyHandler = Nats.staticCredentials(
                null,
                nkey.toCharArray()
            )
            adaptedBuilder.authHandler(nkeyHandler)
        }
        val adaptedOpts = adaptedBuilder
            .connectionName(id)
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
        dispatcher = connection?.createDispatcher(this@NatsRpcWithOwnConn::handleRpcCall)
        dispatcher?.subscribe(RPC_SUBJECT)
    }

    fun stop() {
        dispatcher?.unsubscribe(RPC_SUBJECT)
        connection?.close()
        println("$id NATS connection closed")
    }

    private fun handleRpcCall(msg: Message) {
        println("$id RPC call received on subject: ${msg.subject}, replyTo: ${msg.replyTo}, data: ${String(msg.data)}")
        connection?.publish(msg.replyTo, "Hi!".toByteArray())
    }

    companion object {
        const val RPC_SUBJECT = "rpc_call"
    }
}