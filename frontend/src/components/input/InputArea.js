import React from "react";

class InputArea extends React.Component {
    render() {
        return (
            <div className="message-area">
                <InputBox {...this.props} ref={box => this.box = box}/>
            </div>
        );
    }
}

class InputBox extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            cId: 0,
            cType: ''
        };
        this.sendMessage = this.sendMessage.bind(this);
        this.switchChannel = this.switchChannel.bind(this);
    }

    sendMessage(e) {
        this.props.sendMessage(this.state.cId, this.state.cType, this._inputElement.value);
        this._inputElement.value = "";
        e.preventDefault();
    }

    switchChannel(cId, cType) {
        this.setState(() => {
            return {
                cId: cId,
                cType: cType
            };
        });
    }

    render() {
        var inputStyle = {
            margin: '3px',
            padding: '7px 5px 7px 5px',
            fontSize: '15px',
            width: '80%',
            border: '0px'
        };
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

export default InputArea;