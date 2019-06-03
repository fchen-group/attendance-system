import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Tooltip } from 'antd'
import './Collapse.scss'

export default class Collapse extends Component {
  static propTypes = {
    top: PropTypes.any,
    bottom: PropTypes.any,
  }

  state = {
    isCollapse: false
  }

  collapseDom = React.createRef()

  toggleContent = () => {
    this.setState((prevState) => ({
      isCollapse: !prevState.isCollapse
    }))
  }

  render() {
    const { isCollapse } = this.state
    const maxHeight = isCollapse ? '0' : 'auto'
    const tip = isCollapse ? '点击展开' : '点击折叠'

    return (
      <div className="collapse">
        <Tooltip placement="topLeft" title={tip}>
          <div className="collapse__top" onClick={this.toggleContent}>
            {this.props.top}
          </div>
        </Tooltip>
        <div className="collapse__bottom" ref={this.collapseDom} style={{ height: maxHeight }}>
          {this.props.bottom}
        </div>
      </div>
    )
  }
}
