kafka-console-producer \
  --broker-list localhost:9092 \
  --topic orders < ../data/orders.txt \
  --property parse.key=true \
  --property key.separator=,

kafka-console-producer \
  --broker-list localhost:9092 \
  --topic vouchers < ../data/vouchers.txt \
  --property parse.key=true \
  --property key.separator=,
