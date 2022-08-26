const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

const common = (isServer) => {
    return {
        target: isServer ? 'node' : 'web',
        mode: 'development',
        output: {
            path: path.resolve(__dirname, 'dist')
        },
        module: {
            rules: [
                {
                    test: /\.css$/i,
                    use: [
                        MiniCssExtractPlugin.loader,
                        'css-loader'
                    ]
                },
                {
                    test: /\.tsx?$/,
                    use: {
                        loader: require.resolve('babel-loader'),
                        options: {
                            presets: [
                                ['@babel/preset-typescript', {
                                    isTSX: true,
                                    allExtensions: true
                                }],
                                '@babel/preset-react'
                            ]
                        }
                    }
                }
            ]
        },
        plugins: [
            new MiniCssExtractPlugin(),
            !isServer && new HtmlWebpackPlugin({
                template: path.resolve(__dirname, 'index.html')
            }),
            new webpack.DefinePlugin({
                'process.env.IS_SERVER': JSON.stringify(isServer),
            })
        ].filter(Boolean)
    };
};

module.exports = [{
    ...common(false),
    entry: {
        client: path.resolve(__dirname, 'src/client.tsx')
    }
}, {
    ...common(true),
    entry: {
        server: path.resolve(__dirname, 'src/server.tsx')
    }
}];
