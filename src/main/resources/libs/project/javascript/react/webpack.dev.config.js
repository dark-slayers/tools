var webpack = require("webpack");
var path = require('path');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var config = require('./webpack.base.config.js');
var entrys = require('./entrys.js');
const addEntrys = {
	'test': 'Tets页面',
};
Object.assign(entrys, addEntrys);
const list = Object.keys(entrys);
for (let key of list) {
  config.entry[key] = "./src/entry/" + key + ".entry.js",
    config.plugins.push(new HtmlWebpackPlugin({
      title: entrys[key],
      filename: key + '.html',
      template: './src/template.html',
      chunks: ['vendor', 'runtime', key]
    }));
}
module.exports = config;