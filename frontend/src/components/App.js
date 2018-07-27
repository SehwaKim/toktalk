import React from 'react';
import {Route, HashRouter} from "react-router-dom";

import Profile from './sidebar/Profile';
import GroupTag from './sidebar/GroupTag';
import ChannelList from './sidebar/ChannelList';
import DmTag from './sidebar/DmTag';
import DmList from './sidebar/DmList';
import QuickSwitcher from './sidebar/QuickSwitcher';
import Header from './header/Header';
import ChatArea from './chat/ChatArea';
import InputArea from './input/InputArea';
import Footer from './footer/Footer';
import WebSocket from './websocket/WebSocket';

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

class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            routes: [],
            current: 0
        };
        this.chatAreas = new Map();
        this.addItem = this.addItem.bind(this);
        this.sendMessage = this.sendMessage.bind(this);
        this.printMessage = this.printMessage.bind(this);
        this.switchChannel = this.switchChannel.bind(this);
        this.markAsUnread = this.markAsUnread.bind(this);
    }

    addItem(cId) {
        this.setState((prevState) => {
            return {
                routes: prevState.routes.concat(<Route exact path={'/' + cId} cId={cId} key={Date.now() + cId + cId}
                                                       render={() => <ChatArea key={Date.now() + cId} cId={cId}
                                                                               ref={(el => this.chatAreas.set(cId, el))}/>}/>)
            }
        });
    }

    sendMessage(cId, text) {
        this.sock.sendChatMsg(cId, text);
    }

    switchChannel(cId) {
        this.setState(() => {
            return {
                current: cId
            };
        });
        this.input.box.switchChannel(cId);
        this.sock.switchChannel(cId);
    }

    printMessage(message) {
        this.chatAreas.get(message.channelId).addItem(message);
    }

    markAsUnread(cId) {
        this.channels.itemRefs.get(cId).increaseUnread();
    }

    render() {
        return (
            <HashRouter>
                <div>
                    <div className="sidebar">
                        <Profile path="woman.png"/>
                        <QuickSwitcher/>
                        <GroupTag/>
                        <ChannelList ref={channels => this.channels = channels} addChatArea={this.addItem}
                                     switchChannel={this.switchChannel}/>
                        <DmTag/>
                        <DmList {...this.props}/>
                    </div>
                    <Header name="general"/>
                    <Divider/>
                    {this.state.routes}
                    <InputArea ref={input => this.input = input} sendMessage={this.sendMessage}/>
                    <Footer/>
                    <WebSocket ref={sock => this.sock = sock} printMessage={this.printMessage}
                               markAsUnread={this.markAsUnread}/>
                </div>
            </HashRouter>
        );
    }
}

export default App;