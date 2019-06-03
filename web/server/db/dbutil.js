const mysql = require('mysql')

function createConnection() {
  const connection = mysql.createConnection({
    host: '127.0.0.1',
    port: '3306',
    user: 'root',
    password: '123456',
    database: 'sign'
  })
  return connection
}

function query(querySql, params) {
  return new Promise((resolve, reject) => {
    const connection = createConnection()
    connection.connect(err => {
      if(err) {
        console.error('error connecting: ' + err.stack)
        return
      }
      console.log('connected as id ' + connection.threadId)
    })
    connection.query(querySql, params, (error, result) => {
      if(error === null) {
        resolve(result)
      } else {
        reject(error)
      }
    })
    connection.end()
  })
}

module.exports.createConnection = createConnection
module.exports.query = query
