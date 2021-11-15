kafka-topics \
  --bootstrap-server localhost:9092 \
  --delete \
  --topic orders

kafka-topics \
  --bootstrap-server localhost:9092 \
  --delete \
  --topic promotions

kafka-topics \
  --bootstrap-server localhost:9092 \
  --delete \
  --topic orders-promotion-applied
