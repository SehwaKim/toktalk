import React from "react";

class MessageArea extends React.Component {
    render() {
        return (
            <div className="message-area">
                <MessageBox {...this.props}/>
            </div>
        );
    }
}

class MessageBox extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            cId: 1
        };
        this.sendMessage = this.sendMessage.bind(this);
    }

    sendMessage(e) {
        this.props.sendMessage(this.state.cId, this._inputElement.value);
        this._inputElement.value = "";
        e.preventDefault();
    }

    render() {
        var inputStyle = {
            margin: '3px',
            padding: '7px 5px 7px 5px',
            fontSize: '15px',
            width: '80%',
            border: '0px'
        }
        return (
            <div className="message-box">
                <form onSubmit={this.sendMessage}>
                    <input ref={(a) => this._inputElement = a}
                           placeholder="메세지를 입력하세요" style={inputStyle}/>
                </form>
            </div>
        );
    }
}

export default MessageArea;