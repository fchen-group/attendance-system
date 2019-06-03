import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Modal, message } from 'antd'
import AddTeacherForm from '../components/AddTeacherForm'
import request from '../../../helper/request'

export default class AddTeacherModal extends Component {
  static propTypes = {
    visible: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    onReload: PropTypes.func.isRequired
  }

  state = {
    modifying: false
  }

  handleRef = (ele) => this.child = ele

  handleAddTeacher = () => {
    this.setState({ modifying: true })
    this.child.props.form.validateFields((err, values) => {
      this.setState({ modifying: false })
      if(err) {
        return
      }
      if(values.password === undefined) {
        values.password = values.teacherNum
      }
      this.addTeacher(values)
    })
  }

  addTeacher = async (values) => {
    const { teacherNum, name, college, password } = values
    const data = await request('/course/teacher/addTeacher', {
      method: 'POST',
      body: JSON.stringify({teacherNum, name, college, password})
    })
    if(data) {
      message.success('添加成功！')
      this.props.onClose()
      this.props.onReload()
    }
  }

  render() {
    const { visible, onClose } = this.props
    return (
      <Modal
        title="添加教师"
        visible={visible}
        destroyOnClose={true}
        maskClosable={false}
        okText="添加"
        confirmLoading={this.state.modifying}
        onOk={this.handleAddTeacher}
        onCancel={onClose}
      >
        <AddTeacherForm onRef={this.handleRef} />
      </Modal>
    )
  }
}
