package zx.soft.kafka.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.kafka.consumer.KafkaConsumerGroup;
import zx.soft.kafka.consumer.MessageHandler;

public class ConsumerGroupExample {
	private static Logger logger = LoggerFactory.getLogger(ConsumerGroupExample.class);

	public static void main(String[] args) {
		String topic = "apt-test";
		int threads = 24;

		KafkaConsumerGroup example = new KafkaConsumerGroup(topic);
		example.run(threads, new MessageHandler() {

			@Override
			public void handleMessage(byte[] key, byte[] message) {
				logger.info("Thread " + this.hashCode() + ": " + new String(key)  + "\t");
			}

		});
	}
}
