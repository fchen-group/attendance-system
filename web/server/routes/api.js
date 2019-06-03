const axios = require('axios')
const dbutil = require('../db/dbutil')
const bcrypt = require('bcrypt')

module.exports = function (Router) {

  const router = new Router();

  // 获取微信用户openid
  router.get('/user/openid', async (ctx, next) => {
    const { code } = ctx.request.query;
    const APPID = 'wxe31c8b9041ec51cd';
    const SECRET = '04895f16558cb65a7ee98e2919e3cd06';
    const URL = 'https://api.weixin.qq.com/sns/jscode2session?appid=' +
      APPID + '&secret=' + SECRET + '&js_code=' + code + '&grant_type=authorization_code';
    const { data } = await axios({
      method: 'GET',
      url: URL
    });
    ctx.body = { ...data };
  });


  // 用户是否绑定openid
  router.get('/user/isExist', async (ctx, next) => {
    const { openid } = ctx.request.query;
    const querySql = 'select id from user where open_id = ?;'
    const params = [openid]
    const user = await dbutil.query(querySql, params)
    if (!user.length) {
      ctx.body = {
        status: "fail",
        msg: "尚未绑定"
      }
    } else {
      ctx.body = {
        status: "success",
        msg: "已绑定"
      }
    }
  });

  // 登录
  router.post('/user/login', async (ctx, next) => {
    const { password, username, openid } = ctx.request.body;
    // await sql.startTransaction();
    // const user = await sql.executeTransaction("select password from user where name = ?;", [username]);
    const user = await dbutil.query('select password from user where name = ?;', [username])
    if (!user.length) {
      ctx.body = {
        status: "fail",
        msg: "不存在此用户"
      }
    } else {
      if (user[0].password === bcrypt.hashSync(password,user[0].password)) {
        await dbutil.query('update user set open_id = ? where name = ?;', [openid, username])
        // await sql.executeTransaction("update user set open_id = ? where name = ?;", [openid, username]);
        ctx.body = {
          status: "success",
          msg: "绑定成功"
        }
      } else {
        ctx.body = {
          status: "fail",
          msg: "密码错误"
        }
      }
    }
    // await sql.stopTransaction();
  });

  return router.routes();
}
