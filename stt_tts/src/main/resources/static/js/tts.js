const textData = document.querySelector("#textData");
const speakButton = document.querySelector("#speakButton");

speakButton.addEventListener("click", async () => {
    try {
        console.log(textData.innerText);
        
        const data = { originText: textData.innerText };

        const response = await fetch("/tts", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error("Failed to fetch audio file name");
        }

        const filename = await response.text();
        console.log(filename);

        if (filename) {
            const audioUrl = `/audio/${filename}?t=${new Date().getTime()}`;
            const audio = new Audio(audioUrl);
            await audio.play();
            console.log("Audio is playing");
        } else {
            console.log("No filename received");
        }
    } catch (error) {
        console.error("Error:", error);
    }
});

// const textData = document.querySelector("#textData");
// const speakButton = document.querySelector("#speakButton");

// speakButton.addEventListener("click", () => {

//     console.log(textData.innerText);
    
//     const data = { originText: textData.innerText };

//     fetch("/tts", {
//         method : "POST",
//         headers : {"Content-Type" : "application/json"},
//         body : JSON.stringify(data) 
//     })
//     .then(res => res.text())
//     .then(filename => {
//         console.log(filename);

//         if (filename) {
//             const audio = new Audio(`/audio/${filename}?t=${new Date().getTime()}`);
//             audio.play()
//                 .then(() => {
//                     console.log("Audio is playing");
//                 })
//                 .catch(error => {
//                     console.error("Error playing audio:", error);
//                 });
//         } else {
//             console.log("Please enter a file name");
//         }
    
//     })
// })
