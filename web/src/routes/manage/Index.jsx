import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Tabs, Icon, Table, Button, Input, Popconfirm, message, Spin, Divider } from 'antd'
import { ReactComponent as TeacherSvg } from './images/teacher.svg'
import AddTeacherModel from './containers/AddTeacherModal'
import request from '../../helper/request'
import './Index.scss'

const { TabPane } = Tabs
const { Search } = Input

export default class Manage extends Component {
  static propTypes = {
    prop: PropTypes
  }

  state = {
    loadingData: false,
    teacherData: [],
    addTeacherShow: false,
  }

  componentWillMount = () => {
    this.loadData()
  }

  loadData = async () => {
    this.setState({ loadingData: true, loadedMore: false })
    const data = await request('/course/teacher/getTeacherList')
    this.setState({ loadingData: false })
    if(data) {
      this.setState({
        teacherData: data.teacherList.map(item => ({ ...item, key: item.id }))
      })
    }
  }

  handleShowAddTeacherModel = () => {
    this.setState({ addTeacherShow: true })
  }

  handleCloseAddTeacherModel = () => {
    this.setState({ addTeacherShow: false })
  }

  handleResetPassword = async (id) => {
    const data = await request(`/course/teacher/resetPassword/${id}`, { method: 'PUT' })
    if(data) {
      message.success('重置密码成功')
    }
  }

  handleDelete = async (id) => {
    const data = await request(`/course/teacher/deleteTeacher/${id}`, { method: 'POST' })
    if(data) {
      message.success('删除成功')
      this.loadData()
    }
  }

  render() {
    const { teacherData, addTeacherShow, loadingData } = this.state
    const teacherColumns = [
      { title: '职工号', dataIndex: 'teacher_num', align: 'center' },
      { title: '教师', dataIndex: 'name', align: 'center' },
      { title: '所属学院', dataIndex: 'college', align: 'center' },
      { title: '操作',
        dataIndex: 'operation',
        align: 'center',
        render: (text, record) => {
          const { id } = record
          return (
            <span>
              <Popconfirm title="确定要重置该教师的密码吗?" onConfirm={() => this.handleResetPassword(id)}>
                <a href="javascript:;">重置密码</a>
              </Popconfirm>
              <Divider type="vertical" />
              <Popconfirm title="确定要删除该教师吗?" onConfirm={() => this.handleDelete(id)}>
                <a href="javascript:;">删除</a>
              </Popconfirm>
            </span>
          )
        }
      }
    ]

    return (
      <div className="manage">
        <div className="manageWrap">
          <Tabs defaultActiveKey="1" size="large" onChange={this.handleChangeTab}>
            <TabPane tab={<span><Icon component={TeacherSvg} />教师信息</span>} key="1">
              <div className="manage__opera">
                <Button icon="plus" type="primary" onClick={this.handleShowAddTeacherModel}>添加教师</Button>
                <Search
                  placeholder="输入教师名字"
                  onSearch={value => console.log(value)}
                  style={{ width: 200, marginLeft: 10 }}
                  enterButton
                />
                <Button type="primary" icon="redo" style={{ marginLeft: 4, padding: "0 15px" }} onClick={this.loadData}/>
              </div>
              <Spin spinning={loadingData}>
                <Table
                  columns={teacherColumns}
                  dataSource={teacherData}
                />
              </Spin>
            </TabPane>
            {/* <TabPane tab={<span><Icon type="solution" />课程信息</span>} key="2"> */}
              {/* <Table columns={studentColumns} dataSource={stuData} /> */}
            {/* </TabPane> */}
          </Tabs>
          <AddTeacherModel visible={addTeacherShow} onClose={this.handleCloseAddTeacherModel} onReload={this.loadData}/>
        </div>
      </div>
    )
  }
}
