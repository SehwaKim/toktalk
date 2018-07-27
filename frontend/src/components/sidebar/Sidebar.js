import React from "react";
import Profile from './Profile';
import ChannelList from './ChannelList';
import DmList from './DmList';

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

class Sidebar extends React.Component {
    render() {
        return (
            <div className="sidebar">
                <Profile path="woman.png"/>
                <QuickSwitcher/>
                <GroupTag/>
                <ChannelList {...this.props}/>
                <DmTag/>
                <DmList {...this.props}/>
            </div>
        );
    }
}

export default Sidebar;