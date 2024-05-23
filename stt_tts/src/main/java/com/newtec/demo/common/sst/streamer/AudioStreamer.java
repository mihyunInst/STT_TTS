package com.newtec.demo.common.sst.streamer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AudioStreamer {

    public static void streamAudio(WebSocket webSocket, String filePath) throws Exception {
        AudioInputStream in;
        
        try {
            File file = new File(filePath);
            in = AudioSystem.getAudioInputStream(file);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        byte[] buffer = new byte[1024];
        int readBytes;
        
        while ((readBytes = in.read(buffer)) != -1) {
        	
            WebSocket sent = webSocket.sendBinary(ByteBuffer.wrap(buffer, 0, readBytes), true).join();
            if (sent != null) {
            	log.warn("Send buffer is full. Cannot complete request. Increase sleep interval.");
                return;
            }
            Thread.sleep(0, 50);
        }
        in.close();
        webSocket.sendText("EOS", true).join();
    }
}
