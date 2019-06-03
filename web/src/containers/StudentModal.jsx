import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Modal, message } from 'antd'
import StudentTable from '../components/StudentTable'
import request from '../helper/request'

export default class StudentModal extends Component {
  static propTypes = {
    visible: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    courseId: PropTypes.number.isRequired,
  }

  state = {
    studentsData: []
  }

  componentWillReceiveProps(nextProps) {
    if(nextProps.visible === true) {
      this.loadStudents(nextProps.courseId)
    }
  }

  loadStudents = async () => {
    const data = await request(`/course/${this.props.courseId}/student`)
    if(data) {
      const studentsData = data.studentList.map(item => ({ ...item, key: item.studentNum }))
      this.setState({ studentsData })
    }
  }

  setStudents = stus => this.setState({ studentsData: stus })

  handleAddStudent = async (row) => {
    const data = await request(`/course/${this.props.courseId}/student`, {
      method: 'POST',
      body: JSON.stringify(row)
    })
    if(data) {
      message.success('添加成功')
    }
  }

  handleSaveStudent = async (stuId, row) => {
    const data = await request(`/course/student/${stuId}`, {
      method: 'PUT',
      body: JSON.stringify(row)
    })
    if(data) {
      message.success('修改成功')
    }
  }

  handleDeleteStudent = async (stuId) => {
    let data = null
    // 批量
    if(stuId.length) {
      data = await request(`/course/${this.props.courseId}/studentList`, {
        method: 'DELETE',
        body: JSON.stringify({students: stuId})
      })
    // 单个
    } else {
      data = await request(`/course/${this.props.courseId}/student/${stuId}`, { method: 'DELETE' })
    }
    if(data) {
      message.success('删除成功')
    }
  }

  handleResetPassword = async (stuId) => {
    const data = await request(`/course/student/resetPassword/${stuId}`, { method: 'PUT' })
    if(data) {
      message.success('重置密码成功')
    }
  }

  handleUntyingWx = async (stuId) => {
    const data = await request(`/course/student/deleteOpenId/${stuId}`, { method: 'PUT' })
    if(data) {
      message.success('解绑微信号成功')
    }
  }

  handleImportStudent = async (file, cb) => {
    const formData = new FormData()
    formData.append('file', file)
    const data = await request(`/course/${this.props.courseId}/student`, {
      method: 'POST',
      body: formData
    })
    if(data) {
      message.success('导入成功')
    }
    cb()
    this.loadStudents()
  }

  render() {
    const { visible, onClose } = this.props
    return (
      <Modal
        title="学生管理"
        width={720}
        visible={visible}
        destroyOnClose={true}
        onOk={() => onClose()}
        onCancel={() => onClose()}
      >
        <StudentTable
          dataSource={this.state.studentsData}
          setStudents={this.setStudents}
          onAdd={this.handleAddStudent}
          onDelete={this.handleDeleteStudent}
          onSave={this.handleSaveStudent}
          onImport={this.handleImportStudent}
          onResetPassword={this.handleResetPassword}
          onUntyingWx={this.handleUntyingWx}
        />
      </Modal>
    )
  }
}
