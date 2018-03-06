package me.ramswaroop.jbot.core.slack;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

import org.springframework.http.HttpHeaders;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

public class PingingWebSocketClient extends StandardWebSocketClient {

	@Override
	protected ListenableFuture<WebSocketSession> doHandshakeInternal(WebSocketHandler webSocketHandler,
			HttpHeaders headers, final URI uri, List<String> protocols,
			List<WebSocketExtension> extensions, Map<String, Object> attributes) {
		
		ListenableFuture<WebSocketSession> future = super.doHandshakeInternal(webSocketHandler, headers, uri, protocols, extensions, attributes);
		future.addCallback(new ListenableFutureCallback<WebSocketSession>() {
			
			@Override
			public void onSuccess(@Nullable WebSocketSession resultingWebSocketSession) {
				Timer timer = new Timer();
		        timer.schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							logger.debug("Sending Ping message");
							resultingWebSocketSession.sendMessage(new PingMessage());
						} catch (IOException e) {
							logger.error("Cannot send Ping message", e);
						}
					}
				}, 0, 60000); // TODO do not hardcode
			}
			
			@Override
			public void onFailure(Throwable ex) {
				// nothing to do here, this is handled elsewhere
			}
			
		});
		
		return future;
	}
	
}
