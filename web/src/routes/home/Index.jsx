import React, { Component } from 'react'
import { Spin, Button } from 'antd'
import Semester from './components/Semester'
import AddCourseDrawer from './container/AddCourseDrawer'
import request from '../../helper/request'
import { getSemester } from '../../helper/util'
import './Index.scss'

export default class Home extends Component {

  state = {
    data: [{
      semester: getSemester(),
      CourseList: []
    }],
    historyData: [],
    loadingData: false,
    hasMore: true,
    loadedMore: false,
    loadingMore: false,
    drawerShow: false,
  }

  componentWillMount = () => {
    this.loadData()
  }

  loadData = async () => {
    this.setState({ loadingData: true, loadedMore: false })
    const data = await request('/course')
    this.setState({ loadingData: false })
    if(data) {
      if(data.currentCourseList.length !== 0) {
        this.setState({
          data: data.currentCourseList
        })
      }
      if(data.historyCourseList.length === 0) {
        this.setState({
          hasMore: false
        })
      }
      this.setState({
        historyData: data.historyCourseList
      })
      window._courseData = data
    }
  }

  loadMore = () => {
    this.setState({
      loadingMore: true
    })
    this.setState(prevState => ({
      loadingMore: false,
      loadedMore: true,
      data: prevState.data.concat(prevState.historyData)
    }))
  }

  foldUp = () => {
    this.setState(prevState => ({
      data: prevState.data.slice(0, 1),
      loadedMore: false
    }))
  }

  showDrawer = () => {
    this.setState({
      drawerShow: true
    })
  }

  closeDrawer = () => {
    this.setState({
      drawerShow: false
    })
  }

  render() {
    const { data, hasMore, loadedMore, loadingMore, drawerShow, loadingData } = this.state
    return (
      <div className="home">
        <div className="home__content">
          <Button type="primary" icon="plus" size="large" className="home__btn--add" onClick={this.showDrawer}>添加课程</Button>
          <Spin spinning={loadingData}>
            <ul className="home__list">
              {data.map((item, index) => (
                <li key={item.semester}>
                  <Semester title={item.semester} courses={item.CourseList} onReload={this.loadData} onAddCourse={index === 0 ? this.showDrawer : undefined}/>
                </li>)) }
            </ul>
          </Spin>
          <div className="home__more">
            {loadedMore ? (
              <span>无更多课程信息，<span className="home__moreText" onClick={this.foldUp}>收起</span></span>
            ) : loadingMore ? (
              <Spin tip="加载中..." />
            ) : hasMore ? (
              <span className="home__moreText" onClick={this.loadMore}>更多课程信息...</span>
            ) : null }
          </div>
          <AddCourseDrawer visible={drawerShow} onClose={this.closeDrawer} onReload={this.loadData} />
        </div>
      </div>
    )
  }
}
