import React from "react";

class Avatar extends React.Component {
    render() {
        return (
            <img className="img-avatar" src={this.props.path} alt="Avatar"></img>
        );
    }
}

export default Avatar;