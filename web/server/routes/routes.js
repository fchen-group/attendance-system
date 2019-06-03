module.exports = function (Router) {
  const router = new Router()

  router.get('/', async (ctx, next) => {
    await ctx.render('index')
  })

  return router.routes();
}
