import React, { Component } from 'react'
import { Form, Icon, Input, Button, Checkbox, message } from 'antd'
import request from '../../../helper/request'
import './LoginForm.scss'

const FormItem = Form.Item;

class NormalLoginForm extends Component {
  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        console.log('Received values of form: ', values);
        this.login(values)
      }
    });
  }

  login = async (values) => {
    const data = await request('/login', {
      method: 'POST',
      body: JSON.stringify(values),
    })
    if(data) {
      localStorage.setItem("username",values.username)
      window.location.href = '/'
    } else {
      message.error('用户名或者密码错误')
    }
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form onSubmit={this.handleSubmit} className="loginForm">
        <FormItem>
          {getFieldDecorator('username', {
            rules: [{ required: true, message: '请输入您的用户名!' }],
          })(
            <Input prefix={<Icon type="user" style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder="Username" />
          )}
        </FormItem>
        <FormItem>
          {getFieldDecorator('password', {
            rules: [{ required: true, message: '请输入您的密码!' }],
          })(
            <Input prefix={<Icon type="lock" style={{ color: 'rgba(0,0,0,.25)' }} />} type="password" placeholder="Password" />
          )}
        </FormItem>
        <FormItem>
          {getFieldDecorator('remember', {
            valuePropName: 'checked',
            initialValue: true,
          })(
            <Checkbox>Remember me</Checkbox>
          )}
          <a className="loginForm__forgot" href="/">Forgot password</a>
          <Button type="primary" htmlType="submit" className="loginForm__button">
            登录
          </Button>
          Or <a href="/">register now!</a>
        </FormItem>
      </Form>
    );
  }
}

const LoginForm = Form.create()(NormalLoginForm)

export default LoginForm
