package zx.soft.kafka.producer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.utils.config.ConfigUtil;
import zx.soft.utils.log.LogbackUtil;

/**
 *
 * @author donglei
 *
 */
public class ProducerInstance {

	private static Logger logger = LoggerFactory.getLogger(ProducerInstance.class);

	private KafkaProducer<String, byte[]> producer;

	private boolean sync;

	private static ProducerInstance instance = new ProducerInstance();

	private ProducerInstance() {
		Properties kafka = ConfigUtil.getProps("kafka.properties");
		logger.info("load properties :" + kafka.toString());
		sync = Boolean.parseBoolean(kafka.getProperty("sync", "false"));

		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getProperty("bootstrap.servers"));

		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

		props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafka.getProperty("timeout.ms", "50000"));

		props.put(ProducerConfig.ACKS_CONFIG, kafka.getProperty("acks", "1"));

		this.producer = new KafkaProducer<String, byte[]>(props);
	}

	public static ProducerInstance getInstance() {
		return instance;
	}

	public void pushRecord(String topic, byte[] record) {
		ProducerRecord<String, byte[]> producerRecord = new ProducerRecord<String, byte[]>(topic, record);
		if (sync) {
			try {
				producer.send(producerRecord).get();
			} catch (InterruptedException | ExecutionException e) {
				logger.error("Records push failed!");
				logger.error(LogbackUtil.expection2Str(e));
			}
		} else {
			producer.send(producerRecord, new PushCallback());
		}
	}

	public void pushRecord(String topic, String key, byte[] record) {
		ProducerRecord<String, byte[]> producerRecord = new ProducerRecord<String, byte[]>(topic, key, record);
		if (sync) {
			try {
				producer.send(producerRecord).get();
			} catch (InterruptedException | ExecutionException e) {
				logger.error("Records push failed!");
				logger.error(LogbackUtil.expection2Str(e));
			}
		} else {
			producer.send(producerRecord, new PushCallback());
		}
	}

	public void pushRecords(String topic, List<byte[]> records) {
		for (byte[] record : records) {
			pushRecord(topic, record);
		}
	}

	public void close() {
		this.producer.close();
	}

	static class PushCallback implements Callback {
		@Override
		public void onCompletion(RecordMetadata metadata, Exception exception) {
			if (exception != null) {
				logger.error("Records push failed!");
				logger.error(LogbackUtil.expection2Str(exception));
			}

		}
	}

}
