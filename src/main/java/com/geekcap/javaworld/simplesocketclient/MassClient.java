package com.geekcap.javaworld.simplesocketclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

public class MassClient {

	private static int CONN_COUNT = 1000;
	private static String server;
	
	public static void main(String[] args) throws InterruptedException, IOException {
		if (args.length < 2) {
			System.out.println("Usage: SimpleSocketClientExample <server> CONN_COUNT");
			System.exit(0);
		}
		String server = args[0];
		CONN_COUNT = Integer.parseInt(args[1]);

		System.out.println("Loading contents of URL: " + server);

		initSockets();
		
		
		send("GET / HTTP/1.0");
		read();
		
		while (true) {
			Thread.sleep(1000 * 10);
			String message = "" + new Date();
			send(message);
			readAndVerify(message);
		}
		

		// Thread.sleep(6000000);
	}

	private static List<Channel> channelList = new ArrayList<>();
	
	private static void initSockets() throws IOException {
		for (int i = 0; i < CONN_COUNT; i++) {
			if (i%100 == 0) System.out.println("initSockets count " + i);
			try {
				Socket socket = new Socket(server, 8080);

				PrintStream out = new PrintStream(socket.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				channelList.add(new Channel(socket, out, in));
			} catch (SocketException e) {
				e.printStackTrace();
				System.err.println("err with socket num " + i);
				throw new RuntimeException(e);
			}
		}
		
	}
	
	private static void send(String message) {
		Stopwatch sw = Stopwatch.createStarted();
		System.out.println("send " + message);
		int i = 0;
		for (Channel channel : channelList) {
			if (i++%100 == 0) System.out.println("count " + i);
			channel.out.println(message);
		}
		System.out.println("send took " + sw.elapsed(TimeUnit.MILLISECONDS));
	}
	
	private static void read() throws IOException {
		Stopwatch sw = Stopwatch.createStarted();
		int i = 0;
		for (Channel channel : channelList) {
			channel.out.flush();
			String line = channel.in.readLine();
			if (i++%100 == 0) {
				System.out.println("read " + i + " " + line);
			}
			
		}
		System.out.println("read took " + sw.elapsed(TimeUnit.MILLISECONDS));
	}
	
	private static void readAndVerify(String expected) throws IOException {
		Stopwatch sw = Stopwatch.createStarted();
		int i = 0;
		String line = null;
		for (Channel channel : channelList) {
			channel.out.flush();
			line = channel.in.readLine();
			if (i++%100 == 0) {
				if(!line.equals(expected)){
					System.out.println("ERROR send " + expected + " response " + line);
				} else {
					// System.out.println("read " + i + " " + line);
				}
			}
			
		}
		System.out.println("read took " + sw.elapsed(TimeUnit.MILLISECONDS) + " " + line);
	}

	private static class Channel {
		Socket socket;
		PrintStream out;
		BufferedReader in;
		public Channel(Socket socket, PrintStream out, BufferedReader in) {
			super();
			this.socket = socket;
			this.out = out;
			this.in = in;
		} 
	}
	
}
