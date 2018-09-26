import React from "react";
import $ from "jquery";
import {
    NavLink
} from "react-router-dom";
import Avatar from './Avatar';

class DmList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            items: []
        };
        this.itemRefs = new Map();
        this.addNewChannel = this.addNewChannel.bind(this);
        this.removeChannel = this.removeChannel.bind(this);
    }

    addNewChannel(channel) {
        this.setState((prevState) => {
            return {
                items: prevState.items.concat(<DMItem switchChannel={this.props.switchChannel}
                                                      key={Date.now() + channel.id}
                                                      name={channel.name} id={channel.id}
                                                      path="/images/woman.png"
                                                      ref={(el => this.itemRefs.set(channel.id, el))}/>)
            };
        });
        this.props.addChatArea(channel.id);
    }

    removeChannel(cId) {
        this.itemRefs.set(cId, null);
        this.setState((prevState) => {
            return {
                items: prevState.items.filter(item => item.props.id != cId)
            };
        });
    }

    componentDidMount() {
        $.ajax({
            url: '/api/channels/direct',
            method: 'GET'
        }).done(json => {
            var channels = [];
            for (let channel of json) {
                channels.push(<DMItem switchChannel={this.props.switchChannel} key={Date.now() + channel.id}
                                      name={channel.name} id={channel.id}
                                      path="/images/woman.png"
                                      ref={(el => this.itemRefs.set(channel.id, el))}/>);
                this.props.addChatArea(channel.id);
            }
            this.setState({items: channels});
        });
    }

    render() {
        return (
            <div className="list dm">
                {this.state.items}
            </div>
        );
    }
}

class DMItem extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            unread: ''
        };
        this.switch = this.switch.bind(this);
        this.increaseUnread = this.increaseUnread.bind(this);
    }

    componentDidMount() {
        $.ajax({
            url: '/api/messages/unread',
            method: 'GET',
            data: {channelId: this.props.id}
        }).done(json => {
            if (json != null) {
                this.setState({unread: json.unreadCnt});
            }
        });
    }

    increaseUnread() {
        let current = this.state.unread;
        if ('' == current) {
            current = 0;
        }
        this.setState({unread: current + 1});
    }

    switch() {
        this.setState({unread: ''});
        this.props.switchChannel(this.props.id, this.props.name, 'DIRECT');
    }

    render() {
        var divStyle = {
            textAlign: 'right',
            paddingRight: '10px'
        };
        return (
            <NavLink exact to={'/' + this.props.id} onClick={() => this.switch()}>
                <div className="row">
                    <div className="column avatar">
                        <Avatar {...this.props}/>
                    </div>
                    <div className="column status">
                        <svg className="icon">
                            <circle className="circle"/>
                        </svg>
                    </div>
                    <div className="column name">
                        {this.props.name}
                    </div>
                    <div style={divStyle} className="column">
                        <span className="badge">{this.state.unread}</span>
                    </div>
                </div>
            </NavLink>
        );
    }
}

export default DmList;