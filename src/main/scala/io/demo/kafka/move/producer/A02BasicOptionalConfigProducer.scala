package io.demo.kafka.move.producer

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
    props.put("client.id", this.getClass.getCanonicalName) // https://kafka.apache.org/documentation/#consumerconfigs_client.id
    props.put("batch.size", "16384") // https://kafka.apache.org/documentation/#producerconfigs_batch.size
    props.put("linger.ms", "5000") // https://kafka.apache.org/documentation/#producerconfigs_linger.ms
    props.put("compression.type", "lz4") // https://kafka.apache.org/documentation/#producerconfigs_compression.type
    props.put("acks", "0") // https://kafka.apache.org/documentation/#producerconfigs_acks
    props.put("retries", "3") // https://kafka.apache.org/documentation/#producerconfigs_retries
    props.put("retry.backoff.ms", "100") // https://kafka.apache.org/documentation/#producerconfigs_retry.backoff.ms
    props.put("buffer.memory", "33554432") // https://kafka.apache.org/documentation/#producerconfigs_buffer.memory
    props.put("max.in.flight.requests.per.connection", "2") // https://kafka.apache.org/documentation/#producerconfigs_max.in.flight.requests.per.connection
    props.put("enable.idempotence", "true") // https://kafka.apache.org/documentation/#producerconfigs_enable.idempotence
    props
  }

  val topic = "quickstart-events"

  val producer = new KafkaProducer[String, String](properties)

  val record = new ProducerRecord[String, String](topic, UUID.randomUUID().toString)

  try {
    producer.send(record)
  } catch {
    case _: Throwable => println("Booom!")
  }

  producer.close()
}
