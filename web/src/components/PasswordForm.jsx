import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Form, Input } from 'antd'

const FormItem = Form.Item

class PasswordForm extends Component {
  static propTypes = {
    onRef: PropTypes.func
  }

  componentWillMount() {
    if(this.props.onRef) {
      this.props.onRef(this)
    }
  }

  state = {
    confirmDirty: false
  }

  handleConfirmBlur = (e) => {
    const value = e.target.value;
    this.setState({ confirmDirty: this.state.confirmDirty || !!value });
  }

  validateToNextPassword = (rule, value, callback) => {
    const form = this.props.form;
    if (value && this.state.confirmDirty) {
      form.validateFields(['confirmPassword'], { force: true });
    }
    callback();
  }

  compareToFirstPassword = (rule, value, callback) => {
    const form = this.props.form;
    if (value && value !== form.getFieldValue('newPassword')) {
      callback('两次输入的密码不相同！');
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
        <FormItem label="旧密码" {...formItemLayout}>
          {getFieldDecorator('oldPassword', {
            rules: [{ required: true, message: '请输入旧密码' }]
          })(
            <Input type="password" />
          )}
        </FormItem>
        <FormItem label="新密码" {...formItemLayout}>
          {getFieldDecorator('newPassword', {
            rules: [{
              required: true, message: '请输入新密码'
            }, {
              validator: this.validateToNextPassword
            }]
          })(
            <Input type="password" />
          )}
        </FormItem>
        <FormItem label="确认新密码" {...formItemLayout}>
          {getFieldDecorator('confirmPassword', {
            rules: [{
              required: true, message: '请再次输入新密码'
            }, {
              validator: this.compareToFirstPassword
            }]
          })(
            <Input type="password" onBlur={this.handleConfirmBlur} />
          )}
        </FormItem>
      </Form>
    )
  }
}

export default Form.create()(PasswordForm)
