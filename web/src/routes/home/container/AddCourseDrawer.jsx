import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Drawer, Button, message } from 'antd'
import CourseForm from '../../../components/CourseForm'
import StudentTable from '../../../components/StudentTable'
import './AddCourseDrawer.scss'
import { getDuplicateInArray } from '../../../helper/util'
import request from '../../../helper/request'

class AddCourseDrawer extends Component {
  static propTypes = {
    visible: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    onReload: PropTypes.func.isRequired,
  }

  state = {
    students: [],
  }

  onRef = ref => this.child = ref

  setStudents = stus => this.setState({ students: stus })

  handleAddCourse = (e) => {
    e.preventDefault()
    this.child.props.form.validateFields((err, values) => {
      if(!err) {
        const stus = this.state.students
        const stuNumArr = stus.map(i => i.studentNum)
        const result = getDuplicateInArray(stuNumArr)
        if(result.length > 0) {
          message.open({
            content: `创建失败，添加的学生中有重复的学号：${result.join(',')}`,
            duration: 10,
            type: 'error'
          })
          return
        } else {
          this.addCourse(values)
        }

      }
    })
  }

  addCourse = async (values) => {
    const { courseName, courseNum } = values
    const time = values.keys.map(i => values.time[i])
    const students = this.state.students.map(item => ({
      name: item.name,
      classInfo: item.classInfo,
      studentNum: item.studentNum
    }))
    const obj = { courseName, courseNum, time, students }
    const data = await request('/course', {
      method: 'POST',
      body: JSON.stringify(obj)
    })
    if(data) {
      this.props.onReload()
      this.props.onClose()
      this.setState({ students: [] })
    }
  }

  render() {
    const { students } = this.state
    const { onClose, visible } = this.props
    return (
      <Drawer
        className="addCourseForm"
        title="添加课程"
        width={760}
        placement="right"
        onClose={onClose}
        maskClosable={false}
        destroyOnClose={true}
        visible={visible}
        style={{ height: 'calc(100% - 55px)', overflow: 'auto', paddingBottom: 53 }}
      >
        <div className="addCourseDrawer__course">
          <CourseForm onRef={this.onRef} />
        </div>
        <div className="addCourseDrawer__student">
          <p>课程学生</p>
          <StudentTable dataSource={students} setStudents={this.setStudents} />
        </div>
        <div className="addCourseDrawer__btnGroup">
          <Button style={{ marginRight: 8 }} onClick={onClose}>取消</Button>
          <Button type="primary" onClick={this.handleAddCourse}>创建</Button>
        </div>
      </Drawer>
    )
  }
}

export default AddCourseDrawer
