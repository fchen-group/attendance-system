import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'
import { Menu, Icon, Dropdown } from 'antd'
import PasswordModal from './PasswordModal'
import request from '../helper/request'
import './MyHeader.scss'

export default class MyHeader extends Component {
  static propTypes = {
    match: PropTypes.object.isRequired
  }

  state = {
    username: '',
    pwdModalShow: false,
    path: '/'
  }

  componentWillMount() {
    this.changeTab(this.props.match.url)
    this.setState({
      username: localStorage.getItem('username')
    })
  }

  componentWillReceiveProps(nextProps) {
    this.changeTab(nextProps.match.url)
  }

  changeTab = (url) => {
    if(/(^\/detail$)|(^\/detail\/)/.test(url)) {
      this.setState({ path: '/detail' })
    } else if(/(^\/manage$)|(^\/manage\/)/.test(url)) {
      this.setState({ path: '/manage' })
    } else if(url === '/') {
      this.setState({ path: '/' })
    }
  }

  handleUserClick = (e) => {
    if(e.key === '1') {
      this.setState({ pwdModalShow: true })
    } else if(e.key === '2') {
      this.logout()
    }
  }

  handleClosePwdModal = () => {
    this.setState({ pwdModalShow: false })
  }

  logout = async () => {
    const data = await request('/logout')
    if(data) {
      localStorage.removeItem('username')
      window.location.href = '/'
    }
  }

  render() {
    const userMenu = (
      <Menu onClick={this.handleUserClick}>
        <Menu.Item key="1">修改密码</Menu.Item>
        <Menu.Divider />
        <Menu.Item key="2">退出</Menu.Item>
      </Menu>
    )
    return (
      <div className="header">
        <h1 className="header__logo">✔ 优签到后台管理系统</h1>
        <Menu className="header__menu"
          theme="dark"
          mode="horizontal"
          selectedKeys={[this.state.path]}
        >
          <Menu.Item key="/">
            <Link to="/"><Icon type="form" style={{ fontSize: 20 }} />我的课程</Link>
          </Menu.Item>
          <Menu.Item key="/detail">
            <Link to="/detail"><Icon type="bar-chart" style={{ fontSize: 20 }} />签到详情</Link>
          </Menu.Item>
          <Menu.Item key="/manage">
            <Link to="/manage"><Icon type="cluster" style={{ fontSize: 20 }} />系统管理</Link>
          </Menu.Item>
        </Menu>
        <div className="header__user">
          <Dropdown overlay={userMenu} placement="bottomRight">
            <span className="header__user--text">
              <Icon type="user" style={{ fontSize: 28 }} /> {this.state.username} <Icon type="down" style={{ fontSize: 14}}/>
            </span>
          </Dropdown>
        </div>
        <PasswordModal visible={this.state.pwdModalShow} onClose={this.handleClosePwdModal} />
      </div>
    )
  }
}
