import React from 'react';
import ReactDOM from 'react-dom';
import Demo from '../jsx/Demo.jsx';
import PageTitle from '../jsx/ui/base/PageTitle.jsx';

ReactDOM.render(
  <PageTitle title="Demo"/>, document.getElementById('page-title'));
ReactDOM.render(
  <Demo />, document.getElementById('main-ui'));
