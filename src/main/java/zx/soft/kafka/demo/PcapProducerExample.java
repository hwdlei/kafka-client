package zx.soft.kafka.demo;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.kafka.producer.ProducerInstance;

public class PcapProducerExample {
	private static Logger logger = LoggerFactory.getLogger(PcapProducerExample.class);

	public static void main(String args[]) throws InterruptedException, ExecutionException, IOException {

		ProducerInstance instance = ProducerInstance.getInstance();
		String topic = "apt-cache";
		long timestamp = System.currentTimeMillis();
		int index = 0;

		try (DataInputStream inputStream = new DataInputStream(new FileInputStream(
				"src/main/resources/pcap/ftp_12m-f4.pcap"))) {
			byte[] datas = new byte[1024];
			int num = 0;
			while ((num = inputStream.read(datas)) != -1) {
				byte[] packet = getPacket(timestamp, index, datas, num);
				instance.pushRecord(topic, "ftp_12m-f4_" + timestamp + index, packet);
				System.out.println("ftp_12m-f4_" + timestamp + "_" + index);
				index++;
			}
		}

		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.putInt(index);
		buffer.flip();
		byte[] intBytes = new byte[buffer.remaining()];
		buffer.get(intBytes);
		byte[] endPacket = getPacket(timestamp, 0xFFFFFFFF, intBytes, 4);
		System.out.println("ftp_12m-f4_" + timestamp + "_" + index);
		instance.pushRecord(topic, "ftp_12m-f4_" + timestamp + "_" + index, endPacket);

		instance.close();
	}

	public static byte[] getPacket(long timestamp, int index, byte[] payload, int length) throws UnknownHostException {
		ByteBuffer buffer = ByteBuffer.allocate(1024 * 10);
		buffer.order(ByteOrder.BIG_ENDIAN);
		byte[] ipdates = new byte[12];
		buffer.put(ipdates);
		buffer.put(InetAddress.getLocalHost().getAddress());
		buffer.putLong(timestamp);
		buffer.putInt(index);
		buffer.put(payload, 0, length);
		buffer.flip();
		byte[] datas = new byte[buffer.remaining()];
		buffer.get(datas);
		return datas;
	}

}