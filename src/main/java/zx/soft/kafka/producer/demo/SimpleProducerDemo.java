package zx.soft.kafka.producer.demo;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试单生产者对单分区写数据：
 * 1、同一个生产者往同一个分区写数据，写入什么顺序读取就是什么顺序；
 *
 * 测试环境：
 * 1、apt-test:24个分区，没有复制；
 * 2、apt-test1:1个分区，没有复制；
 *
 * @author donglei
 * @date: 2016年5月9日 下午3:27:35
 */
public class SimpleProducerDemo {

	private static final Logger logger = LoggerFactory.getLogger(SimpleProducerDemo.class);

	public static void main(String[] args) {

		Properties props = new Properties();
		props.put("bootstrap.servers", "kafka01:19092,kafka02:19093,kafka03:19094");
		props.put("acks", "all");
		props.put("retries", 3);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", StringSerializer.class);
		props.put("value.serializer", ByteArraySerializer.class);
		int partitionNum = 1;
		Producer<String, byte[]> producer = new KafkaProducer<>(props);
		for (int i = 0; i < 1000000; i++) {
			producer.send(new ProducerRecord<String, byte[]>("apt-test", partitionNum, i + "", new byte[2 * 1024]));
			logger.info(i + "");
		}
		producer.close();
	}

}
