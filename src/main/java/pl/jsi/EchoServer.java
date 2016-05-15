package pl.jsi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;
import io.vertx.core.streams.Pump;

public class EchoServer extends AbstractVerticle {

	public void start() {
		vertx.createNetServer().connectHandler(new Handler<NetSocket>() {
			public void handle(final NetSocket socket) {
				Pump.factory.pump(socket, socket).start();
			}
		}).listen(1234);
	}

	public static void main(String[] args) {

		Vertx vertx = Vertx.factory.vertx();
		vertx.createNetServer().connectHandler(new Handler<NetSocket>() {
			public void handle(final NetSocket socket) {
				Pump.factory.pump(socket, socket).start();
			}
		}).listen(8080);

	}

}