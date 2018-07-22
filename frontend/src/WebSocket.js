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
            if ('chat' == type) {
                this.props.printMessage(data);
            }

            if ('messageList' == type) {

            }

            if ('unread' == type) {

            }

            if ('system' == type) {

            }

            if ('typing' == type) {

            }

            if ('channel_mark' == type) {

            }

            if ('upload_file' == type) {

            }

            if ('channel_joined' == type) {

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