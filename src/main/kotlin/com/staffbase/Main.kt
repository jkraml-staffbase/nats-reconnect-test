package com.staffbase

import com.staffbase.NatsRpcWithOwnConn.Companion.RPC_SUBJECT
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
import java.time.Duration

object Main {


    @JvmStatic
    fun main(args: Array<String>) {

        val connBldr: Options.Builder = Options.Builder()
            .maxReconnects(-1) // -1 means infinite
            .server("nats://127.0.0.1:4222")
            .connectionTimeout(Duration.ofSeconds(10))
            // .socketReadTimeoutMillis(10000) -- this causes the connection to be closed when nothing is happening

        val nkey = "SUACSSL3UAHUDXKFSNVUZRF5UHPMWZ6BFDTJ7M6USDXIEDNPPQYYYCU3VY" // nkey from docs
        val nkeyHandler = Nats.staticCredentials(
            null,
            nkey.toCharArray()
        )
//        val nkeylistener = EverythingListener("nkey-conn")
//        val nkeyConnOpts = Options.Builder(connBldr.build()) // clone settings
//            .authHandler(nkeyHandler)
//            .connectionListener(nkeylistener)
//            .errorListener(nkeylistener)
//            .inboxPrefix("_INBOX_client")
//            .build()
//        val nkeyConn = Nats.connectReconnectOnConnect(nkeyConnOpts)

        val plainListener = EverythingListener("plain-conn")
        val plainConnOpts = Options.Builder(connBldr.build()) // clone settings
            .connectionListener(plainListener)
            .errorListener(plainListener)
            .build()
        val plainConn = Nats.connectReconnectOnConnect(plainConnOpts)

        val plainSender = NatsSendLooperWithSharedConn("Sender", plainConn)
//        val rpc1 = NatsRpcWithSharedConn("Rpc-1", nkeyConn,)
//        val nkeyJs = nkeyConn.jetStream()
//        val sendLooper1 = JsSendLooperWithSharedConn("Send-Looper-1", nkeyJs)
//        val sender = JsSenderWithSharedConn("Sender", plainConn.jetStream(), listOf(
//            "foo" to "foo1",
//            "bar" to "bar1",
//            "foo" to "foo2",
//        ))

        plainSender.start()
//        rpc1.start()
//        sendLooper1.start()
//        sender.start()

        // wait for ENTER key to exit
        println("Press ENTER to exit")
        readLine()

        plainSender.stop()
//        rpc1.stop()
//        sendLooper1.stop()
//        sender.stop()

        println("NATS connection closed")
    }

}