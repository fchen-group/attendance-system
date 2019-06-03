import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Row, Col } from 'antd'
import Card from '../../../containers/Card'
import Collapse from '../../../components/Collapse'
import './Semester.scss'

export default class Semester extends Component {
  static propTypes = {
    title: PropTypes.string.isRequired,
    courses: PropTypes.array.isRequired,
    onReload: PropTypes.func.isRequired,
    onAddCourse: PropTypes.func
  }

  render() {
    const { title, courses, onReload, onAddCourse } = this.props
    return (
      <div className="semester">
        <Collapse
          top={
            <h2 className="semester__title">{title}</h2>
          }
          bottom={
            <div className="semester__content">
              <Row gutter={{ xs: 8, sm: 16, md: 24, lg: 32 }}>
                {courses.map(item => (
                  <Col xs={24} sm={12} md={8} lg={6} xl={6} className="cardWrap" key={item.courseId}>
                    <Card data={item} onReload={onReload} />
                  </Col>))}
                  {onAddCourse &&
                    <Col xs={24} sm={12} md={8} lg={6} xl={6} className="cardWrap">
                      <div className="semester__example" onClick={onAddCourse}></div>
                    </Col>
                  }
              </Row>
            </div>
          } />
      </div>
    )
  }
}
