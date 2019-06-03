import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Modal, Popconfirm, Button,  } from 'antd'
import CourseForm from '../components/CourseForm'
import request from '../helper/request'

export default class CourseModal extends Component {
  static propTypes = {
    visible: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    courseData: PropTypes.object.isRequired,
    onReload: PropTypes.func.isRequired
  }

  state = {
    modifyingCourse: false
  }

  handleDelete = async () => {
    const { onClose, onReload } = this.props
    const data = await request(`/course/${this.props.courseData.courseId}`, { method: 'DELETE' })
    if(data) {
      onClose()
      onReload()
    }
  }

  handleCancel = () => {
    this.props.onClose()
  }

  handleModify = () => {
    this.setState({ modifyingCourse: true })
    this.child.props.form.validateFields((err, values) => {
      this.setState({ modifyingCourse: false })
      if(err) {
        return
      }
      this.modifyCourse(values)
    })
  }

  modifyCourse = async (values) => {
    const { courseData, onClose, onReload } = this.props
    const { courseNum, courseName } = values
    const time = values.keys.map(i => values.time[i])
    const obj = { courseNum, courseName, time }
    const data = await request(`/course/${courseData.courseId}`, {
      method: 'PUT',
      body: JSON.stringify(obj)
    })
    if(data) {
      onClose()
      onReload()
    }
  }

  handleRef = (ref) => this.child = ref

  render() {
    const { visible, courseData } = this.props
    return (
      <Modal
        title="课程信息"
        width={640}
        visible={visible}
        onCancel={this.props.onClose}
        footer={[
          <Popconfirm key="delete" placement="topLeft" title="您确定要删除该课程吗?" onConfirm={this.handleDelete}>
            <Button type="danger" style={{ float: 'left' }}>
              删除课程
            </Button>
          </Popconfirm>,
          <Button key="cancel" onClick={this.handleCancel}>取消</Button>,
          <Button key="submit" type="primary" loading={this.state.modifyingCourse} onClick={this.handleModify}>
            修改
          </Button>
        ]}
      >
        <CourseForm data={courseData} onRef={this.handleRef}/>
      </Modal>
    )
  }
}
