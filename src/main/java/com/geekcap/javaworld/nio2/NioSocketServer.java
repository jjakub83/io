package com.geekcap.javaworld.nio2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicInteger;

public class NioSocketServer {
	public NioSocketServer() {

		final AtomicInteger counter = new AtomicInteger();

		try {
			// Create an AsynchronousServerSocketChannel that will listen on
			// port 5000
			final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(8080));

			// Listen for a new request
			listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

				@Override
				public void completed(final AsynchronousSocketChannel ch, Void att) {
			//		System.out.println("completed start " + counter.incrementAndGet());
					// Accept the next connection
					listener.accept(null, this);

					// Greet the client
					ch.write(ByteBuffer.wrap("Hello, I am Echo Server 2020, let's have an engaging conversation!\n".getBytes()));

					// Allocate a byte buffer (4K) to read from the client
					final ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
					try {

						ch.read(byteBuffer, new Holder<StringBuffer>(StringBuffer.class),
							new CompletionHandler<Integer, Holder<StringBuffer>>() {

								@Override
								public void completed(Integer bytesRead, Holder<StringBuffer> holder) {
									if (bytesRead != -1) {
										System.out.println("bytes read: " + bytesRead);

										// Make the buffer ready to read
										byteBuffer.flip();

										// Convert the buffer into a line
										byte[] lineBytes = new byte[bytesRead];
										byteBuffer.get(lineBytes, 0, bytesRead);
										String message = new String(lineBytes);
										if (holder.value.length() > 0) {
											System.out.println("found previous content " + holder.value);
											message = holder.value.toString() + message;
											holder.value.delete(0, holder.value.length());
										}

										// Debug
			//							System.out.println("Message: " + message);

										if (message.contains("\n")) {
											if (! message.endsWith("\n")) {
			//									System.out.println("not ending with EOL [" + message + "]");
												int pos = message.lastIndexOf("\n");
			//									System.out.println("putting into holder: [" + message.substring(pos+1)+"]");
												holder.value.append(message.substring(pos+1));
												message = message.substring(0, pos+1);
													
											}
											for (String line : message.split("\n")) {
			//									System.out.println("line: " + line);
												// Echo back to the caller
												ch.write(ByteBuffer.wrap(( line + "\n").getBytes()));

											}

											if (message.endsWith("\n\n")) {
			//									System.out.println("double end - ending conversation");
												closeChannel();
												return;
											} else if (message.endsWith("\n")) {
			//									System.out.println("single end - interesting case");
												// closeChannel();
												// return;
											} else {
			//									System.out.println("continuing conversation");
											}
										} else {
											holder.value.append(message);
										}
										// Make the buffer ready to write
										byteBuffer.clear();
									
										ch.read(byteBuffer, holder, this);

									} else {
			//							System.out.println("closing channel, bytesRead == -1");
										closeChannel();
									}
								}

								@Override
								public void failed(Throwable exc, Holder<StringBuffer> attachment) {
									closeChannel();
								}

								private void closeChannel() {
									if (ch.isOpen()) {
										try {
											ch.close();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
							});

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				@Override
				public void failed(Throwable exc, Void att) {
					System.out.println("accept failed");
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		NioSocketServer server = new NioSocketServer();
		try {
			Thread.sleep(6000000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
