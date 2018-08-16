import React from "react";
import $ from "jquery";
import {
    NavLink
} from "react-router-dom";
import Avatar from './Avatar';

class DmList extends React.Component {
    render() {
        return (
            <div className="list dm">
                <DMItem name="최연정" path="/images/woman.png"/>
                <DMItem name="김세경" path="/images/woman.png"/>
                <DMItem name="오세진" path="/images/man.png"/>
                <DMItem name="홍길동" path="/images/man.png"/>
            </div>
        );
    }
}

class DMItem extends React.Component {
    render() {
        return (
            <a href="#">
                <div className="row">
                    <div className="column avatar">
                        <Avatar {...this.props}/>
                    </div>
                    <div className="column name">
                        {this.props.name}
                    </div>
                    <div className="column status">
                        <svg className="icon">
                            <circle className="circle"/>
                        </svg>
                    </div>
                </div>
            </a>
        );
    }
}

export default DmList;