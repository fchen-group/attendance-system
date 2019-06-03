import React, { Component } from 'react'
import LoginForm from './components/LoginForm'
import './Index.scss'

export default class Login extends Component {

  componentDidMount() {
  }

  render() {
    return (
      <div className="login">
        <div className="login__form">
          <h1>优签到 ✔</h1>
          <LoginForm />
        </div>
        <div className="login__footer">
          粤ICP备18077856号
        </div>
      </div>
    )
  }
}
