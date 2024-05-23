package com.newtec.demo.common.sst.listener;


import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RTZRWebSocketListener implements WebSocket.Listener{
	
	//private static final Logger logger = Logger.getLogger(RTZRWebSocketListener.class.getName());
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void onOpen(WebSocket webSocket) {
    	log.info("OPEN");
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
    	log.debug("onText data : " + data);
        webSocket.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        log.debug("onBinary data : " + data);
        webSocket.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
    	log.info("Closed {0} {1}", statusCode, reason);
        latch.countDown();
        return Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        error.printStackTrace();
        latch.countDown();
    }

    public void waitClose() throws InterruptedException {
    	log.info("Wait for finish");
        latch.await();
    }
}
