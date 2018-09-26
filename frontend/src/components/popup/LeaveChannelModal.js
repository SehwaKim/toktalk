import React from "react";
import $ from "jquery";

class LeaveChannelModal extends React.Component {
    constructor(props) {
        super(props);
        this.leaveChannel = this.leaveChannel.bind(this);
    }

    leaveChannel() {
        this.props.togglePopup();
        if (this.props.cType == 'PUBLIC') {
            $.ajax({
                url: '/api/channelUsers/' + this.props.userId,
                method: 'DELETE',
                contentType: 'application/json',
                data: JSON.stringify({channelId: this.props.cId})
            }).done(json => {
                this.props.removeItem(this.props.cId);
                this.props.switchChannel(0, '');
            });
        }
        if (this.props.cType == 'DIRECT') {
            this.props.removeItem(this.props.cId);
            this.props.switchChannel(0, '');
        }
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
        return (
            <div className='popup'>
                <div style={modalContent}>
                    <span className="close" onClick={this.props.togglePopup}>&times;</span>
                    <p>정말 나가실겁니까?</p>
                    <div style={button}>
                        <button className="btn btn-lg btn-primary" style={button}
                                onClick={this.leaveChannel}>나가기
                        </button>
                        <button className="btn btn-lg btn-default" style={button}
                                onClick={this.props.togglePopup}>취소
                        </button>
                    </div>
                </div>
            </div>
        );
    }
}

export default LeaveChannelModal;