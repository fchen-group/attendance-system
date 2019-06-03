import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Tabs, Icon, Table, message, Button, Spin, Divider, Popconfirm } from 'antd'
import SignResultModal from '../../../containers/SignResultModal'
import request from '../../../helper/request'
import './DetailTable.scss'

const { TabPane } = Tabs

export default class DetailTable extends Component {
  static propTypes = {
    courseId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired
  }

  state = {
    signData: [],
    stuData: [],

    loadingSignData: false,
    loadingStuData: false,

    signId: -1,
    signDetailShow: false,
  }

  componentWillMount() {
    const { courseId } = this.props
    if(courseId !== '') {
      this.loadSignData(courseId)
      this.loadStuData(courseId)
    }
  }

  componentWillReceiveProps(nextProps) {
    const { courseId } = nextProps
    if(courseId !== '' && courseId !== this.props.courseId) {
      this.loadSignData(courseId)
      this.loadStuData(courseId)
    }
  }

  loadSignData = async (courseId = this.props.courseId) => {
    this.setState({ loadingSignData: true })
    const data = await request(`/course/${courseId}/sign`)
    this.setState({ loadingSignData: false })
    if(data) {
      this.setState({
        signData: data.list.map(item => ({ ...item, key: item.signId }))
      })
    }
  }

  loadStuData = async (courseId) => {
    this.setState({ loadingStuData: true })
    const data = await request(`/course/studentDetail/${courseId}`)
    this.setState({ loadingStuData: false })
    if(data) {
      this.setState({
        stuData: data.studentDetailBOList.map(item => ({ ...item, key: item.id }))
      })
    }
  }

  showSignDetail = (signId) => {
    this.setState({
      signId,
      signDetailShow: true
    })
  }

  handleCloseDetail = () => {
    this.setState({ signDetailShow: false })
    this.loadSignData(this.props.courseId)
    this.loadStuData(this.props.courseId)
  }

  handleEndSign = async (signId) => {
    const data = await request(`/course/${this.props.courseId}/sign/${signId}/signState`, {
      method: 'POST',
      body: JSON.stringify({ state: 0 })
    })
    if(data) {
      message.success('结束签到成功')
      this.loadSignData()
    }
  }

  handleExport = () => {
    window.open(`https://api.inforsecszu.net/v1/course/${this.props.courseId}/sign/download`);
  }

  deleteRecord = async (signId) => {
    const data = await request(`/course/deleteSign/${signId}`, { method: 'DELETE' })
    if(data) {
      message.success('删除成功')
      this.loadSignData()
    }
  }

  render() {
    const signColumns = [
    {
      title: '周数',
      dataIndex: 'week',
      align: 'center'
    }, {
      title: '课程时间',
      dataIndex: 'courseTime',
      align: 'center'
    }, {
      title: '出勤率',
      dataIndex: 'attendanceRate',
      align: 'center'
    }, {
      title: '已签到人数',
      dataIndex: 'signedAmount',
      align: 'center'
    }, {
      title: '未签到人数',
      dataIndex: 'notSignAmount',
      align: 'center'
    }, {
      title: '发起时间',
      dataIndex: 'createtime',
      align: 'center'
      // defaultSortOrder: 'descend',
      // sorter: (a, b) => a.createtime > b.createtime
    }, {
      title: '结束时间',
      dataIndex: 'endtime',
      align: 'center',
      render: (text, record) => {
        const { state } = record
        return (
          <span>
            {state !== 0
              ? '默认2小时后结束'
              : text
            }
          </span>
        )
      }
    }, {
      title: '操作',
      dataIndex: 'operation',
      align: 'center',
      render: (text, record) => {
        const { signId, state } = record
        return (
          <span>
            {state !== 0 && <a href="javascript:;" className="operation__link operation__link--end" onClick={() => this.handleEndSign(signId)}>立即结束</a>}
            <a href="javascript:;" className="operation__link" onClick={() => this.showSignDetail(signId)}>查看详情</a>
            <Divider type="vertical" />
            <Popconfirm title="确定要删除该记录?" onConfirm={() => this.deleteRecord(signId)}>
              <span className="operation__link--delete">删除</span>
            </Popconfirm>
          </span>
        )
      }
    }]

    const studentColumns = [{
      title: '学号',
      dataIndex: 'studentNum',
      align: 'center',
    }, {
      title: '姓名',
      dataIndex: 'name',
      align: 'center',
    }, {
      title: '班级',
      dataIndex: 'classInfo',
      align: 'center'
    }, {
      title: '签到率',
      dataIndex: 'signRate',
      align: 'center'
    }, {
      title: '已签到次数',
      dataIndex: 'signedNum',
      align: 'center'
    }, {
      title: '未签到次数',
      dataIndex: 'notSignedNum',
      align: 'center'
    }]

    const { signData, stuData, signDetailShow, signId, loadingSignData, loadingStuData } = this.state
    return (
      <div className="detailTable">

        <Tabs defaultActiveKey="1" size="large" onChange={this.handleChangeTab}>
          <TabPane tab={<span><Icon type="schedule" />签到历史</span>} key="1">
            <Spin spinning={loadingSignData}>
              <Table columns={signColumns} dataSource={signData} />
            </Spin>
          </TabPane>
          <TabPane tab={<span><Icon type="solution" />学生详情</span>} key="2">
            <Spin spinning={loadingStuData}>
              <Table columns={studentColumns} dataSource={stuData} />
            </Spin>
          </TabPane>
        </Tabs>
        <Button type="primary" icon="export" className="exportBtn" onClick={this.handleExport}>导出签到记录</Button>
        <SignResultModal signId={signId} visible={signDetailShow} onClose={this.handleCloseDetail} />
      </div>
    )
  }
}
