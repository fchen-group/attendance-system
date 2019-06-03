import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Icon, Tooltip } from 'antd'
import { weekMap } from '../helper/util'
import './Card.scss'

export default class Card extends Component {
  static propTypes = {
    data: PropTypes.object.isRequired,
    showCourse: PropTypes.func.isRequired,
    showStudent: PropTypes.func.isRequired,
    showQrcode: PropTypes.func.isRequired,
    showDetail: PropTypes.func.isRequired
  }

  render() {
    const { showCourse, data, showStudent, showQrcode, showDetail } = this.props
    const { courseId, courseName, time } = data
    return (
      <div className={`courseCard courseCard--bg${courseId % 10}`}>
        {/* <img class="courseCard__bg" src="/source/circle.svg" alt=""/> */}
        <div className="courseCard__info">
          <h3 className="courseCard__title">{courseName}</h3>
          <p className="courseCard__address">{time[0].loc}</p>
          <p className="courseCard__time">星期{weekMap[time[0].weekday]} 第{time[0].start}节~第{time[0].end}节</p>
        </div>
        <ul className="courseCard__operation">
          <li>
            <Tooltip placement="left" title={'发起签到'}>
              <Icon type="check" onClick={showQrcode} />
            </Tooltip>
          </li>
          <li>
            <Tooltip placement="left" title={'学生管理'}>
              <Icon type="team" onClick={showStudent} />
            </Tooltip>
          </li>
          <li>
            <Tooltip placement="left" title={'签到详情'}>
              <Icon type="bar-chart" onClick={showDetail} />
              </Tooltip>
          </li>
          <li>
            <Tooltip placement="left" title={'课程信息'}>
              <Icon type="edit" onClick={showCourse} />
            </Tooltip>
          </li>
        </ul>
      </div>
    )
  }
}
