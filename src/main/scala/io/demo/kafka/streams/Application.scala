package io.demo.kafka.streams

import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

import java.util.{Properties, UUID}

object Application extends App {

  val topology = OrderPromotionEnricherTopology()

  println(topology.describe())

  val config = new Properties()
  config.put(StreamsConfig.APPLICATION_ID_CONFIG, s"orderPromotionEnricher${UUID.randomUUID()}")
  config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")

  val kafkaStreams = new KafkaStreams(topology, config)

  kafkaStreams.setUncaughtExceptionHandler(e => {
    println(s"Caught unhandled exception", e)
    StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_APPLICATION
  })

  println("Starting order promotion streams")

  kafkaStreams.start()

  sys.ShutdownHookThread {
    kafkaStreams.close()
  }
}
