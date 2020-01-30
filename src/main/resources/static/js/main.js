'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');

let stompClient = null;
let username = null;

const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        const socket = new WebSocket('ws://localhost:8689/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Chat
    stompClient.subscribe('/chat/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/chat/add-user",
        {},
        JSON.stringify({client_nickname: username, message_type: 'JOIN'})
    );

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        const chatMessage = {
            client_nickname: username,
            content: messageInput.value,
            message_type: 'WRITE'
        };

        stompClient.send("/chat/send-message", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);

    if (Array.isArray(message)) {
        for (let i = 0; i < message.length; i++) {
            processSimpleMessage(message[i]);
        }
    } else {
        processSimpleMessage(message);
    }
}

function processSimpleMessage(message) {
    let messageElement = document.createElement('li');
    if (message.message_type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.client_nickname + ' joined!';
    } else if (message.message_type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.client_nickname + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        const avatarElement = document.createElement('i');
        const avatarText = document.createTextNode(message.client_nickname[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.client_nickname);

        messageElement.appendChild(avatarElement);

        const usernameElement = document.createElement('span');
        const usernameText = document.createTextNode(message.client_nickname);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    const textElement = document.createElement('p');
    const messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(client_nickname) {
    let hash = 0;
    for (let i = 0; i < client_nickname.length; i++) {
        hash = 31 * hash + client_nickname.charCodeAt(i);
    }

    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
