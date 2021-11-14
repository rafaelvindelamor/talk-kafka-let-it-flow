kafka-topics \
  --bootstrap-server localhost:9092 \
  --topic orders \
  --replication-factor 1 \
  --partitions 3 \
  --create

kafka-topics \
  --bootstrap-server localhost:9092 \
  --topic promotions \
  --replication-factor 1 \
  --partitions 3 \
  --create \
  --config "cleanup.policy=compact"

kafka-topics \
  --bootstrap-server localhost:9092 \
  --topic orders-promotion-applied \
  --replication-factor 1 \
  --partitions 3 \
  --create
