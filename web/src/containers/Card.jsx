import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Card from '../components/Card'
import CourseModal from './CourseModal'
import StudentModal from './StudentModal'
import QrcodeModal from './QrcodeModal'
import { withRouter } from 'react-router-dom'

class CardContainer extends Component {
  static propTypes = {
    data: PropTypes.object.isRequired,
    onReload: PropTypes.func.isRequired
  }

  state = {
    courseShow: false,
    studentShow: false,
    qrcodeShow: false
  }

  handleCloseCourse = () => {
    this.setState({ courseShow: false })
  }

  handleShowCourse = () => {
    this.setState({ courseShow: true })
  }

  handleCloseStudent = () => {
    this.setState({ studentShow: false })
  }

  handleShowStudent = () => {
    this.setState({ studentShow: true })
  }

  handleCloseQrcode = () => {
    this.setState({ qrcodeShow: false })
  }

  handleShowQrcode = () => {
    this.setState({ qrcodeShow: true })
  }

  handleShowDetail = (e) => {
    e.stopPropagation()
    if(this.props.match.params.courseId != this.props.data.courseId) {
      this.props.history.push(`/detail/${this.props.data.courseId}`)
    }
  }

  render() {
    const { data, onReload } = this.props
    const { courseShow, studentShow, qrcodeShow } = this.state
    return (
      <div>
        <Card data={data} showCourse={this.handleShowCourse} showStudent={this.handleShowStudent} showQrcode={this.handleShowQrcode} showDetail={this.handleShowDetail} />
        <CourseModal visible={courseShow} courseData={data} onClose={this.handleCloseCourse} onReload={onReload} />
        <StudentModal visible={studentShow} onClose={this.handleCloseStudent} courseId={data.courseId} />
        <QrcodeModal visible={qrcodeShow} onClose={this.handleCloseQrcode} courseData={data}/>
      </div>
    )
  }
}

export default withRouter(CardContainer)
