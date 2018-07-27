import React from "react";

class Footer extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            message: ""
        };
    }

    render() {
        return (
            <div className="footer">
                <span>{this.state.message}</span>
            </div>
        );
    }
}

export default Footer