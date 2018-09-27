import React from "react";
import $ from "jquery";
import Avatar from '../sidebar/Avatar';

class NewDirectMessagePopup extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            path: '/images/woman.png',
            name: '',
            email: '',
            showNoResult: false,
            showResult: false
        };
        this.findFriendByEmail = this.findFriendByEmail.bind(this);
        this.addNewDirectMessage = this.addNewDirectMessage.bind(this);
    }

    findFriendByEmail(e) {
        $.ajax({
            url: '/api/users/' + this._inputElement.value,
            method: 'GET',
            contentType: 'application/json'
        }).done(json => {
            if (json === undefined) {
                this.setState(() => {
                    return {
                        showNoResult: true,
                        showResult: false
                    }
                });
            } else {
                this.setState(() => {
                    return {
                        name: json.nickname,
                        email: json.email,
                        showNoResult: false,
                        showResult: true
                    }
                });
            }
        });
        e.preventDefault();
    }

    addNewDirectMessage() {
        this.props.togglePopup();
        $.ajax({
            url: '/api/channels/direct',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({partnerEmail: this.state.email})
        }).done(json => {
            this.props.addNewChannel(json);
        }).fail(data => {
            this.props.addNewChannel(data);
        });
    }

    render() {
        var modalContent = {
            backgroundColor: '#fefefe',
            margin: '15% auto',
            padding: '20px',
            border: '1px solid #888',
            width: '30%'
        };
        var button = {
            margin: '5px'
        };
        var inputStyle = {
            margin: '3px',
            padding: '7px 5px 7px 5px',
            fontSize: '35px',
            width: '80%',
            border: '0px'
        };
        var noFriendDiv = {};
        var resultDiv = {};
        return (
            <div className='popup'>
                <div style={modalContent}>
                    <span className="close" onClick={this.props.togglePopup}>&times;</span>
                    <div>
                        <form onSubmit={this.findFriendByEmail}>
                            <input ref={(a) => this._inputElement = a}
                                   placeholder="이름으로 대화 상대 검색" style={inputStyle}/>
                        </form>
                    </div>
                    {this.state.showNoResult ?
                        <div style={noFriendDiv}>
                            <p>찾으시는 대화 상대가 존재하지 않습니다.</p>
                        </div>
                        : null
                    }
                    {this.state.showResult ?
                        <div style={resultDiv}>
                            <div className="row">
                                <div className="column avatar">
                                    <Avatar path={this.state.path}/>
                                </div>
                                <div className="column name">
                                    {this.state.name}
                                </div>
                            </div>
                            <div style={button}>
                                <button className="btn btn-lg btn-primary" style={button}
                                        onClick={this.addNewDirectMessage}>대화 시작
                                </button>
                                <button className="btn btn-lg btn-default" style={button}
                                        onClick={this.props.togglePopup}>취소
                                </button>
                            </div>
                        </div>
                        : null
                    }

                </div>
            </div>
        );
    }
}

export default NewDirectMessagePopup;