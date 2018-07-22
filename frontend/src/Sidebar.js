import React from "react";
import $ from 'jquery';

class Profile extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            name: ''
        };
    }

    componentDidMount() {
        $.ajax({
            url: '/api/users',
            method: 'GET'
        }).done(json => {
            this.setState({name: json.nickname});
        });
    }

    render() {
        var divStyle = {
            display: 'table',
            verticalAlign: 'middle',
            width: '100%',
            paddingLeft: 15,
            paddingBottom: 5,
            color: '#dde3ef',
            fontSize: 19
        };
        var innerDivStyle_1 = {
            display: 'table-cell',
            // verticalAlign: 'middle',
            width: '78%',
        };
        var innerDivStyle_2 = {
            display: 'table-cell',
            verticalAlign: 'top',
            width: '22%'
        };
        var imgStyle = {
            width: '30%',
            borderRadius: '50%'
        };
        var buttonStyle = {
            backgroundColor: 'Transparent',
            backgroundRepeat: 'no-repeat',
            border: 'none',
            cursor: 'pointer',
            overflow: 'hidden',
            outline: 'none'
        };
        return (
            <div style={divStyle}>
                <div>
                    <div style={innerDivStyle_1}>
                        <img style={imgStyle} src={this.props.path} alt="Avatar"></img>
                        <span>
                            <svg className="i-chevron-bottom" viewBox="0 0 32 32" width="10" height="10" fill="none"
                                 stroke="white" stroke-linecap="round" stroke-linejoin="round" stroke-width="2">
                            <path d="M30 12 L16 24 2 12"/>
                            </svg>
                        </span>
                    </div>
                    <div style={innerDivStyle_2}>
                        <button style={buttonStyle}>
                            <svg className="i-bell" viewBox="0 0 32 32" width="28" height="28" fill="none"
                                 stroke="white" stroke-linecap="round" stroke-linejoin="round" stroke-width="2">
                                <path
                                    d="M8 17 C8 12 9 6 16 6 23 6 24 12 24 17 24 22 27 25 27 25 L5 25 C5 25 8 22 8 17 Z M20 25 C20 25 20 29 16 29 12 29 12 25 12 25 M16 3 L16 6"/>
                            </svg>
                        </button>
                    </div>
                </div>
                <div>
                    <svg className="icon">
                        <circle className="circle"/>
                    </svg>
                    &nbsp;<span>{this.state.name}</span>
                </div>
            </div>
        );
    }
}

class QuickSwitcher extends React.Component {
    render() {
        var divStyle = {
            padding: 15
        };
        var buttonStyle = {
            width: '100%',
            paddingTop: 5,
            paddingBottom: 5,
            border: 0,
            outline: 0,
            fontSize: 17,
            borderRadius: 5,
            backgroundColor: '#003462',
            color: '#E2E8F4',
            textAlign: 'left'
        };
        return (
            <div style={divStyle}>
                <button style={buttonStyle}>
                    <svg className="i-search" viewBox="0 0 32 32" width="18" height="18" fill="none"
                         stroke="currentcolor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2">
                        <circle cx="14" cy="14" r="12"/>
                        <path d="M23 23 L30 30"/>
                    </svg>
                    &nbsp;검색
                </button>
            </div>
        );
    }
}

class GroupTag extends React.Component {
    render() {
        var buttonStyle = {
            backgroundColor: 'Transparent',
            backgroundRepeat: 'no-repeat',
            border: 'none',
            cursor: 'pointer',
            overflow: 'hidden',
            outline: 'none',
            padding: '6px 8px 6px 16px',
            fontSize: '20px',
            color: 'rgb(226, 232, 244)'
        };
        var divStyle = {
            display: 'table',
            verticalAlign: 'middle',
            width: '100%'
        };
        var innerDivStyle_1 = {
            display: 'table-cell',
            width: '82%',
        };
        var innerDivStyle_2 = {
            display: 'table-cell',
            verticalAlign: 'middle',
            width: '18%'
        };
        return (
            <div style={divStyle}>
                <div style={innerDivStyle_1}>
                    <button style={buttonStyle}><b>그룹 대화</b></button>
                </div>
                <div style={innerDivStyle_2}>
                    <button style={buttonStyle}>
                        <svg className="i-ban" viewBox="0 0 32 32" width="14" height="14" fill="none"
                             stroke="currentcolor"
                             stroke-linecap="round" stroke-linejoin="round" stroke-width="2">
                            <path d="M16 2 L16 30 M2 16 L30 16"/>
                        </svg>
                    </button>
                </div>
            </div>
        );
    }
}

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
                channels.push(<ChannelItem key={Date.now()} name={channel.name}/>);
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
    render() {
        return (
            <a href="#">{this.props.name}</a>
        );
    }
}

class DmTag extends React.Component {
    render() {
        var buttonStyle = {
            backgroundColor: 'Transparent',
            backgroundRepeat: 'no-repeat',
            border: 'none',
            cursor: 'pointer',
            overflow: 'hidden',
            outline: 'none',
            padding: '6px 8px 6px 16px',
            fontSize: '20px',
            color: 'rgb(226, 232, 244)'
        };
        var divStyle = {
            display: 'table',
            verticalAlign: 'middle',
            width: '100%'
        };
        var innerDivStyle_1 = {
            display: 'table-cell',
            width: '82%',
        };
        var innerDivStyle_2 = {
            display: 'table-cell',
            verticalAlign: 'middle',
            width: '18%'
        };
        return (
            <div style={divStyle}>
                <div style={innerDivStyle_1}>
                    <button style={buttonStyle}><b>연락처</b></button>
                </div>
                <div style={innerDivStyle_2}>
                    <button style={buttonStyle}>
                        <svg className="i-ban" viewBox="0 0 32 32" width="14" height="14" fill="none"
                             stroke="currentcolor"
                             stroke-linecap="round" stroke-linejoin="round" stroke-width="2">
                            <path d="M16 2 L16 30 M2 16 L30 16"/>
                        </svg>
                    </button>
                </div>
            </div>
        );
    }
}

class DmList extends React.Component {
    render() {
        return (
            <div className="list dm">
                <DMItem name="최연정" path="woman.png"/>
                <DMItem name="김세경" path="woman.png"/>
                <DMItem name="오세진" path="man.png"/>
                <DMItem name="홍길동" path="man.png"/>
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

class Avatar extends React.Component {
    render() {
        return (
            <img className="img-avatar" src={this.props.path} alt="Avatar"></img>
        );
    }
}

class Sidebar extends React.Component {
    render() {
        return (
            <div className="sidebar">
                <Profile path="woman.png"/>
                <QuickSwitcher/>
                <GroupTag/>
                <ChannelList/>
                <DmTag/>
                <DmList {...this.props}/>
            </div>
        );
    }
}

export default Sidebar;