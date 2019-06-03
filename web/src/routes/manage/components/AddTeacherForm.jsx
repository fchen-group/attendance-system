import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Form, Input, Checkbox } from 'antd'

const FormItem = Form.Item

class AddTeacherForm extends Component {
  static propTypes = {
    onRef: PropTypes.func
  }

  state = {
    confirmDirty: false,
    checkPwd: true,
  }

  componentWillMount() {
    if(this.props.onRef) {
      this.props.onRef(this)
    }
  }

  handleConfirmBlur = (e) => {
    const value = e.target.value;
    this.setState({ confirmDirty: this.state.confirmDirty || !!value })
  }

  handleChangeCheck = (e) => {
    this.setState({
      checkPwd: e.target.checked,
    }, () => {
      this.props.form.setFieldsValue({
        password: undefined,
        confirmPassword: undefined
      })
      this.props.form.validateFields(['password', 'confirmPassword'], { force: true })
    })
  }

  validateToNextPassword = (rule, value, callback) => {
    const form = this.props.form;
    if (value && this.state.confirmDirty) {
      form.validateFields(['confirmPassword'], { force: true })
    }
    callback();
  }

  compareToFirstPassword = (rule, value, callback) => {
    const form = this.props.form;
    if (value && value !== form.getFieldValue('password')) {
      callback('两次输入的密码不相同！')
    } else {
      callback();
    }
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
        <FormItem label="教师姓名" {...formItemLayout}>
          {getFieldDecorator('name', {
            rules: [{ required: true, message: '请输入教师姓名' }]
          })(
            <Input />
          )}
        </FormItem>
        <FormItem label="所属学院" {...formItemLayout}>
          {getFieldDecorator('college', {
            rules: [{ required: true, message: '请输入教师所属学院' }]
          })(
            <Input />
          )}
        </FormItem>
        <FormItem label="职工号" {...formItemLayout}>
          {getFieldDecorator('teacherNum', {
            rules: [{ required: true, message: '请输入职工号' }]
          })(
            <Input placeholder="用于登录系统" />
          )}
        </FormItem>
        <FormItem label="登录密码" {...formItemLayout}>
          {getFieldDecorator('password', {
            rules: [
              { required: !this.state.checkPwd, message: '请输入密码' },
              { validator: this.validateToNextPassword }]
          })(
            <Input type="password" placeholder="不填则默认与职工号相同" disabled={this.state.checkPwd} />
          )}
        </FormItem>
        <FormItem label="确认密码" {...formItemLayout}>
          {getFieldDecorator('confirmPassword', {
            rules: [
              { required: !this.state.checkPwd, message: '请再次输入密码' },
              { validator: this.compareToFirstPassword }]
          })(
            <Input type="password" onBlur={this.handleConfirmBlur} placeholder="不填则默认与职工号相同" disabled={this.state.checkPwd}/>
          )}
        </FormItem>
        <div style={{ textAlign: 'right' }}>
          <Checkbox
          checked={this.state.checkPwd}
          onChange={this.handleChangeCheck}
          >
            使用默认密码 ( 默认与职工号相同 )
          </Checkbox>
        </div>
      </Form>
    )
  }
}

export default Form.create()(AddTeacherForm)

