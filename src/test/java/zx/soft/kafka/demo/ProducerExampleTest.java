package zx.soft.kafka.demo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import junit.framework.TestCase;

public class ProducerExampleTest extends TestCase {

	public void testGetPacket() throws UnknownHostException {
		long timestamp = System.currentTimeMillis();
		byte[] datas = PcapProducerExample.getPacket(timestamp, 0, "123".getBytes(), "123".getBytes().length);
		ByteBuffer buffer = ByteBuffer.wrap(datas);
		buffer.order(ByteOrder.BIG_ENDIAN);
		if (datas.length > 28) {
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
				assertTrue(ip.equals(InetAddress.getLocalHost().getHostAddress()));
				long timestamps2 = buffer.getLong();
				assertTrue(timestamp == timestamps2);
				System.out.println(timestamp);
				int index = buffer.getInt();
				assertTrue(index == 0);
				byte[] payloadDatas = new byte[datas.length - 28];
				System.arraycopy(datas, 28, payloadDatas, 0, datas.length - 28);
				assertTrue("123".equals(new String(payloadDatas)));

			} catch (Exception e) {
			}
		}
	}
}
