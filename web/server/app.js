const path = require('path')
const Koa = require('koa')
const Router = require('koa-router')
const bodyParser = require('koa-bodyparser')
const cors = require('koa2-cors')
const views = require('koa-views')
const compress = require('koa-compress')
const staticCache = require('koa-static-cache')

const api = require('./routes/api')
const routes = require('./routes/routes')

const app = new Koa()

const PORT = 80


app
  .use(compress())
  .use(cors())
  .use(bodyParser())
  .use(views('../build'))
  .use(api(Router))
  .use(routes(Router))
  .use(staticCache(path.join(__dirname, '..', 'build'), {
    maxAge: 365 * 24 * 60 * 60
  }))

app.listen(PORT, '0.0.0.0', () => {
  console.log('server running on port ' + PORT);
});

