import React from "react";
import $ from "jquery";

class Profile extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            name: '',
            id: ''
        };
    }

    componentDidMount() {
        $.ajax({
            url: '/api/users',
            method: 'GET'
        }).done(json => {
            this.setState({name: json.nickname, id: json.id});
            this.props.setUserId(json.id);
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
                        <a href={"/users/"} target="_blank"><img style={imgStyle} src={this.props.path} alt="Avatar"></img></a>
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

export default Profile;