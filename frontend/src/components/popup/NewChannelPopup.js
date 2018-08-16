import React from "react";
import $ from "jquery";

class NewChannelPopup extends React.Component {
    constructor(props) {
        super(props);
        this.createNewChannel = this.createNewChannel.bind(this);
    }

    createNewChannel() {
        this.props.togglePopup();
        $.ajax({
            url: '/api/channels',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({name: this._inputBox.value, type: 'public'})
        }).done(json => {
            this.props.addNewChannel(json);
        });
    }

    render() {
        var inputStyle = {
            borderRadius: '4px',
            width: '30%',
            padding: '22px 20px',
            margin: '8px 0',
            display: 'inline-block',
            border: '1px solid #ccc',
            boxSizing: 'border-box',
            fontSize: '18px'
        };
        var button = {
            margin: '5px'
        };
        return (
            <div className='popup'>
                <div className='popup_newchannel'>
                    <div>
                        <h2>새 그룹 대화 시작</h2>
                    </div>
                    <div>
                        <form onSubmit={this.createNewChannel}>
                            <div>
                                <input style={inputStyle} ref={(el) => this._inputBox = el} placeholder="제목 입력 (필수)"/>
                            </div>
                            <div>
                                <input style={inputStyle} placeholder="초대할 친구 검색"/>
                            </div>
                            <div style={button}>
                                <button className="btn btn-lg btn-primary" style={button} type="submit">시작하기</button>
                                <button className="btn btn-lg btn-default" style={button}
                                        onClick={this.props.togglePopup}>취소
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        );
    }
}

// <button disabled>시작하기</button>
export default NewChannelPopup;