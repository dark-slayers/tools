import React from 'react';
import ReactDOM from 'react-dom';
import FrameTest from '../jsx/FrameTest.jsx';
import PageTitle from 'react-ui/lib/base/PageTitle.js';

const width = document.body.scrollWidth;
const height = document.body.scrollHeight;
ReactDOM.render(
  <PageTitle title="DEBUG"/>, document.getElementById('page-title'));
ReactDOM.render(
  <FrameTest width={width} height={height} url="http://bbs.ngacn.cc/read.php?tid=10246979&rand=179"/>, document.getElementById('main-ui'));
