package com.newtec.demo.main.model.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MulticastSocket;
import java.net.URL;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newtec.demo.main.model.mapper.MainMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpeechToTextService {
	
	private final MainMapper mapper;

	public String stt(String filepath, String clientId, String clientSecret) {	
		
		// 반환하기 위해 try문 외부에서 선언
        StringBuffer response = new StringBuffer();	
    
        try {
            
            File voiceFile = new File(filepath);
            
            String language = "Kor"; // 언어 코드 ( Kor, Jpn, Eng, Chn )
            String apiURL = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=" + language;
            URL url = new URL(apiURL);

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
        	
            OutputStream outputStream = conn.getOutputStream();
            FileInputStream inputStream = new FileInputStream(voiceFile);
            
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            outputStream.flush();
            inputStream.close();
            BufferedReader br = null;
            int responseCode = conn.getResponseCode();
            
            if(responseCode == 200) { // 정상 호출 시
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                
            } else {  // 오류 발생 시
                System.out.println("error!!!!!!! responseCode= " + responseCode);
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            
            String inputLine;

            if(br != null) {
            	
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                
                br.close();
                
            }
       
        } catch (Exception e) {
			e.printStackTrace();
		}
        
		return response.toString();	// 변환된 문자열 반환
	}

	@Transactional
	public int sendMessage(String message) {
		return mapper.sendMessage(message);
	}
}
