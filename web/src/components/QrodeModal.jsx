import React, { Component } from 'react'
import PropTypes from 'prop-types'
import QRCode from 'qrcode.react'
import { Modal, Button, Spin } from 'antd'

export default class QrcodeModal extends Component {
  static propTypes = {
    visible: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    url: PropTypes.string.isRequired
  }

  state = {
    innerHeight: window.innerHeight,
  }

  componentDidMount() {
    window.addEventListener('resize', this.handleClientResize)
  }

  componentWillUnmount() {
    window.removeEventListener('resize', this.handleClientResize)
  }

  handleClientResize = () => {
    this.setState({ innerHeight: window.innerHeight })
  }

  render() {
    const { visible, onClose, url } = this.props
    const { innerHeight } = this.state
    return (
      <Modal
        width={innerHeight - 112}
        style={{ top: 20 }}
        visible={visible}
        closable={false}
        maskClosable={false}
        footer={
          <Button type="primary" onClick={onClose}>结束签到</Button>
        }
      >
        <Spin spinning={url == ''}>
          <QRCode value={url} size={innerHeight - 160} />
        </Spin>
      </Modal>
    )
  }
}
