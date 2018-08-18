import React from "react";

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

export default QuickSwitcher;