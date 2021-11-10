package io.demo.kafka.producer

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import java.util.{Properties, UUID}

object A02BasicOptionalConfigProducer extends App {

  // https://kafka.apache.org/documentation/#producerconfigs
  val properties: Properties = {
    val props = new Properties()
    // Mandatory properties
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    // Optional properties
    props.put("client.id", this.getClass.getCanonicalName)
    props.put("batch.size", "16384")
    props.put("linger.ms", "5000")
    props.put("compression.type", "lz4")
    props.put("acks", "0")
    props.put("retries", "3")
    props.put("retry.backoff.ms", "100")
    props.put("buffer.memory", "33554432")
    props.put("max.in.flight.requests.per.connection", "2")
    props
  }

  val producer = new KafkaProducer[String, String](properties)
  val topic = "quickstart-events"

  val record = new ProducerRecord[String, String](topic, UUID.randomUUID().toString)

  try {
    producer.send(record)
  } catch {
    case _: Throwable => println("Booom!")
  }

  producer.close()
}
