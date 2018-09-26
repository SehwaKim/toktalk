import React from "react";
import LeaveChannelModal from '../popup/LeaveChannelModal';

class Header extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            title: '',
            cType: '',
            showLeaveChannelModal: false
        };
        this.switchChannel = this.switchChannel.bind(this);
        this.toggleLeaveChannelModal = this.toggleLeaveChannelModal.bind(this);
    };

    switchChannel(title, cType) {
        this.setState(() => {
            return {
                title: title,
                cType: cType
            };
        });
    }

    toggleLeaveChannelModal() {
        this.setState((prevState) => {
            return {
                showLeaveChannelModal: !prevState.showLeaveChannelModal
            }
        });
    }

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
                    <div className="dropdown">
                        <button style={{...buttonStyle, ...titleStyle}}>{this.state.title}</button>
                        <div className="dropdown-content">
                            <button style={buttonStyle} onClick={this.toggleLeaveChannelModal}>나가기</button>
                        </div>
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
                {this.state.showLeaveChannelModal ?
                    <LeaveChannelModal
                        togglePopup={this.toggleLeaveChannelModal} cId={this.props.cId} cType={this.state.cType}
                        userId={this.props.userId} removeItem={this.props.removeItem}
                        switchChannel={this.props.switchChannel}
                    />
                    : null
                }
            </div>
        );
    }
}

export default Header;