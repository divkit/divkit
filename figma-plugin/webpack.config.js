const webpack = require('webpack');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const HtmlInlineScriptWebpackPlugin = require('html-inline-script-webpack-plugin');
const path = require('path');
const packageJson = require('./package.json');
const sveltePreprocess = require('svelte-preprocess');

const commonConfig = ({
    version,
    envVars,
    isProd,
    srcBase,
}) => ({
    target: 'web',
    mode: isProd ? 'production' : 'development',
    devtool: isProd ? false : 'inline-source-map',
    entry: {
        ui: path.resolve(srcBase, 'ui.ts'),
        code: path.resolve(srcBase, 'code.ts'),
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: [
                    {
                        loader: 'style-loader',
                        options: {
                            insert: 'head',
                        },
                    },
                    {
                        loader: 'css-loader',
                    },
                ],
            },
            {
                test: /\.styl$/,
                use: [
                    {
                        loader: 'style-loader',
                        options: {
                            insert: 'head',
                        },
                    },
                    {
                        loader: 'css-loader',
                    },
                    {
                        loader: 'stylus-loader',
                    },
                ],
            },
            {
                test: /\.([jt]s|svelte)$/,
                exclude: /node_modules/,
                use: {
                    loader: require.resolve('babel-loader'),
                    options: {
                        presets: [
                            '@babel/preset-env',
                            '@babel/preset-typescript',
                        ],
                    },
                },
            },
            {
                test: /\.svelte$/,
                use: {
                    loader: 'svelte-loader',
                    options: {
                        preprocess: sveltePreprocess(),

                        compilerOptions: {
                            dev: !isProd,
                        },

                        emitCss: isProd,
                    },
                },
            },
            {
                oneOf: [
                    {
                        test: /\.svg$/i,
                        resourceQuery: /raw/,
                        type: 'asset/source',
                    },
                    {
                        test: /\.(svg|png)$/i,
                        resourceQuery: /data-uri/,
                        type: 'asset/inline',
                    },
                ],
            },
        ],
    },

    resolve: {
        extensions: ['.ts', '.js', '.svelte', '.d.ts', '.styl', '.css'],
    },

    output: {
        filename: '[name].js',
        path: path.resolve(__dirname, 'dist'),
        publicPath: '/',
    },

    plugins: [
        new HtmlWebpackPlugin({
            template: path.resolve(srcBase, 'ui.html'),
            filename: 'ui.html',
            chunks: ['ui'],
            inject: 'body',
        }),
        new HtmlInlineScriptWebpackPlugin(),
        new webpack.DefinePlugin({
            'process.env.IS_PROD': JSON.stringify(isProd),
            'process.env.VERSION': JSON.stringify(version),
            ...envVars,
        }),
    ],

    optimization: {
        minimizer: [
            '...',
            new CssMinimizerPlugin(),
        ],
    },
});

module.exports = (env, argv) => {
    const isProd = argv.mode === 'production';

    return {
        ...commonConfig({
            version: packageJson.version,
            envVars: {
                'process.env.__PLAYGROUND_BASE_URL': JSON.stringify(''),
                'process.env.__HELP_URL': JSON.stringify(''),
                'process.env.__UPLOAD_IMAGE_URL': JSON.stringify(''),
                'process.env.__SECRET_INTERNALS': JSON.stringify(false),
            },
            isProd,
            srcBase: './src',
        }),
        output: {
            filename: '[name].js',
            path: path.resolve(__dirname, 'dist'),
            publicPath: '/',
        },
    };
};

module.exports.commonConfig = commonConfig;
