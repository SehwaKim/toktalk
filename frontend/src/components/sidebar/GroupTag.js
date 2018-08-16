import React from "react";

class GroupTag extends React.Component {
    constructor(props) {
        super(props);
    }

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
                    <button style={buttonStyle} onClick={this.props.togglePopup}>
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

export default GroupTag;