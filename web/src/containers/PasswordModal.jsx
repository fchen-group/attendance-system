import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Modal, message } from 'antd'
import PasswordForm from '../components/PasswordForm'
import request from '../helper/request'

export default class PasswordModal extends Component {
  static propTypes = {
    visible: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
  }

  state = {
    modifying: false
  }

  handleRef = (ele) => this.child = ele

  handleModifyPassword = () => {
    this.child.props.form.validateFields((err, values) => {
      if(err) {
        return
      }
      this.modifyPassword(values)
    })
  }

  modifyPassword = async (values) => {
    this.setState({ modifying: true })
    const { confirmPassword, ...obj } = values
    const data = await request('/user/password', {
      method: 'PUT',
      body: JSON.stringify(obj)
    })
    this.setState({ modifying: false })
    if(data) {
      message.success('修改成功')
    } else {

    }
  }

  render() {
    const { visible, onClose } = this.props
    return (
      <Modal
        title="修改密码"
        width={480}
        visible={visible}
        okText="修改"
        confirmLoading={this.state.modifying}
        onOk={this.handleModifyPassword}
        onCancel={onClose}
      >
        <PasswordForm onRef={this.handleRef} />
      </Modal>
    )
  }
}
