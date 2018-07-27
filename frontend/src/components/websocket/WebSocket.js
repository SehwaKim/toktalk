import React from 'react';
import SockJS from 'sockjs-client';

class WebSocket extends React.Component {
    constructor() {
        super();
        this.state = {
            currentId: 1
        };
        this.sendChatMsg = this.sendChatMsg.bind(this);
    }
    componentDidMount() {
        this.connection = new SockJS('http://localhost:9090/sock');
        this.connection.onheartbeat = e => {
            this.connection.send(JSON.stringify({'type': 'pong'}));
            e.preventDefault();
        };
        this.connection.onmessage = e => {
            var data;
            try {
                data = JSON.parse(e.data);
            } catch (ex) {
                data = e.data;
            }
            const type = data.type;
            if ('CHAT' == type) {
                this.props.printMessage(data);
            }

            if ('MESSAGES' == type) {

            }

            if ('UNREAD' == type) {

            }

            if ('SYSTEM' == type) {

            }

            if ('TYPING' == type) {

            }

            if ('CHANNEL_MARK' == type) {

            }

            if ('UPLOAD_FILE' == type) {

            }

            if ('CHANNEL_JOINED' == type) {

            }

            this.setState({
                // messages: this.state.messages.concat([e.data])
            });
            e.preventDefault();
        }
    }

    sendChatMsg(cId, text) {
        if (!(text == "")) {
            this.connection.send(JSON.stringify({'type': 'chat', 'channelId': cId, 'text': text}));
        }
    }
    render() {
        return (null);
    }
}

export default WebSocket;