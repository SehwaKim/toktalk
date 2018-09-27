import React from 'react';
import SockJS from 'sockjs-client';

class WebSocket extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            currentId: 1
        };
        this.sendChatMsg = this.sendChatMsg.bind(this);
    }
    componentDidMount() {
        var scheme = window.location.protocol;
        var path = scheme + '//' + window.location.host + "/sock";
        if (window.location.host == 'localhost:3000') {
            path = "http://localhost:8080/sock";
        }
        this.connection = new SockJS(path);
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
            if ('CHAT' === type) {
                if (data.notification) {
                    this.props.markAsUnread(data.channelId); // notifyUnread(data.channelId);
                    // notificationf(data.channelId); // 구글 노티
                    return;
                }

                // $("#typingAlarm").text(''); //기존
                // clearTimeout(timer1);//기존
                this.props.printMessage(data);
            }

            if ('SYSTEM' === type) {
                this.props.printMessage(data);
            }

            if ('TYPING' === type) {

            }

            if ('CHANNEL_MARK' === type) {

            }

            if ('CHANNEL_JOINED' === type) {
                if (data.channel.type === 'DIRECT') {
                    this.props.addNewChannel(data.channel);
                }
            }

            this.setState({
                // messages: this.state.messages.concat([e.data])
            });
            e.preventDefault();
        }
    }

    sendChatMsg(cId, cType, text) {
        if (!(text === "") && cId !== 0) {
            this.connection.send(JSON.stringify({
                'type': 'chat',
                'channelId': cId,
                'channelType': cType,
                'text': text
            }));
        }
    }

    notifyInvitation(cId, userId) {
        this.connection.send(JSON.stringify({'type': 'invite_direct', 'channelId': cId, 'userId': userId}))
    }

    render() {
        return (null);
    }

    switchChannel(cId) {
        this.connection.send(JSON.stringify({'type': 'switch', 'channelId': cId}));
    }
}

export default WebSocket;