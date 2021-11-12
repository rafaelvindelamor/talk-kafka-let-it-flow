kafka-console-producer \
  --broker-list localhost:9092 \
  --topic orders < orders.txt \
  --property parse.key=true \
  --property key.separator=,

kafka-console-producer \
  --broker-list localhost:9092 \
  --topic promotions < promotions.txt \
  --property parse.key=true \
  --property key.separator=,