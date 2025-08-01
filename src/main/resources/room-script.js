let stompClient = null;
let selectedRoomId = null;

document.addEventListener("DOMContentLoaded", () => {
    loadRooms();

    document.getElementById("roomSelect").addEventListener("change", function () {
        const roomId = this.value;
        if (roomId) {
            connectToRoom(roomId);
        }
    });
});

function loadRooms() {
    fetch("http://localhost:8080/api/rooms")
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById("roomSelect");
            select.innerHTML = "<option value=''>-- Select Room --</option>";
            data.forEach(room => {
                const option = document.createElement("option");
                option.value = room.id;
                option.text = room.name;
                select.appendChild(option);
            });
        });
}

function connectToRoom(roomId) {
    if (stompClient) {
        stompClient.disconnect(() => {
            console.log("Disconnected from previous room");
        });
    }

    const socket = new SockJS("http://localhost:8080/ws");
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
        selectedRoomId = roomId;

        stompClient.subscribe("/topic/room/" + roomId, function (message) {
            const msg = JSON.parse(message.body);
            showMessage(msg.content, msg.sender, msg.timestamp);
        });

        fetch("http://localhost:8080/api/" + roomId + "/messages")
            .then(res => res.json())
            .then(messages => {
                document.getElementById("messages").innerHTML = "";
                messages.forEach(msg => {
                    showMessage(msg.content, msg.sender, msg.timestamp);
                });
            });
    });
}

function sendMessage() {
    const content = document.getElementById("inputMessage").value;
    if (!content || !selectedRoomId) return;

    const sender = "Me"; // you can change this

    stompClient.send("/app/room/" + selectedRoomId, {}, JSON.stringify({
        sender: sender,
        content: content
    }));

    document.getElementById("inputMessage").value = '';
}

function showMessage(content, sender, timestamp) {
    const messageElement = document.createElement("div");
    messageElement.classList.add("message-bubble");
    messageElement.classList.add(sender === "Me" ? "client" : "server");

    const p = document.createElement("p");
    p.textContent = content;

    const span = document.createElement("span");
    span.textContent = new Date(timestamp).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});

    messageElement.appendChild(p);
    messageElement.appendChild(span);

    document.getElementById("messages").appendChild(messageElement);
    document.getElementById("messages").scrollTop = document.getElementById("messages").scrollHeight;
}
