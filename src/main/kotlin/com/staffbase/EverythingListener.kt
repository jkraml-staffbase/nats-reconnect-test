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
import io.nats.client.Consumer
import io.nats.client.ErrorListener
import io.nats.client.JetStreamSubscription
import io.nats.client.Message
import io.nats.client.support.Status
import java.lang.Exception

class EverythingListener(private val id: String) : ConnectionListener, ErrorListener {

    override fun connectionEvent(
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

    @Deprecated("Deprecated in Java")
    override fun connectionEvent(
        conn: Connection?,
        type: ConnectionListener.Events?
    ) {
    }

    override fun errorOccurred(conn: Connection?, error: String?) {
        println("$id NATS Error occurred on connection ${conn?.connectedUrl}: $error")
    }

    override fun exceptionOccurred(conn: Connection?, exp: Exception?) {
        println("$id NATS Exception occurred on connection ${conn?.connectedUrl}: $exp")
    }

    override fun slowConsumerDetected(conn: Connection?, consumer: Consumer?) {
        println("$id NATS Slow consumer detected on connection ${conn?.connectedUrl}: $consumer")
    }

    override fun messageDiscarded(conn: Connection?, msg: Message?) {
        println("$id NATS Message discarded on connection ${conn?.connectedUrl}: $msg")
    }

    override fun heartbeatAlarm(
        conn: Connection?,
        sub: JetStreamSubscription?,
        lastStreamSequence: Long,
        lastConsumerSequence: Long
    ) {
        println("$id NATS Heartbeat alarm on connection ${conn?.connectedUrl}: sub=$sub, lastStreamSequence=$lastStreamSequence, lastConsumerSequence=$lastConsumerSequence")
    }

    override fun unhandledStatus(
        conn: Connection?,
        sub: JetStreamSubscription?,
        status: Status?
    ) {
        println("$id NATS Unhandled status on connection ${conn?.connectedUrl}: sub=$sub, status=$status")
    }

    override fun pullStatusWarning(
        conn: Connection?,
        sub: JetStreamSubscription?,
        status: Status?
    ) {
        println("$id NATS Pull status warning on connection ${conn?.connectedUrl}: sub=$sub, status=$status")
    }

    override fun pullStatusError(
        conn: Connection?,
        sub: JetStreamSubscription?,
        status: Status?
    ) {
        println("$id NATS Pull status error on connection ${conn?.connectedUrl}: sub=$sub, status=$status")
    }

    override fun flowControlProcessed(
        conn: Connection?,
        sub: JetStreamSubscription?,
        subject: String?,
        source: ErrorListener.FlowControlSource?
    ) {
        println("$id NATS Flow control processed on connection ${conn?.connectedUrl}: sub=$sub, subject=$subject, source=$source")
    }

    override fun socketWriteTimeout(conn: Connection?) {
        println("$id NATS Socket write timeout on connection ${conn?.connectedUrl}")
    }
}