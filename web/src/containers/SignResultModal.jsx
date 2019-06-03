import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Modal } from 'antd'
import SignResultTable from '../components/SignResultTable'
import request from '../helper/request'

export default class ScanResultModal extends Component {
  static propTypes = {
    visible: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
    signId: PropTypes.number.isRequired,
  }

  state = {
    list: []
  }

  componentWillReceiveProps(nextProps) {
    if(nextProps.visible && nextProps.visible !== this.props.visible) {
      this.loadData(nextProps.signId)
    }
  }

  loadData = async (signId = this.props.signId) => {
    const data = await request(`/course/${signId}/signRecord`)
    if(data) {
      const list = data.list.map(item => ({
        key: item.id,
        studentNum: item.student_num,
        name: item.name,
        classInfo: item.class_info,
        hasSigned: item.state,
        signTime: this.getNullTime(item.sign_time)
      }))
      this.setState({ list })
    }
  }

  getNullTime = (time) => {
    if(time === '0001-01-01T01:01:00' || time === '0001-01-01 01:01:00') {
      return '-'
    }
    return time
  }

  handleChangeSign = async (checked, key) => {
    const data = await request(`/course/signRecord/${key}/update`, {
      method: 'PUT',
      body: JSON.stringify({ state: checked ? 3 : 2 })
    })
    if(data) {
      // 同时将数据也更新，避免换页导致视图不显示更改后的开关状态
      const newList = [...this.state.list]
      for(let i = 0; i < newList.length; i ++) {
        if(newList[i].key === key) {
          newList[i].hasSigned = checked ? 3 : 2
          break;
        }
      }
      this.setState({
        list: newList
      })
    }
  }

  handleReload = () => {
    this.loadData()
  }

  render() {
    const { visible, onClose } = this.props
    return (
      <Modal
        width={720}
        title="签到结果"
        visible={visible}
        onOk={onClose}
        onCancel={onClose}
      >
        <SignResultTable data={this.state.list} onChangeSign={this.handleChangeSign} onReload={this.handleReload} />
      </Modal>
    )
  }
}
