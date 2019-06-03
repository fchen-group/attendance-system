import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Form, Select, Input, Row, Col, Icon } from 'antd'
import './CourseForm.scss'

const FormItem = Form.Item
const { Option } = Select

class CourseForm extends Component {
  static propTypes = {
    onRef: PropTypes.func.isRequired,
    data: PropTypes.object,
  }

  uuid = 0

  addInput = () => {
    const { form } = this.props
    const keys = form.getFieldValue('keys')
    const nextKeys = keys.concat(++this.uuid)
    form.setFieldsValue({
      keys: nextKeys
    })
    console.log(keys)
  }

  removeInput = (k) => {
    const { form } = this.props
    const keys = form.getFieldValue('keys')
    if(keys.length === 1) {
      return
    }
    form.setFieldsValue({
      keys: keys.filter(key => key !== k)
    })
  }

  componentDidMount() {
    const { data } = this.props
    this.props.onRef(this)
    this.uuid = data ? data.time.length - 1 : 0
  }

  render() {
    const { data, form } = this.props
    const { getFieldDecorator, getFieldValue } = form
    const times = data ? data.time : [0]
    const week = ['日', '一', '二', '三', '四', '五', '六']
    const session = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
    const weekGroup = week.map((v, i) => <Option value={i} key={v}>{v}</Option>)
    const sessionGroup = session.map(v => <Option value={v} key={v}>{v}</Option>)
    getFieldDecorator('keys', {initialValue: Array.from({length: data ? data.time.length : 1}, (v, i) => i)})
    const keys = getFieldValue('keys')
    const addrAndTime = keys.map((k, i) => {
      return (
        <Row gutter={16} key={k}>
          <Col span={12}>
            <FormItem label={i === 0 ? "上课地点" : ""}>
              {getFieldDecorator(`time[${k}].loc`, {
                initialValue: data ? times[k] ? times[k].loc : '' : '',
                rules: [{ required: true, message: '请输入上课地点' }],
              })(<Input placeholder="请输入上课地点" />)}
            </FormItem>
          </Col>
          <Col span={12}>
            <FormItem label={i === 0 ? "上课时间" : ""}>
              <Row align="middle" type="flex">
                <Col span={3}><span className="courseForm__text">星期</span></Col>
                <Col span={5}>
                  <FormItem className="courseForm__innerFormItem">
                    {getFieldDecorator(`time[${k}].weekday`, {
                      initialValue: data ? times[k] ? times[k].weekday : '' : 1,
                      rules: [{ required: true, message: '请选择星期' }],
                    })(<Select>{weekGroup}</Select>)}
                  </FormItem>
                </Col>
                <Col span={1}></Col>
                <Col span={5}>
                  <FormItem className="courseForm__innerFormItem">
                    {getFieldDecorator(`time[${k}].start`, {
                      initialValue: data ? times[k] ? times[k].start : '' : 1,
                      rules: [{ required: true, message: '请选择开始节数' }],
                    })(<Select>{sessionGroup}</Select>)}
                  </FormItem>
                </Col>
                <Col span={1}><span className="courseForm__text">-</span></Col>
                <Col span={5}>
                  <FormItem className="courseForm__innerFormItem">
                    {getFieldDecorator(`time[${k}].end`, {
                      initialValue: data ? times[k] ? times[k].end : '' : 2,
                      rules: [{ required: true, message: '请选择结束节数' }],
                    })(<Select>{sessionGroup}</Select>)}
                  </FormItem>
                </Col>
                <Col span={2}><span className="courseForm__text">节</span></Col>
                {i === 0 && (
                  <Col span={2}>
                    <span className="courseForm__text">
                      <Icon type="plus-circle" style={{ fontSize: 18, cursor: 'pointer' }} onClick={this.addInput} />
                    </span>
                  </Col>
                )}
                {i > 0 && (
                  <Col span={2}>
                    <span className="courseForm__text">
                      <Icon type="minus-circle-o" style={{ fontSize: 18, cursor: 'pointer' }} disabled={keys.length === 1} onClick={() => this.removeInput(k)} />
                    </span>
                  </Col>
                )}
              </Row>
            </FormItem>
          </Col>
        </Row>
      )
    })

    return (
      <Form layout="vertical" className="courseForm">
        <Row gutter={16}>
          <Col span={12}>
            <FormItem label="课程名">
              {getFieldDecorator('courseName', {
                initialValue: data ? data.courseName : '',
                rules: [{ required: true, message: '请输入课程名' }],
              })(<Input placeholder="请输入课程名" />)}
            </FormItem>
          </Col>
          <Col span={12}>
            <FormItem label="课程号">
              {getFieldDecorator('courseNum', {
                initialValue: data ? data.courseNum : '',
                rules: [{ required: true, message: '请输入课程号' }, { pattern: /^\d+$/, message: '课程号需为数字'}],
              })(<Input placeholder="请输入课程号" />)}
            </FormItem>
          </Col>
        </Row>
        {addrAndTime}
      </Form>
    )
  }
}

export default Form.create()(CourseForm);
