var webpack = require("webpack");
var path = require('path');
var HtmlWebpackPlugin = require('html-webpack-plugin');

var config = {
  entry: {
    vendor: ["react", 'react-dom'],
  },
  output: {
    path: path.resolve(__dirname, './dist'),
    filename: "[name].[chunkhash:4].bundle.js"
  },
  plugins: [
    new webpack.optimize.CommonsChunkPlugin({
      name: 'vendor',
      filename: "vendor.[chunkhash:4].bundle.js"
    }),
    new webpack.optimize.CommonsChunkPlugin({
      name: 'runtime',
      filename: "runtime.[chunkhash:4].bundle.js"
    })
  ],
  module: {
    rules: [{
      test: /\.(js|jsx)?$/,
      exclude: /node_modules/,
      use: [{
        loader: 'babel-loader',
        options: {
          presets: [
            ['env', {
              modules: false,
              "targets": {
                "browsers": ["Chrome >= 60", "Firefox >= 55","ie >= 9"]
              }
            }],
            ['stage-0'],
            ['react']
          ],
          plugins: ['syntax-dynamic-import']
        }
      }]
    }, {
      test: /\.css$/,
      use: [{
        loader: 'style-loader',
      }, {
        loader: 'css-loader',
      }]
    }, {
      test: /\.(png|jpg|gif)$/,
      use: [{
        loader: 'url-loader?limit=200000',
      }]
    }]
  }
}

module.exports = config;
