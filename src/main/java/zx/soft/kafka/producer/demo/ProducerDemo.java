package zx.soft.kafka.producer.demo;

import zx.soft.kafka.producer.ProducerInstance;

public class ProducerDemo {

	public static void main(String[] args) {

		// Producer
		ProducerInstance producer = ProducerInstance.getInstance();
		String topic = "test";
		for (int i = 0; i < 10; i++) {
			producer.pushRecord(topic, (i + "").getBytes());
		}
		producer.close();

		System.out.println("Finish!");
	}

}