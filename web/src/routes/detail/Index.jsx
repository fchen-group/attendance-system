import React, { Component } from 'react'
import { Select } from 'antd'
import Slide from './components/Slide'
import './Index.scss'
import DetailTable from './containers/DetailTable'
import { getSemester } from '../../helper/util'
import request from '../../helper/request'

const { Option } = Select

export default class Index extends Component {

  state = {
    historyCourseList: [],
    currentCourseList: [{
      semester: getSemester(),
      semesterId: 0,
      CourseList: []
    }],
    selectedCourseId: '',
    selectedCourseIndex: 0,
    selectedSemesterId: 0,
    signData: [],
    showingData: [],
  }

  componentWillMount() {
    this.loadData()
  }

  componentWillReceiveProps(nextProps) {
    this.getSelectedCourse(nextProps.match.params.courseId)
  }

  loadData = async () => {
    let data = null
    if(window._courseData) {
      data = window._courseData
    } else {
      data = await request('/course')
      window._courseData = data
    }
    if(data) {
      const { currentCourseList, historyCourseList } = data
      if(currentCourseList.length > 0) {
        this.setState({
          currentCourseList: currentCourseList,
          historyCourseList: historyCourseList,
          showingData: currentCourseList[0].CourseList
        }, this.getSelectedCourse)
      } else {
        this.setState({
          historyCourseList: historyCourseList,
          showingData: this.state.currentCourseList[0].CourseList
        }, this.getSelectedCourse)
      }
    }
  }

  handleReload = () => {
    window._courseData = null
    this.loadData()
  }

  getSelectedCourse = (courseId = this.props.match.params.courseId) => {
    // 直接无id进入路由
    if(courseId === undefined) {
      const courseList = this.state.currentCourseList[0].CourseList
      if(courseList.length > 0) {
        this.setState({
          selectedSemesterId: this.state.currentCourseList[0].semesterId,
          selectedCourseId: courseList[0].courseId
        })
      }
    } else {
      // 根据课程id找到对应学期、学期中课程的index
      const result = this.getSemesterFromCourseId(courseId)
      if(result) {
        this.setState({
          selectedSemesterId: result.semesterId,
          selectedCourseIndex: result.courseIndex,
          selectedCourseId: courseId,
          showingData: result.courseList
        })
      } else {
        const courseList = this.state.currentCourseList[0].CourseList
        if(courseList.length > 0) {
          this.setState({
            selectedSemesterId: this.state.currentCourseList[0].semesterId,
            selectedCourseId: courseList[0].courseId,
            selectedCourseIndex: 0
          })
        }
      }
    }
  }

  getSemesterFromCourseId = (courseId) => {
    const { currentCourseList, historyCourseList } = this.state
    const semesterList = currentCourseList.concat(historyCourseList)
    for(let i = 0, slen = semesterList.length; i < slen; i ++) {
      let courseList = semesterList[i].CourseList
      if(courseList) {
        for(let j = 0, clen = courseList.length; j < clen; j ++) {
          if(courseList[j].courseId == courseId) {
            return {
              semesterId: semesterList[i].semesterId,
              courseIndex: j,
              courseList
            }
          }
        }
      }
    }
    return
  }

  handleSelectSemester = (semesterId, option) => {
    this.setState({
      selectedSemesterId: semesterId,
      selectedCourseIndex: 0,
      showingData: option.props["data-course"],
      selectedCourseId: option.props['data-course'][0] ? option.props['data-course'][0].courseId : 0
    })
  }

  handleSelectCourse = (courseId) => {
    this.setState({ selectedCourseId: courseId })
    this.props.history.push(`/detail/${courseId}`)
  }

  render() {
    const { historyCourseList, currentCourseList, selectedSemesterId, selectedCourseIndex, selectedCourseId, showingData } = this.state
    // const semesters = historyCourseList.map(item => ({
    //   semesterId: item.semesterId,
    //   semester: item.semester
    // }))
    const semesters = [...historyCourseList]
    semesters.unshift(...currentCourseList)
    console.log(semesters)
    return (
      <div className="detail">
        <div className="detailWrap">
          <div className="detail__slide">
            <div className="detail__slide--title">
              <Select value={selectedSemesterId} onChange={this.handleSelectSemester}>
                {semesters.map(item => (<Option value={item.semesterId} key={item.semesterId} data-course={item.CourseList}>{item.semester}</Option>))}
              </Select>
            </div>
            <div className="detail__slide--content">
              <Slide index={selectedCourseIndex} data={showingData} onSelect={this.handleSelectCourse} onReload={this.handleReload} />
            </div>
          </div>
          <div className="detail__table">
            <DetailTable courseId={selectedCourseId}/>
          </div>
        </div>
      </div>
    )
  }
}
