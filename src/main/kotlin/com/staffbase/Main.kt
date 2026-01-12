package com.staffbase

import io.nats.client.ConnectionListener.Events.*
import io.nats.client.Options
import java.time.Duration

object Main {



    @JvmStatic
    fun main(args: Array<String>) {
        val connOpts: Options = Options.Builder()
            .maxReconnects(-1) // -1 means infinite
            .server("nats://127.0.0.1:4222")
            .connectionTimeout(Duration.ofSeconds(10))
            // .socketReadTimeoutMillis(10000) -- this causes the connection to be closed when nothing is happening
            .build()

        val looper1 = NatsLooper(connOpts, "NatsLooper-1")
        looper1.start()
//        val looper2 = NatsLooper(connOpts, "NatsLooper-2")
//        looper2.start()
//        val looper3 = NatsLooper(connOpts, "NatsLooper-3")
//        looper3.start()

        // wait for ENTER key to exit
        println("Press ENTER to exit")
        readLine()
        looper1.stop()
//        looper2.stop()
//        looper3.stop()
        println("NATS connection closed")
    }
}