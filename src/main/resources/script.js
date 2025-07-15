var socket = new SockJS('http://localhost:8088/ws');
var stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/messages', function (message) {
        showMessage(JSON.parse(message.body)['response'], false);
    });
});

function sendMessage() {
    var messageContent = document.getElementById("inputMessage").value;
    if (messageContent) {
        showMessage(messageContent, true);
        stompClient.send("/app/test", {}, messageContent);
        document.getElementById("inputMessage").value = '';
    }
}

function showMessage(message, isClient) {
    var messageElement = document.createElement('div');
    messageElement.classList.add('message-bubble');
    messageElement.classList.add(isClient ? 'client' : 'server');

    var text = document.createElement('p');
    text.innerText = message;

    var date = document.createElement('span');
    var now = new Date();
    date.innerText = now.getHours().toString().padStart(2, '0') + ":" + now.getMinutes().toString().padStart(2, '0');

    messageElement.appendChild(text);
    messageElement.appendChild(date);

    document.getElementById("messages").appendChild(messageElement);
    document.getElementById("messages").scrollTop = document.getElementById("messages").scrollHeight;
}