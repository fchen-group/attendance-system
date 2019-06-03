const weekMap = {0: '日', 1: '一', 2: '二', 3: '三', 4: '四', 5: '五', 6: '六'}

function asyncLoaded(url, callback) {
  var script = document.createElement('script');
  script.type = "text/javascript";
  if(script.readyState) {//IE
    script.onreadystatechange = function () {
      if(script.readyState === "complete" || script.readyState === "loaded") {
        callback();
        script.onreadystatechange = null;
      }
    }
  }else {
    script.onload = function () {
      callback();
    }
  }
  script.src = url;
  document.body.appendChild(script);
}

function getDuplicateInArray(arr) {
  var result = []
  arr.concat().sort().sort(function (a, b) {
    a === b && result.indexOf(a) === -1 && result.push(a)
  })
  return result
}

function getSemester() {
  var date = new Date()
  var year = date.getFullYear()
  var month = date.getMonth() + 1
  if(month >= 2 && month < 8) {
    return year + '春季课程'
  } else if(month >= 8) {
    return year + '秋季课程'
  } else {
    return year - 1 + '秋季课程'
  }
}

export {
  weekMap,
  asyncLoaded,
  getDuplicateInArray,
  getSemester
}
