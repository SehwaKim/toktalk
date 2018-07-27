import React from "react";
import $ from "jquery";
import {
    NavLink
} from "react-router-dom";

class ChannelList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            items: []
        };
        this.addItem = this.addItem.bind(this);
    }

    addItem(e) {
        /*if (this._inputElement.value !== "") {
            var newItem = {
                text: this._inputElement.value,
                key: Date.now()
            };

            this.setState((prevState) => {
                return {
                    items: prevState.items.concat(newItem)
                };
            });

            this._inputElement.value = "";
        }*/

        e.preventDefault();
    }

    componentDidMount() {
        $.ajax({
            url: '/api/channels',
            method: 'GET'
        }).done(json => {
            var channels = [];
            for (let channel of json) {
                channels.push(<ChannelItem switchChannel={this.props.switchChannel} key={Date.now() + channel.id}
                                           name={channel.name} id={channel.id}/>);
                this.props.addChatArea(channel.id);
            }
            ;
            this.setState({items: channels});
        });
    }

    render() {
        return (
            <div className="list channels">
                {this.state.items}
            </div>
        );
    }
}

class ChannelItem extends React.Component {
    constructor(props) {
        super(props);
        this.switch = this.switch.bind(this);
    }

    switch(e) {
        this.props.switchChannel(this.props.id);
    }

    render() {
        return (
            <NavLink exact to={'/' + this.props.id} onClick={(e) => this.switch(e)}>{this.props.name}</NavLink>
        );
    }
}

export default ChannelList;