import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Carousel, Icon } from 'antd'
import Card from '../../../containers/Card'
import './Slide.scss'

export default class Slide extends Component {
  static propTypes = {
    data: PropTypes.array.isRequired,
    onReload: PropTypes.func.isRequired,
    onSelect: PropTypes.func,
    index: PropTypes.number,
  }

  state = {
    nowIndex: 0
  }

  componentWillMount = () => {
    if(this.props.index) {
      this.handleSelect(this.props.index)
    }
  }

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.index !== this.props.index) {
      this.handleSelect(nextProps.index)
    }
  }

  handleSelect = (index, courseId) => {
    if(index !== this.state.nowIndex) {
      this.setState({ nowIndex: index })
      this.slider.goTo(index && index - 1, false) // 跑到中间

      if(this.props.onSelect && courseId) {
        this.props.onSelect(courseId)
      }
    }
  }

  render() {
    const settings = {
      speed: 800,
      dots: false,
      arrows: true,
      vertical: true,
      infinite: false,
      slidesToShow: 3,
      slidesToScroll: 3,
      className: "slide",
      ref: ele => this.slider = ele,
      prevArrow: <Icon type="up" />,
      nextArrow: <Icon type="down" />
    }
    return (
      <Carousel {...settings}>
        {this.props.data.map((item, index) => (
          <div
            key={item.courseId}
            className={`slide__item ${this.state.nowIndex === index ? 'slide__item--active' : ''}`}
            onClick={() => this.handleSelect(index, item.courseId)}
          >
            <Card data={item} onReload={this.props.onReload}/>
          </div>
        ))}
        {/* 占位 */}
        {Array.from({length:　settings.slidesToShow - 1}).map((i, index) => <div key={index}></div>)}
      </Carousel>
    )
  }
}
