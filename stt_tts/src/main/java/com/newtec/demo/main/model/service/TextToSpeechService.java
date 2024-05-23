package com.newtec.demo.main.model.service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class TextToSpeechService {
	
	@Autowired
    private Environment env;
 
    public String getUrl() {
        return env.getProperty("GOOGLE_APPLICATION_CREDENTIALS");
    }
	
	 public String googleTTS(String originText, String path) throws Exception{
		 
		 String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
		 log.info("GOOGLE_APPLICATION_CREDENTIALS: {}", credentialsPath);

        if (credentialsPath == null || !Files.exists(Paths.get(credentialsPath))) {
        	throw new IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS 환경 변수가 설정되지 않았거나 파일을 찾을 수 없습니다.");
        }

		// 클라이언트 인스턴스 생성
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // 변환할 텍스트 설정
            SynthesisInput input = SynthesisInput.newBuilder().setText(originText).build();

            // 음성 설정: 한국어(Ko-KR) 및 중립적 성별
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("ko-KR")
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();

            // 오디오 파일 형식 설정
            AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

            // 텍스트-음성 변환 요청 수행
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // 응답에서 오디오 내용 가져오기
            ByteString audioContents = response.getAudioContent();

            // 오디오 내용을 파일에 쓰기
            try (OutputStream out = new FileOutputStream(path + "output.mp3")) {
				out.write(audioContents.toByteArray());
				log.info(path + "output.mp3 생성" );
				
				
			}
            
            return "output";
            
        } catch (Exception e) {
        	e.printStackTrace();
			return null;
		}
	 }
}
