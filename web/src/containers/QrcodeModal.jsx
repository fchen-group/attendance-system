import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Modal, Form, Select } from 'antd'
import QrcodeModal from '../components/QrodeModal'
import SignResultModal from './SignResultModal'
import { weekMap } from '../helper/util'
import { withRouter } from 'react-router-dom'
import request from '../helper/request'

const FormItem = Form.Item
const { Option } = Select

class QrcodeOptionsForm extends Component {
  static propTypes = {
    onRef: PropTypes.func.isRequired,
    courseTime: PropTypes.array.isRequired
  }
  componentWillMount() {
    this.props.onRef(this)
  }
  render() {
    const { getFieldDecorator } = this.props.form
    const formItemLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 19 },
      }
    }
    return (
      <Form>
        <FormItem label="当前周" {...formItemLayout}>
          {getFieldDecorator('week', {
            rules: [{ required: true, message: '请选择当前周数' }]
          })(
            <Select>
              {Array.from({ length: 20 }).map((v, i) => <Option value={i + 1} key={i}>第{i + 1}周</Option>)}
            </Select>
          )}
        </FormItem>
        <FormItem label="课程时间" {...formItemLayout}>
          {getFieldDecorator('courseTimeId', {
            rules: [{ required: true, message: '请选择课程时间' }]
          })(
            <Select>
              {this.props.courseTime.map(item => (
                <Option
                  key={item.courseTimeId}
                  value={item.courseTimeId}>
                  星期{weekMap[item.weekday]} 第{item.start}节~第{item.end}节 &nbsp;&nbsp;&nbsp;{item.loc}
                </Option>)
              )}
            </Select>
          )}
        </FormItem>
      </Form>
    )
  }
}

QrcodeOptionsForm = Form.create()(QrcodeOptionsForm)

class QrcodeModalContainer extends Component {
  static propTypes = {
    courseData: PropTypes.object.isRequired,
    visible: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
  }

  state = {
    gettingQrcode: false,
    qrcodeShow: false,
    resultShow: false,
    httpLink: '',
    signId: 0
  }

  wsFlag = false
  ws = null

  handleRef = (ele) => this.child = ele

  handleGetQrcode = () => {
    this.child.props.form.validateFields((err, values) => {
      if (err) {
        return
      }
      this.time = values.courseTimeId
      this.week = values.week
      this.getQrcode(values)
    })
  }

  getQrcode = async (values) => {
    const data = await request(`/course/${this.props.courseData.courseId}/sign`, {
      method: 'POST',
      body: JSON.stringify(values)
    })
    if (data) {
      this.setState({
        gettingQrcode: false,
        qrcodeShow: true,
        signId: data.signID
      })
      this.webSocketConnect(data.websocketLink)
    }
  }

  webSocketConnect = (link) => {
    this.ws = new WebSocket(link)
    const obj = {
      time: this.time,
      week: this.week,
      signId: this.state.signId,
      courseId: this.props.courseData.courseId,
      start: 1
    }
    this.ws.onopen = () => {
      console.log('连接建立...')
      this.ws.send(JSON.stringify(obj))
      this.wsFlag = true
    }
    this.ws.onmessage = (evt) => {
      const { data } = evt;
      const obj = JSON.parse(data)
      this.ws.send("ACK");
      this.setState({
        httpLink: obj.httpLink
      })
    }
    this.ws.error = (err) => {
      console.log('ws错误')
      console.log(err);
    };
    this.ws.onclose = () => {
      console.log("连接已关闭...");
      if (this.wsFlag) {
        console.log("重新建立连接...");
        this.ws = new WebSocket(link);
      }
    }
  }

  handleCloseQrcode = async () => {
    const data = await request(`/course/${this.props.courseData.courseId}/sign/${this.state.signId}/signState`, {
      method: 'POST',
      body: JSON.stringify({ state: 0 }) // 结束状态
    })
    if(data) {
      console.log(data)
    }
    this.setState({
      qrcodeShow: false,
      resultShow: true
    })
    this.props.onClose()
    this.wsFlag = false
    this.ws.close()
  }

  loadResult = async () => {
    const data = await request(`/course/${this.state.signId}/signRecord`)
    if(data) {
      const list = data.list.map(item => ({
        key: item.id,
        studentNum: item.student_num,
        name: item.name,
        classInfo: item.class_info,
        hasSigned: item.state,
        signTime: item.sign_time
      }))
      this.setState({
        stuResultList: list
      })
    }
  }

  handleCloseResult = () => {
    this.setState({ resultShow: false })
    if(this.props.history.location.pathname.includes('detail')) {
      window.location.reload()
    } else {
      this.props.history.push(`/detail/${this.props.courseData.courseId}`)
    }
  }

  render() {
    const { visible, courseData, onClose } = this.props
    const { gettingQrcode, qrcodeShow, resultShow, httpLink } = this.state
    return (
      <div>
        <Modal
          title="发起签到"
          visible={visible}
          okText="生成二维码"
          onOk={this.handleGetQrcode}
          onCancel={onClose}
          confirmLoading={gettingQrcode}
        >
          <QrcodeOptionsForm courseTime={courseData.time} onRef={this.handleRef} />
        </Modal>
        <QrcodeModal visible={qrcodeShow} url={httpLink} onClose={this.handleCloseQrcode} />
        <SignResultModal  signId={this.state.signId} visible={resultShow} onClose={this.handleCloseResult} />
      </div>
    )
  }
}

export default withRouter(QrcodeModalContainer)
