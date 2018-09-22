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
        this.setState((prevState) => {
            return {
                items: prevState.items.filter(item => item.props.id != cId)
            };
        });
    }

    componentDidMount() {
        $.ajax({
            url: '/api/channels',
            method: 'GET'
        }).done(json => {
            var channels = [];
            for (let channel of json) {
                channels.push(<ChannelItem switchChannel={this.props.switchChannel} key={Date.now() + channel.id}
                                           name={channel.name} id={channel.id}
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
    render() {
        return (
            <a href="#">
                <div className="row">
                    <div className="column avatar">
                        <Avatar {...this.props}/>
                    </div>
                    <div className="column name">
                        {this.props.name}
                    </div>
                    <div className="column status">
                        <svg className="icon">
                            <circle className="circle"/>
                        </svg>
                    </div>
                </div>
            </a>
        );
    }
}

export default DmList;