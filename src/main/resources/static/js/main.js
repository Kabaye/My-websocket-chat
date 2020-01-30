'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');

let stompClient = null;
let nickname = null;

const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    nickname = document.querySelector('#name').value.trim();

    if (nickname) {
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
    stompClient.subscribe('/chat/public/' + nickname, onMessageReceived);

    // Tell your username to the server
    stompClient.send("/chat/add_user",
        {},
        JSON.stringify({client_nickname: nickname, message_type: 'JOIN'})
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
            client_nickname: nickname,
            content: messageInput.value,
            message_type: 'WRITE'
        };

        stompClient.send("/chat/send_message", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function processPayload(payload, needToGoDown) {
    const message = JSON.parse(payload.body);
    if (message.response_type === 'SIMPLE_MESSAGE') {
        if (message.simple_message.client_nickname === nickname) {
            needToGoDown = true;
        }
        processSimpleMessage(message.simple_message, false, needToGoDown);
    } else if (message.response_type === 'OLD_MESSAGES') {
        for (let i = message.old_messages.length - 1; i >= 0; i--) {
            processSimpleMessage(message.old_messages[i], true, false);
        }
    }
}

function onMessageReceived(payload) {
    let needToGoDown = messageArea.scrollTop + messageArea.clientHeight === messageArea.scrollHeight;
    processPayload(payload, needToGoDown);
}

function processSimpleMessage(message, isFirst, needToGoDown) {
    let messageElement = document.createElement('li');
    messageElement.style.wordBreak = 'break-all';
    messageElement.style.maxWidth = '100%';
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

    if (isFirst) {
        let firstChild = messageArea.childNodes[0];
        messageArea.insertBefore(messageElement, firstChild);
    } else {
        messageArea.appendChild(messageElement);
    }

    messageArea.scrollTop = needToGoDown ? messageArea.scrollHeight : messageArea.scrollTop;
}

function getAvatarColor(client_nickname) {
    let hash = 0;
    for (let i = 0; i < client_nickname.length; i++) {
        hash = 31 * hash + client_nickname.charCodeAt(i);
    }

    var index = Math.abs(hash % colors.length);
    return colors[index];
}

function onScroll() {
    if (messageArea.scrollTop < 100) {
        getOldMessages(messageArea.childNodes.length, 10)
    }
}

function getOldMessages(lowerBound, amount) {
    if (stompClient) {
        const oldMessageRequest = {
            client_nickname: nickname,
            lower_bound: lowerBound,
            amount: amount
        };

        stompClient.send("/chat/get_old_messages", {}, JSON.stringify(oldMessageRequest));
    }
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);

