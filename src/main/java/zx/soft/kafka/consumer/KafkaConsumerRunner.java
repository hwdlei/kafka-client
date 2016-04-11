package zx.soft.kafka.consumer;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

/**
 *	Kafka抽象消费者
 * @author donglei
 * @date: 2016年1月25日 下午7:27:10
 * @param <K>
 * @param <V>
 */
public abstract class KafkaConsumerRunner<K, V> implements Runnable, IMessageHandler<K, V> {
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final KafkaConsumer<K, V> consumer;

	public KafkaConsumerRunner(KafkaConsumer<K, V> consumer) {
		this.consumer = consumer;
	}

	@Override
	public void run() {
		try {
			while (!closed.get()) {
				ConsumerRecords<K,V> records = consumer.poll(1000);
				for(ConsumerRecord<K, V> record : records) {
					System.out.printf("partition = %d, offset = %d, key = %s, value = %s\n", record.partition(),
							record.offset(), record.key(), record.value());
					handleMessage(record.key(), record.value());
				}
			}
		} catch (WakeupException e) {
			// Ignore exception if closing
			if (!closed.get()) {
				throw e;
			}
		} finally {
			consumer.close();
		}
	}

	// Shutdown hook which can be called from a separate thread
	public void shutdown() {
		closed.set(true);
		consumer.wakeup();
	}

	@Override
	public abstract void handleMessage(K key, V value);
}
