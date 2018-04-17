var webpack = require("webpack");
var path = require('path');
var UglifyJSPlugin = require('uglifyjs-webpack-plugin');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var config = require('./webpack.base.config.js');
var entrys = require('./entrys.js');

config.module.rules[0].use[0].options.plugins.push('transform-remove-console');
config.plugins.push(new UglifyJSPlugin({
  uglifyOptions: {
    ie8: false,
    output: {
      comments: false,
      beautify: false,
    },
    mangle: {
      keep_fnames: true
    },
    compress: {
      warnings: false,
      drop_console: true
    },
  }
}));
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
