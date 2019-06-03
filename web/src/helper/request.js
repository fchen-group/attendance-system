import { message } from 'antd'
export default async function (path, opt) {
  const url = 'https://api.inforsecszu.net/v1' + path
  const options = Object.assign({
    method: 'GET',
    mode: 'cors',
    credentials:'include'
  }, opt)

  try {
    const response = await fetch(url, options)
    if(response.status === 403) {
      localStorage.removeItem('username')
      message.error('登录已过期，请重新登录')
      setTimeout(() => {
        window.location.href = '/'
      }, 2000)
    } else {
      const data = await response.json()
      if(data.status === 'success') {
        return data
      } else {
        // 错误处理
        if(data.msg !== 'invalid username or password') {
          message.error(data.msg)
        }
      }
    }
  } catch(e) {
    // 错误处理
    console.log('异常')
    console.log(e)
  }
}
