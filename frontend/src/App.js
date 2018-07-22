import React, {Component} from 'react';
import {BrowserRouter as Router, Link} from 'react-router-dom';
import Sidebar from './Sidebar';
import ChatArea from './ChatArea';
import MessageArea from './MessageArea';
import WebSocket from "./WebSocket";

class Header extends React.Component {
    render() {
        var buttonStyle = {
            backgroundColor: 'Transparent',
            backgroundRepeat: 'no-repeat',
            border: 'none',
            cursor: 'pointer',
            overflow: 'hidden',
            outline: 'none'
        };
        var titleStyle = {
            fontSize: '20px',
            color: 'black',
            fontWeight: 'bold'
        };
        var searchStyle = {
            fontSize: '17px',
            color: '#8F8F8F'
        };
        var divStyle = {
            display: 'table',
            verticalAlign: 'middle',
            width: '100%'
        };
        var innerDivStyle_1 = {
            display: 'table-cell',
            width: '60%',
        };
        var innerDivStyle_2 = {
            display: 'table-cell',
            verticalAlign: 'middle',
            width: '40%',
            border: '1 solid'
        };
        var textBoxStyle = {
            border: '1px solid #AAAAAA',
            borderRadius: '8px',
            padding: '5px',
            width: '50%',
            marginTop: 5
        };
        return (
            <div className="header" style={divStyle}>
                <div style={innerDivStyle_1}>
                    <div>
                        <button style={{...buttonStyle, ...titleStyle}}>{this.props.name}</button>
                    </div>
                    <div>
                        {/*<button style={Object.assign(buttonStyle, textStyle)}>
                            <svg id="i-star" viewBox="0 0 32 32" width="15" height="15" fill="none" stroke="currentcolor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2">
                                <path d="M16 2 L20 12 30 12 22 19 25 30 16 23 7 30 10 19 2 12 12 12 Z" />
                            </svg>
                        </button>*/}
                    </div>
                </div>
                <div style={innerDivStyle_2}>
                    <div style={textBoxStyle}>
                        <button style={{...buttonStyle, ...searchStyle}}>
                            <svg className="i-search" viewBox="0 0 32 32" width="17" height="17" fill="none"
                                 stroke="currentcolor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2">
                                <circle cx="14" cy="14" r="12"/>
                                <path d="M23 23 L30 30"/>
                            </svg>
                            &nbsp;대화내용 검색
                        </button>
                    </div>
                </div>
            </div>
        );
    }
}

class Divider extends React.Component {
    render() {
        var lineStyle = {
            marginTop: '70px',
            display: 'block',
            border: 0,
            borderTop: '1px solid #E2E2E2',
            padding: 0
        };
        return (
            <hr style={lineStyle}></hr>
        );
    }
}

class Footer extends React.Component {
    render() {
        return (
            <div className="footer">
                <span>최연정 님이 입력 중</span>
            </div>
        );
    }
}

class Root extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            chatAreas: [],
            chatAreaRefs: []
        };
        this.addItem = this.addItem.bind(this);
        this.sendMessage = this.sendMessage.bind(this);
        this.printMessage = this.printMessage.bind(this);
    }

    addItem(cId) {
        let reference;
        this.setState((prevState) => {
            return {
                chatAreas: prevState.chatAreas.concat(<ChatArea key={Date.now() + cId} cId={cId}
                                                                ref={chatArea => reference = chatArea}/>)
            }
        });
        this.setState((prevState) => {
            return {
                chatAreaRefs: prevState.chatAreaRefs.concat(reference)
            }
        });
    }

    sendMessage(cId, text) {
        this.sock.sendChatMsg(cId, text);
    }

    printMessage(message) {
        for (let ref of this.state.chatAreaRefs) {
            if (ref.props.cId == message.channelId) {
                ref.addItem(message);
            }
        }
    }
    render() {
        return (
            <div>
                <Sidebar addChatArea={this.addItem}/>
                <Header name="general"/>
                <Divider/>
                {this.state.chatAreas}
                <MessageArea sendMessage={this.sendMessage}/>
                <Footer/>
                <WebSocket ref={sock => this.sock = sock} printMessage={this.printMessage}/>
            </div>
        );
    }
}

class App extends Component {
    render() {
        return (
            <Router>
                <Root/>
            </Router>
        );
    }
}

export default App;