import React, { Component } from 'react'
import { HashRouter, Route, Switch, Redirect } from 'react-router-dom'
// import Loadable from 'react-loadable'
import { Layout } from 'antd'
import './App.css'
import Login from './routes/login/Index'
import Home from './routes/home/Index'
import Detail from './routes/detail/Index'
import Manage from './routes/manage/Index'
import MyHeader from './containers/MyHeader'

// const Loading = () => <div>Loading...</div>
// const Login = Loadable({ loader: () => import('./routes/login/Index'), loading: Loading })
// const Home  = Loadable({ loader: () => import('./routes/home/Index'), loading: Loading })
// const Detail = Loadable({ loader: () => import('./routes/detail/Index'), loading: Loading })
// const Manage = Loadable({ loader: () => import('./routes/manage/Index'), loading: Loading })
// const MyHeader = Loadable({ loader: () => import('./containers/MyHeader'), loading: Loading })

const { Header, Content } = Layout

class App extends Component {
  componentWillMount() {

  }

  render() {
    const isLogin = localStorage.getItem('username')
    return (
      <HashRouter>
        <Switch>
          <Route path="/login" exact render={() => isLogin ? <Redirect to="/" /> : <Login/>} />
          <Route path={["/", "/detail/:courseId?", "/manage"]} exact render={({ match }) => isLogin
            ? (
              <Layout>
                <Header>
                  <MyHeader match={match}/>
                </Header>
                <Content>
                  <Route path="/" exact component={Home} />
                  <Route path="/detail/:courseId?" exact component={Detail} />
                  <Route path="/manage" exact component={Manage} />
                </Content>
              </Layout>
            ) : <Redirect to="/login" />
          } />
          <Route render={() => 404} />
        </Switch>
      </HashRouter>
    );
  }
}

export default App;
