import React from 'react';
import ReactDOM from 'react-dom';
import IndexPage from '../jsx/Index.jsx';
import PageTitle from '../jsx/ui/base/PageTitle.jsx';

ReactDOM.render(
  <PageTitle title="Index"/>, document.getElementById('page-title'));
ReactDOM.render(
  <IndexPage />, document.getElementById('main-ui'));
