import React from 'react'

class FrameTest extends React.Component {
  render() {
    let style = {
      width: this.props.width - 2,
      height: this.props.height,
      borderStyle: 'none'
    };
    return (
      <iframe style={style} src={this.props.url}>TEST</iframe>
    );
  }
}

export default FrameTest;
