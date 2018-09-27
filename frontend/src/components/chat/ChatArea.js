import React from "react";
import $ from "jquery";
import Avatar from '../sidebar/Avatar';

class ChatArea extends React.Component {
    constructor(props) {
        super(props);
        this.self = React.createRef();
        this.state = {
            items: [],
            lastTime: ''
        };
        this.addItem = this.addItem.bind(this);
    }

    componentDidMount() {
        $.ajax({
            url: '/api/messages',
            method: 'GET',
            data: {channelId: this.props.cId}
        }).done(json => {
            var messages = [];
            for (let message of json) {
                messages.push(<ChatContent key={Date.now() + message.id} {...message}/>);
            }
            this.setState({
                items: messages
            });
        });
    }

    addItem(message) {
        this.setState((prevState) => {
            return {
                items: prevState.items.concat(<ChatContent key={Date.now() + message.id} {...message}/>)
            };
        });
    }

    render() {
        return (
            <div className="chat-area" ref={this.self}>
                {this.state.items}
            </div>
        );
    }
}

class ChatContent extends React.Component {
    constructor(props) {
        super(props);
    }
    render() {
        var nicknameStyle = {
            fontWeight: 'bold'
        };
        var timeStyle = {
            color: '#767676',
            fontSize: '12px',
            marginLeft: '5px'
        };
        var avatarStyle = {};
        var divStyle = {
            width: '4%',
            paddingRight: '10px',
            textAlign: 'right'
        };
        if ('system' == this.props.type) {
            return (
                <div className="chat-content">
                    <div className="row">
                        <div style={divStyle} className="column">
                            <span className="time">{this.props.regdate}</span>
                        </div>
                        <div className="column">
                            <span className="content">{this.props.text}</span>
                        </div>
                    </div>
                </div>
            );
        }
        return (
            <div className="chat-content">
                <div className="row">
                    <div style={divStyle} className="column avatar">
                        <Avatar style={avatarStyle} path="/images/woman.png"/>
                    </div>
                    <div className="column">
                        <div>
                            <span style={nicknameStyle}>{this.props.nickname}</span>
                            <span style={timeStyle}>{this.props.regdate}</span>
                        </div>
                        <div>
                            <span>{this.props.text}</span>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default ChatArea;