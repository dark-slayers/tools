import React from 'react';
import ReactDOM from 'react-dom';
import Test from '../jsx/Test.jsx';
import PageTitle from 'react-ui/lib/base/PageTitle.js';


ReactDOM.render(
  <PageTitle title="Test"/>, document.getElementById('page-title'));
ReactDOM.render(
  <Test/>, document.getElementById('main-ui'));
