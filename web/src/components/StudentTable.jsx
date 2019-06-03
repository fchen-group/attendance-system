import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Table, Input, Button, Popconfirm, Form, Upload, Icon, Spin, Tooltip, message, Divider, Menu, Dropdown } from 'antd'
import './StudentTable.scss'
import excelToJson from '../helper/excelToJson'

const FormItem = Form.Item;
const EditableContext = React.createContext();

const EditableRow = ({ form, index, ...props }) => (
  <EditableContext.Provider value={form}>
    <tr {...props} />
  </EditableContext.Provider>
);

const EditableFormRow = Form.create()(EditableRow);

class EditableCell extends Component {
  render() {
    const {
      editing,
      dataIndex,
      title,
      record,
      index,
      ...restProps
    } = this.props;
    return (
      <EditableContext.Consumer>
        {form => {
          const { getFieldDecorator } = form
          return (
            <td {...restProps}>
              {editing ? (
                <FormItem style={{ margin: 0, padding: 0 }}>
                  {getFieldDecorator(dataIndex, {
                    rules: [{
                      required: true,
                      message: `请输入${title}`
                    }],
                    initialValue: record[dataIndex]
                  })(<Input />)}
                </FormItem>
              ) : restProps.children}
            </td>
          )
        }}
      </EditableContext.Consumer>
    )
  }
}

class EditableTable extends Component {
  static propTypes = {
    dataSource: PropTypes.array.isRequired,
    setStudents: PropTypes.func.isRequired,
    onAdd: PropTypes.func,
    onDelete: PropTypes.func,
    onSave: PropTypes.func,
    onImport: PropTypes.func,
    onResetPassword: PropTypes.func,
    onUntyingWx: PropTypes.func,
  }

  state = {
    editingKey: '',
    importingStu: false,
    selectedRowKeys: [],
    selectedRowStuIds: [],
    searchStuNum: '',
    searchStuName: '',
    searchStuClass: ''
  }

  columns = [
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
              ? <span key={i} className="highlight">{fragment}</span> : fragment
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
              ? <span key={i} className="highlight">{fragment}</span> : fragment
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
              ? <span key={i} className="highlight">{fragment}</span> : fragment
          ))}
        </span>
      ) : text
    }
  }, {
    title: '操作',
    dataIndex: 'operation',
    render: (text, record) => {
      const { key, id } = record
      const editable = this.isEditing(key)
      return (
        <div>
          {editable ? (
            <span>
              <EditableContext.Consumer>
                {form => (
                  <Button type="primary" size="small" onClick={() => this.handleSave(form, key, id)} style={{ marginRight: 8 }}>保存</Button>
                )}
              </EditableContext.Consumer>
              {key !== 'temp' && (
                <EditableContext.Consumer>
                  {form => (
                    <a href="javascript:;" size="small" onClick={() => this.handleCancel(form, key)}>取消</a>
                  )}
                </EditableContext.Consumer>
              )}
            </span>
          ) : <a href="javascript:;" size="small" onClick={() => this.handleEdit(key)}>编辑</a>}
          {this.props.dataSource.length >= 1 ? (
            <span>
              <Divider type="vertical" />
              <Popconfirm title="确定要删除?" onConfirm={() => this.handleDelete(key, id)}>
                <a href="javascript:;" type="danger" size="small">删除</a>
              </Popconfirm>
            </span>
          ) : null}
          {id !== undefined ? (
            <span>
              <Divider type="vertical" />
              <Dropdown overlay={(
                <Menu>
                  <Menu.Item>
                    <Popconfirm title="确定要重置该学生的密码吗?" onConfirm={() => this.handleResetPassword(id)}>
                      <a href="javascript:;">重置密码</a>
                    </Popconfirm>
                  </Menu.Item>
                  <Menu.Item>
                    <Popconfirm title="确定要解绑该学生的微信号吗?" onConfirm={() => this.handleUntyingWx(id)}>
                      <a href="javascript:;">解绑微信号</a>
                    </Popconfirm>
                  </Menu.Item>
                </Menu>
              )}>
                <a className="ant-dropdown-link" href="javascript:;">
                  更多 <Icon type="down" />
                </a>
              </Dropdown>
            </span>
          ) : null}
        </div>
      );
    },
  }]

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

  isEditing = key => {
    return key === this.state.editingKey
  }

  handleEdit = key => {
    const { editingKey } = this.state
    if(editingKey !== '') {
      message.info('请先完成现有编辑')
      return
    }
    this.setState({ editingKey: key })
  }

  handleSave = (form, key, id) => {
    form.validateFields((err, row) => {
      if(err) {
        return
      }
      const newData = [...this.props.dataSource]
      const index = newData.findIndex(item => key === item.key)
      const item = newData[index]
      newData.splice(index, 1, {
        ...item,
        ...row,
        studentNum: row.studentNum,
        key: row.studentNum
      })
      for(let i = 0, len = newData.length; i < len; i ++) {
        if(i !== index && newData[i].key === newData[index].key) {
          message.error(`保存失败，已有学号为${newData[index].key}的学生`)
          return
        }
      }
      this.setState({ editingKey: '' })
      this.props.setStudents(newData)

      if(key === 'temp') {
        if(this.props.onAdd) {
          this.props.onAdd(row)
        }
      } else {
        if(this.props.onSave) {
          this.props.onSave(id, row)
        }
      }
    })
  }

  handleCancel = (form) => {
    form.validateFields((err, row) => {
      if(err) {
        return
      }
      this.setState({ editingKey: '' })
    })
  }

  handleDelete = (key, id) => {
    this.isEditing(key) && this.setState({ editingKey: '' })
    const dataSource = [...this.props.dataSource];
    this.props.setStudents(dataSource.filter(item => item.key !== key))
    if(key !== 'temp' && this.props.onDelete) {
      this.props.onDelete(id)
    }
  }

  handleAdd = () => {
    const { editingKey } = this.state
    const { dataSource, setStudents } = this.props
    if(editingKey !== '') {
      message.info('请先完成现有编辑')
      return
    }
    const newData = {
      key: 'temp',
      name: '',
      studentNum: '',
      classInfo: '',
    };
    setStudents([newData, ...dataSource])
    this.handleEdit('temp')
  }

  handleImportStudent = async (file, fileList) => {
    const { editingKey } = this.state
    const { dataSource, setStudents } = this.props
    if(editingKey !== '') {
      message.info('请先完成现有编辑')
      return Promise.reject()
    }
    this.setState({ importingStu: true })
    const jsonData = await excelToJson(file)

    // 如果缺少学号姓名班级任一项，不给添加
    const stuArr = []
    for(let i = 0, len = jsonData.length; i < len; i ++) {
      let item = jsonData[i]
      if(!item['学号'] || !item['姓名']) {
        if(!item['学号']) {
          message.error('缺少学号信息')
        } else if(!item['姓名']) {
          message.error('缺少姓名信息')
        }
        this.setState({ importingStu: false })
        return Promise.reject(jsonData)
      }
      stuArr.push({
        key: String(item['学号']),
        name: item['姓名'],
        studentNum: String(item['学号']),
        classInfo: item['班级']
      })
    }
    // const stuArr = jsonData.map(item => {
    //   return {
    //     key: String(item['学号']),
    //     name: item['姓名'],
    //     studentNum: String(item['学号']),
    //     classInfo: item['班级']
    //   }
    // })
    if(this.props.onImport) {
      this.props.onImport(file, () => this.setState({ importingStu: false }))
      return Promise.reject(jsonData)
    }
    setStudents(dataSource.concat([...stuArr]))
    this.setState({ importingStu: false })
    message.success(`成功导入${stuArr.length}名学生`)
    return Promise.reject(jsonData)
  }

  handleBatchDelete = () => {
    const { selectedRowKeys, selectedRowStuIds } = this.state
    selectedRowKeys.includes(this.state.editingKey) && this.setState({ editingKey: '' })
    const dataSource = [...this.props.dataSource]
    this.props.setStudents(dataSource.filter(item => !selectedRowKeys.includes(item.key)))
    this.setState({ selectedRowKeys: [] })
    if(this.props.onDelete) {
      this.props.onDelete(selectedRowStuIds)
    }
  }

  handleResetPassword = (id) => {
    if(this.props.onResetPassword) {
      this.props.onResetPassword(id)
    }
  }

  handleUntyingWx = (id) => {
    if(this.props.onUntyingWx) {
      this.props.onUntyingWx(id)
    }
  }

  onSelectChange = (selectedRowKeys, selectedRows) => {
    this.setState({
      selectedRowKeys,
      selectedRowStuIds: selectedRows.map(item => item.id)
    })
  }

  render() {
    const { selectedRowKeys } = this.state
    const { dataSource } = this.props
    const components = {
      body: {
        row: EditableFormRow,
        cell: EditableCell,
      },
    };
    const columns = this.columns.map((col) => {
      if (!col.editable) {
        return col;
      }
      return {
        ...col,
        onCell: record => ({
          record,
          dataIndex: col.dataIndex,
          title: col.title,
          editing: this.isEditing(record.key),
        }),
      };
    });
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange
    }
    const hasSelected = selectedRowKeys.length > 0

    return (
      <div className="studentTable">
        <Button onClick={this.handleAdd} type="primary" style={{ marginBottom: 16, marginRight: 20 }}>
          <Icon type="user-add" theme="outlined" />手动添加
        </Button>
        <Upload beforeUpload={this.handleImportStudent} showUploadList={false}>
          <Tooltip title='请确保excel文件具有"姓名"、"学号"、"班级"等三列' overlayStyle={{ maxWidth: 500 }}>
            <Button type="primary">
              <Icon type="import" theme="outlined" />批量导入
            </Button>
          </Tooltip>
        </Upload>
        <a href="/示例模版.xlsx" style={{ marginLeft: 10 }}><Icon type="file-excel" />示例模版</a>
        <Popconfirm title="确定要批量删除选中学生?" onConfirm={this.handleBatchDelete}>
          <Button type="danger" style={{ float: 'right' }} disabled={!hasSelected}>
            <Icon type="delete" theme="outlined" />批量删除
          </Button>
        </Popconfirm>
        <span style={{ marginLeft: 8 }}>{hasSelected ? `已选择${selectedRowKeys.length}名学生` : ''}</span>
        <Spin spinning={this.state.importingStu}>
          <Table
            components={components}
            rowClassName='editable-row'
            bordered
            pagination={false}
            scroll={{ y: 460 }}
            rowSelection={rowSelection}
            dataSource={dataSource}
            columns={columns}
          />
        </Spin>
      </div>
    );
  }
}

export default EditableTable
