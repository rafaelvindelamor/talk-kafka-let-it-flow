package io.demo.kafka.producer

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

import java.util.{Properties, UUID}

object A05AsyncProducer extends App {

  // https://kafka.apache.org/documentation/#producerconfigs
  val properties: Properties = {
    val props = new Properties()
    // Mandatory properties
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    // Optional properties
    props.put("client.id", this.getClass.getCanonicalName)
    props.put("linger.ms", "0")
    props
  }

  val producer = new KafkaProducer[String, String](properties)
  val topic = "quickstart-events"

  val record = new ProducerRecord[String, String](topic, UUID.randomUUID().toString)

  try {
    producer.send(record, (recordMetadata: RecordMetadata, exception: Exception) => println("Hey!"))
  } catch {
    case _: Throwable => println("Booom!")
  }

  producer.close()
}
