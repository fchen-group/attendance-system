import { asyncLoaded } from './util'

var loadedXLSX = false
var url = 'https://unpkg.com/xlsx@0.14.0/dist/xlsx.core.min.js'

function getJson(file) {
  return new Promise(function (resolve, reject) {
    var reader = new FileReader()
    reader.onload = function (e) {
      var data = e.target.result
      var wb = window.XLSX.read(data, { type: 'binary' })
      var json = window.XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]])
      resolve(json)
    }
    reader.readAsBinaryString(file)
  })
}

async function excelToJson(file) {
  if(!loadedXLSX) {
    await new Promise(function (resolve, reject) {
      asyncLoaded(url, function () {
        loadedXLSX = true;
        resolve()
      })
    })
    return await getJson(file)
  } else {
    return await getJson(file)
  }
}

export default excelToJson
