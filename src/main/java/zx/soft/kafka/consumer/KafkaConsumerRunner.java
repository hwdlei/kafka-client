package zx.soft.kafka.consumer;

import java.util.List;
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
	private final List<String> topics;

	public KafkaConsumerRunner(KafkaConsumer<K, V> consumer, List<String> topics) {
		this.consumer = consumer;
		this.topics = topics;
	}

	@Override
	public void run() {
		try {
			consumer.subscribe(this.topics);
			while (!closed.get()) {
				ConsumerRecords<K,V> records = consumer.poll(1000);
				for(ConsumerRecord<K, V> record : records) {
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
