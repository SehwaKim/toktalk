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
            current: 0,
            userId: 0
        };
        this.chatAreas = new Map();
        this.addItem = this.addItem.bind(this);
        this.sendMessage = this.sendMessage.bind(this);
        this.printMessage = this.printMessage.bind(this);
        this.switchChannel = this.switchChannel.bind(this);
        this.markAsUnread = this.markAsUnread.bind(this);
    }

    setUserId(id) {
        this.setState(() => {
            return {
                userId: id
            }
        });
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

    removeItem(cId) {
        this.setState((prevState) => {
            return {
                routes: prevState.routes.filter(item => item.props.cId != cId)
            }
        });
        this.channels.removeChannel(cId);
        this.directChannels.removeChannel(cId);
    }

    sendMessage(cId, cType, text) {
        this.sock.sendChatMsg(cId, cType, text);
    }

    switchChannel(cId, title, cType) {
        this.setState(() => {
            return {
                current: cId
            };
        });
        this.input.box.switchChannel(cId, cType);
        this.sock.switchChannel(cId);
        this.head.switchChannel(title, cType);
    }

    printMessage(message) {
        this.chatAreas.get(message.channelId).addItem(message);
    }

    markAsUnread(cId) {
        if (this.directChannels.itemRefs.has(cId)) {
            this.directChannels.itemRefs.get(cId).increaseUnread();
        } else {
            this.channels.itemRefs.get(cId).increaseUnread();
        }
    }

    addNewChannel(channel) {
        if ('DIRECT' == channel.type) {
            if (!this.directChannels.itemRefs.has(channel.id)) {
                this.directChannels.addNewChannel(channel);
                if (channel.name == channel.secondUserName) {
                    this.sock.notifyInvitation(channel.id, channel.secondUserId);
                }
            }
        }
        if ('PUBLIC' == channel.type) {
            this.channels.addNewChannel(channel);
        }
    }

    render() {
        return (
            <HashRouter>
                <div>
                    <div className="sidebar">
                        <Profile path="/images/woman.png" setUserId={this.setUserId.bind(this)}/>
                        <QuickSwitcher/>
                        <GroupTag addNewChannel={this.addNewChannel.bind(this)}/>
                        <ChannelList ref={channels => this.channels = channels} addChatArea={this.addItem}
                                     switchChannel={this.switchChannel}/>
                        <DmTag addNewChannel={this.addNewChannel.bind(this)}/>
                        <DmList ref={directChannels => this.directChannels = directChannels} addChatArea={this.addItem}
                                switchChannel={this.switchChannel}/>
                    </div>
                    <Header ref={head => this.head = head} userId={this.state.userId} cId={this.state.current}
                            removeItem={this.removeItem.bind(this)} switchChannel={this.switchChannel}/>
                    <Divider/>
                    {this.state.routes}
                    <InputArea ref={input => this.input = input} sendMessage={this.sendMessage}/>
                    <Footer/>
                    <WebSocket ref={sock => this.sock = sock} printMessage={this.printMessage}
                               markAsUnread={this.markAsUnread} addNewChannel={this.addNewChannel.bind(this)}/>
                </div>
            </HashRouter>
        );
    }
}

export default App;