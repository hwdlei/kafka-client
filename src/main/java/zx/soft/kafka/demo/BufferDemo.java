package zx.soft.kafka.demo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class BufferDemo {

	public static void main(String[] args) {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		String a = "hello";
		buffer.put(a.getBytes());
		System.out.println(buffer.position());
		buffer.flip();
		System.out.println(buffer.remaining());
		System.out.println(buffer.limit());
		System.out.println(buffer.position());
		System.out.println(buffer.array().length);
		System.out.println(new String(buffer.array()));
		try {
			System.out.println(InetAddress.getLocalHost().getHostAddress());
			System.out.println(InetAddress.getLocalHost().getAddress().length);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
