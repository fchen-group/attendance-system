import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Table, Input, Button, Icon, Switch } from 'antd'
import './SignResultTable.scss'

export default class SignResultTable extends Component {
  static propTypes = {
    data: PropTypes.array.isRequired,
    onChangeSign: PropTypes.func.isRequired,
    onReload: PropTypes.func.isRequired
  }

  state = {
    filteredInfo: null,
    signState: 'all',
    earchStuNum: '',
    searchStuName: '',
    searchStuClass: ''
  }

  handleSearch = (selectedKeys, confirm, type) => () => {
    confirm()
    switch (type) {
      case 1:
        this.setState({ searchStuNum: selectedKeys[0] })
        break
      case 2:
        this.setState({ searchStuName: selectedKeys[0] })
        break
      case 3:
        this.setState({ searchStuClass: selectedKeys[0] })
        break
      default:
        break
    }
  }

  handleReset = (clearFilters, type) => () => {
    clearFilters()
    switch (type) {
      case 1:
        this.setState({ searchStuNum: '' })
        break
      case 2:
        this.setState({ searchStuName: '' })
        break
      case 3:
        this.setState({ searchStuClass: '' })
        break
      default:
        break
    }
  }

  setSignFilter = (type) => {
    switch(type) {
      case 'all':
        this.setState({ filteredInfo: null, signState: type })
        break
      case 'success':
        this.setState({
          signState: type,
          filteredInfo: {
            state: [3]
          }
        })
        break
      case 'fail':
        this.setState({
          signState: type,
          filteredInfo: {
            state: [0, 1, 2]
          }
        })
        break
      default:
        break
    }
  }

  render() {
    let { filteredInfo, signState } = this.state
    filteredInfo = filteredInfo || {}
    const columns = [
    {
      title: '学号',
      dataIndex: 'studentNum',
      width: '20%',
      editable: true,
      sorter: (a, b) => a.studentNum - b.studentNum,
      filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
        <div className="custom-filter-dropdown">
          <Input
            ref={ele => this.searchNumInput = ele}
            placeholder="搜索学号"
            value={selectedKeys[0]}
            onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
            onPressEnter={this.handleSearch(selectedKeys, confirm, 1)}
          />
          <Button type="primary" onClick={this.handleSearch(selectedKeys, confirm, 1)}>搜索</Button>
          <Button onClick={this.handleReset(clearFilters, 1)}>重置</Button>
        </div>
      ),
      filterIcon: filtered => <Icon type="search" style={{ color: filtered ? '#108ee9' : '#aaa' }} />,
      onFilter: (value, record) => record.studentNum.includes(value),
      onFilterDropdownVisibleChange: (visible) => {
        if(visible) {
          setTimeout(() => {
            this.searchNumInput.focus()
          })
        }
      },
      render: (text) => {
        const { searchStuNum } = this.state
        return searchStuNum ? (
          <span>
            {text.split(new RegExp(`(?<=${searchStuNum})|(?=${searchStuNum})`, 'i')).map((fragment, i) => (
              fragment.toLowerCase() === searchStuNum.toLowerCase()
                ? <span key={i} style={{ color: '#f50' }}>{fragment}</span> : fragment
            ))}
        </span>
        ) : text
      }
    }, {
      title: '姓名',
      dataIndex: 'name',
      editable: true,
      width: '18%',
      filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
        <div className="custom-filter-dropdown">
          <Input
            ref={ele => this.searchNameInput = ele}
            placeholder="搜索姓名"
            value={selectedKeys[0]}
            onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
            onPressEnter={this.handleSearch(selectedKeys, confirm, 2)}
          />
          <Button type="primary" onClick={this.handleSearch(selectedKeys, confirm, 2)}>搜索</Button>
          <Button onClick={this.handleReset(clearFilters, 2)}>重置</Button>
        </div>
      ),
      filterIcon: filtered => <Icon type="search" style={{ color: filtered ? '#108ee9' : '#aaa' }} />,
      onFilter: (value, record) => record.name.includes(value),
      onFilterDropdownVisibleChange: (visible) => {
        if(visible) {
          setTimeout(() => {
            this.searchNameInput.focus()
          })
        }
      },
      render: (text) => {
        const { searchStuName } = this.state
        return searchStuName ? (
          <span>
            {text.split(new RegExp(`(?<=${searchStuName})|(?=${searchStuName})`, 'i')).map((fragment, i) => (
              fragment.toLowerCase() === searchStuName.toLowerCase()
                ? <span key={i} style={{ color: '#f50' }}>{fragment}</span> : fragment
            ))}
          </span>
        ) : text
      }
    }, {
      title: '班级',
      dataIndex: 'classInfo',
      editable: true,
      width: '22%',
      filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
        <div className="custom-filter-dropdown">
          <Input
            ref={ele => this.searchClassInput = ele}
            placeholder="搜索班级"
            value={selectedKeys[0]}
            onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
            onPressEnter={this.handleSearch(selectedKeys, confirm, 3)}
          />
          <Button type="primary" onClick={this.handleSearch(selectedKeys, confirm, 3)}>搜索</Button>
          <Button onClick={this.handleReset(clearFilters, 3)}>重置</Button>
        </div>
      ),
      filterIcon: filtered => <Icon type="search" style={{ color: filtered ? '#108ee9' : '#aaa' }}/>,
      onFilter: (value, record) => record.classInfo.includes(value),
      onFilterDropdownVisibleChange: (visible) => {
        if(visible) {
          setTimeout(() => {
            this.searchClassInput.focus()
          })
        }
      },
      render: text => {
        const { searchStuClass } = this.state
        return searchStuClass ? (
          <span>
            {text.split(new RegExp(`(?<=${searchStuClass})|(?=${searchStuClass})`, 'i')).map((fragment, i) => (
              fragment.toLowerCase() === searchStuClass.toLowerCase()
                ? <span key={i} style={{ color: '#f50' }}>{fragment}</span> : fragment
            ))}
          </span>
        ) : text
      }
    }, {
      title: '签到',
      dataIndex: 'hasSigned',
      filteredValue: filteredInfo.state || null,
      onFilter: (value, record) => value === record.hasSigned,
      render: (text, record) => (
        <Switch
          onChange={(checked) => this.props.onChangeSign(checked, record.key)}
          checkedChildren={<Icon type="check" />}
          unCheckedChildren={<Icon type="close" />}
          defaultChecked={text === 3}
        />
      )
    }, {
      title: '签到时间',
      dataIndex: 'signTime',
      align: 'center',
      sorter: (a, b) => {
        if(a.hasSigned > b.hasSigned) {
          return 1
        } else if(a.hasSigned < b.hasSigned) {
          return -1
        } else {
          return 0
        }
      }
    }]
    return (
      <div className="signResultTable">
        <div className="signResultTable__btnGroup">
          <Button onClick={() => this.setSignFilter('all')}>全部</Button>
          <Button onClick={() => this.setSignFilter('success')}>成功</Button>
          <Button onClick={() => this.setSignFilter('fail')} type={signState==='fail'?'danger':'default'}>失败</Button>
          <Button type='primary' onClick={this.props.onReload}><Icon type="reload" /></Button>
        </div>
        <Table
          // pagination={false}
          // scroll={{ y: 460 }}
          dataSource={this.props.data}
          columns={columns}
        />
      </div>
    )
  }
}
