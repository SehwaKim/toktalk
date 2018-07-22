import React from 'react';
import SockJS from 'sockjs-client';

class WebSocket extends React.Component {
    constructor() {
        super();
        this.state = {
            messages: []
        };
    }

    componentDidMount() {
        this.connection = new SockJS('http://localhost:9090/sock');
        this.connection.onheartbeat = () => this.connection.send(JSON.stringify({'type': 'pong'}));
        this.connection.onmessage = e => {
            const type = e.data.type;

            if ('chat' == type) {

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
                messages: this.state.messages.concat([e.data])
            });
            console.log(this.state.messages);
        }
    }

    render() {
        return (null);
    }
}

export default WebSocket;