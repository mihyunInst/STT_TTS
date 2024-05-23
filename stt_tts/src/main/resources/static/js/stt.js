let mediaRecorder;
let audioChunks = [];

const startButton = document.getElementById('startRecording');
const stopButton = document.getElementById('stopRecording');
const audioPlayback = document.getElementById('audioPlayback');
const sendBtn = document.getElementById('sendBtn');

// 시작버튼 클릭 시
startButton.addEventListener('click', async () => {
    startButton.disabled = true;
    stopButton.disabled = false;

    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    mediaRecorder = new MediaRecorder(stream);

    mediaRecorder.ondataavailable = event => {
        audioChunks.push(event.data);
    };

    mediaRecorder.onstop = () => {
        const audioBlob = new Blob(audioChunks, { type: 'audio/wav' });
        audioChunks = [];
        const audioUrl = URL.createObjectURL(audioBlob);
        audioPlayback.src = audioUrl;

        // 전송을 위해 audioBlob을 fileUpload 메서드로 전송
        uploadAudioFile(audioBlob);
    };

    mediaRecorder.start();
});

// 정지 버튼 클릭 시 
stopButton.addEventListener('click', () => {
    startButton.disabled = false;
    stopButton.disabled = true;
    mediaRecorder.stop();
});

// 결과를 담을 요소
const resultText = document.getElementById('result');

function uploadAudioFile(audioBlob) {
    const formData = new FormData();
    formData.append('upload', audioBlob, 'recording.wav');
    
    fetch('/stt', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(result => {
        console.log('Success:', result);

        // 결과 문자열
        resultString = result.text;

        // 한 글자씩 표시될 인덱스
        let index = 0;

        // setInterval 함수를 사용하여 특정 시간마다 한 글자씩 표시
        let interval = setInterval(function() {
            // resultString의 앞에서부터 index까지의 부분 문자열을 표시
            resultText.value = resultString.substring(0, index);
            // index 증가
            index++;

            // 모든 글자를 표시한 경우 setInterval 종료
            if (index > resultString.length) {
                clearInterval(interval);
            }
        }, 100); // 각 글자가 표시되는 시간 (밀리초)



    })
    .catch(error => {
        console.error('Error:', error);
    });
}

sendBtn.addEventListener("click", () => {
    
    fetch("/sendMessage" ,{
        method: 'POST',
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({"sttContent" : resultText.value})
    })
    .then(res => res.text())
    .then(data => {
        
        if(data > 0) {
            alert("전송 완료");
            resultText.value = "";

        } else {
            alert("전송 실패.......");
        }
    })

})