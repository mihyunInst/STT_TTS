package com.newtec.demo.main.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.newtec.demo.main.model.dto.SttBoard;
import com.newtec.demo.main.model.service.SpeechToTextService;
import com.newtec.demo.main.model.service.TextToSpeechService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


@PropertySource("classpath:/config.properties")
@Controller
@Slf4j
public class MainController {
	
	@Value("${my.stt.client.id}")
	private String clientId;
	
	@Value("${my.stt.client.secret}")
	private String clientSecret;
	
	@Value("${my.stt.upload.path}")
	private String uploadPath;
	
	@Value("${my.tts.download.path}")
	private String downloadPath;
	
	@Autowired
	private SpeechToTextService speechToTextService;
	
	@Autowired
	private TextToSpeechService textToSpeechService;

	
	@RequestMapping("/")
	public String mainForward(){
		return "common/main";
	}
	
	@GetMapping("stt")
	public String sttForward(){
		return "common/stt";
	}
	
	@GetMapping("tts")
	public String ttsForward(){
		return "common/tts";
	}
	
	// STT 기능
	@ResponseBody
	@PostMapping("stt")
	public String fileUpload(@RequestParam("upload") MultipartFile upload,
							HttpServletRequest req) {
		
        // 업로드된 파일의 원본명 저장
		String filename = upload.getOriginalFilename();
		
		// 파일이 저장될 경로를 설정
		String filepath = uploadPath + filename; 
		
		try {
			// 출력 스트림 향상 후 업로드된 파일 쓰기
			BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
			os.write(upload.getBytes());
			os.close(); // 스트림 닫기
			
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		
		// 저장된 파일의 경로와 clientId, clientSecret 를 이용하여 
		// 네이버 클라우드 AI의 Speech-to-Text(STT) API를 호출 후 응답받기
		String resp = speechToTextService.stt(filepath, clientId, clientSecret);
		return resp;
	}
	
	// STT - 메시지 DB에 저장하기
	@ResponseBody
	@PostMapping("sendMessage")
	public int sendMessage(@RequestBody SttBoard sttBoard) {
		
		int result = speechToTextService.sendMessage(sttBoard.getSttContent());
		
		return result;
		
	}
	
	// TTS 기능
	@ResponseBody
	@PostMapping("tts")
	public String handleTtsRequest(@RequestBody Map<String, Object> map) throws Exception {
		String originText = (String) map.get("originText");
		log.debug("textData : " + originText);
		
		String filePath = textToSpeechService.googleTTS(originText, downloadPath);

		return filePath;
		
	}
	
	// TTS - 클라이언트에게 정적 오디오 파일 전달
	@ResponseBody
	@GetMapping("audio/{filename}")
    public ResponseEntity<Resource> getAudioFile(@PathVariable("filename") String filename) {
		// ResponseEntity<Resource> : HTTP 응답 본문으로 Resource 객체를 반환
		
        try {
        	// 파일 경로를 생성
            Path filePath = Paths.get(downloadPath, filename+".mp3");
            
            if (Files.exists(filePath)) { // 파일이 존재하는지 확인
            	
            	// 파일(음성) 데이터를 HTTP 응답 본문에 포함
                Resource resource = new FileSystemResource(filePath);
                return ResponseEntity.ok() // HTTP 상태 코드 200 OK를 응답으로 설정
                        .header("Content-Type", "audio/mpeg") // 클라이언트에게 반환하는 데이터가 오디오 파일임을 알림
                        .body(resource); // 응답 본문에 Resource 객체를 포함시켜 파일 데이터를 전송
                
            } else {
            	// HTTP 상태 코드 404 Not Found를 응답으로 설정하고, 
            	// 응답 본문을 null로 설정하여 클라이언트에게 파일을 찾을 수 없음을 알림
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            
        } catch (Exception e) {
        	// HTTP 상태 코드 500 Internal Server Error를 응답으로 설정하고, 
        	// 응답 본문을 null로 설정하여 클라이언트에게 서버에서 오류가 발생했음을 알림
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
	


}
