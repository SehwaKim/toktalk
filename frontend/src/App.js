import React, {Component} from 'react';
import {BrowserRouter as Router, Link} from 'react-router-dom';
import Sidebar from './Sidebar';
import WebSocket from "./SockJS";

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

class ChatArea extends React.Component {
    render() {
        return (
            <div className="chat-area">
                <ChatContent {...this.props}/>
                <ChatContent {...this.props}/>
                <ChatContent {...this.props}/>
                <ChatContent {...this.props}/>
                <ChatContent {...this.props}/>
                <ChatContent {...this.props}/>
                <ChatContent {...this.props}/>
            </div>
        );
    }
}

class ChatContent extends React.Component {
    render() {
        return (
            <div className="chat-content">
                <span className="time">{this.props.time}</span>
                <span className="content">{this.props.content}</span>
            </div>
        );
    }
}

class MessageArea extends React.Component {
    render() {
        return (
            <div className="message-area">
                <MessageBox/>
            </div>
        );
    }
}

class MessageBox extends React.Component {
    render() {
        return (
            <div className="message-box">
                <input type="text" className="inputbox"/>
            </div>
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
    render() {
        return (
            <div>
                <Sidebar {...this.props}/>
                <Header name="general"/>
                <Divider/>
                <ChatArea {...this.props}/>
                <MessageArea/>
                <Footer/>
                <WebSocket/>
            </div>
        );
    }
}

class App extends Component {
    render() {
        return (
            <Router>
                <Root content="안녕하세요" time="10:10 PM"/>
            </Router>
        );
    }
}

export default App;