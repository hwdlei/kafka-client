package zx.soft.kafka.consumer.demo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.codec.binary.Hex;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class Comsumer2PcapFile implements Serializable {

	private static final long serialVersionUID = -1754274621470675844L;

	public static void main(String[] args) {
		System.out.println("6e1906576eb30000ea05000038c9860a633d00900b2929250800".length());
		System.out.println("6e1906576eb30000ea05000038c9860a633d00900b2929250800".length());
		Properties props = new Properties();
		props.put("bootstrap.servers", "kafka01:19092,kafka02:19093,kafka03:19094");
		props.put("group.id", "test");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", ByteArrayDeserializer.class.getName());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<String, byte[]>(props);
		consumer.subscribe(Arrays.asList("test"));

		byte[] pcapHeader = new byte[24];
		try (DataInputStream inputStream = new DataInputStream(new FileInputStream(
				"src/main/resources/pcap/ftp_12m-f4.pcap"))) {
			int num = inputStream.read(pcapHeader);
			if(num != 24) {
				System.exit(1);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(
				"src/main/resources/pcap/frasms.pcap"))) {
			outputStream.write(pcapHeader);
			int n = 0;
			while (n < 1) {
				ConsumerRecords<String, byte[]> records = consumer.poll(100);
				for (ConsumerRecord<String, byte[]> record : records) {
					System.out.printf("partition = %d, offset = %d, key = %s, value = %s\n", record.partition(),
							record.offset(), record.key(), record.value());
					byte[] datas = record.value();
					System.out.println(Hex.encodeHexString(datas));
					ByteBuffer buffer = ByteBuffer.wrap(datas);
					buffer.order(ByteOrder.BIG_ENDIAN);
					if (datas.length > 32) {
						// IP标识16byte
						byte[] ipDatas = new byte[16];
						buffer.get(ipDatas);
						boolean netType = false;
						for (int i = 0; i < 12; i++) {
							byte a = ipDatas[i];
							if (a != 0) {
								netType = true;
								break;
							}
						}
						if (!netType) {
							byte[] tmp = new byte[4];
							System.arraycopy(ipDatas, 12, tmp, 0, 4);
							ipDatas = tmp;
						}
						try {
							String ip = InetAddress.getByAddress(ipDatas).getHostAddress();
							long timestamp = buffer.getLong();
							long index = buffer.getInt();
							byte[] payloadDatas = new byte[datas.length - 32];
							System.arraycopy(datas, 32, payloadDatas, 0, datas.length - 32);
							outputStream.write(payloadDatas);
							System.out.println(Hex.encodeHexString(payloadDatas));
							n++;
							break;
						} catch (Exception e) {
						}
					}
				}
			}
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}