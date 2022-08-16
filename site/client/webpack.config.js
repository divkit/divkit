const path = require('path');
const webpack = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const sveltePreprocess = require('svelte-preprocess');
const {
    S3_PATH,
    VERSION = require('../package.json').version
} = process.env;
const isProd = process.env.NODE_ENV === 'production';
const publicPath = S3_PATH ?
    `https://s3.mds.yandex.net/${S3_PATH}/${VERSION}/` : '/';

const configCommon = {
    target: 'web',
    mode: isProd ? 'production' : 'development',
    devtool: isProd ? false : 'cheap-module-source-map',
    output: {
        publicPath,
        path: path.resolve(__dirname, 'dist')
    },
    optimization: {
        minimizer: [
            `...`,
            new CssMinimizerPlugin()
        ]
    },
    resolve: {
        extensions: [ '.tsx', '.ts', '.js', '.svelte' ],
        /*fallback: {
            path: false,
            fs: false,
            assert: false,
            util: false
        }*/
    },
    module: {
        rules: [
            {
                test: /\.css$/i,
                use: [
                    MiniCssExtractPlugin.loader,
                    'css-loader',
                    // require.resolve('postcss-loader')
                    // 'style-loader',
                    // 'css-loader'
                ],
            },
            {
                test: /\.svelte$/,
                use: {
                    loader: 'svelte-loader',
                    options: {
                        preprocess: sveltePreprocess({
                            typescript: true
                        }),

                        compilerOptions: {
                            dev: !isProd,
                            accessors: true
                        },

                        emitCss: isProd,
                        // emitCss: true,
                        hotReload: !isProd,
                        hotOptions: {
                            // Prevent preserving local component state
                            preserveLocalState: false,

                            // If this string appears anywhere in your component's code, then local
                            // state won't be preserved, even when noPreserveState is false
                            noPreserveStateKey: '@!hmr',

                            // Prevent doing a full reload on next HMR update after fatal error
                            noReload: false,

                            // Try to recover after runtime errors in component init
                            optimistic: false,

                            // --- Advanced ---

                            // Prevent adding an HMR accept handler to components with
                            // accessors option to true, or to components with named exports
                            // (from <script context="module">). This have the effect of
                            // recreating the consumer of those components, instead of the
                            // component themselves, on HMR updates. This might be needed to
                            // reflect changes to accessors / named exports in the parents,
                            // depending on how you use them.
                            acceptAccessors: true,
                            acceptNamedExports: true,
                        }
                    }
                },
            },
            {
                test: /\.[jt]s$/,
                exclude: /(node_modules|bower_components)/,
                resourceQuery: {not: /inline/},
                use: {
                    loader: require.resolve('babel-loader'),
                    options: {
                        presets: [
                            ['@babel/preset-env', {
                                targets: {
                                    chrome: 80,
                                    safari: 12,
                                    firefox: 80
                                }
                            }],
                            '@babel/preset-typescript'
                        ]
                    }
                }
            },
            {
                resourceQuery: /inline/,
                type: 'asset/source'
            },
            {
                test: /\.ttf$/,
                use: ['file-loader']
            }/*,
            {
                test: /\.svg$/,
                // type: 'asset/resource'
                use: 'svg-inline-loader'
            }*/,
            {
                test: /\.svg$/,
                type: 'asset/resource'
            }
        ]
    }
};

module.exports = [{
    ...configCommon,
    entry: {
        main: './src/index.ts',
    },
    plugins: [
        new MiniCssExtractPlugin(),
        new HtmlWebpackPlugin({
            template: 'index.html'
        }),
        new webpack.HotModuleReplacementPlugin(),
        new webpack.DefinePlugin({
            'process.env.VERSION': JSON.stringify(VERSION)
        })
    ],
    devServer: {
        hot: true,
        static: {
            directory: path.resolve(__dirname, 'src')
        }
    }
}, {
    ...configCommon,
    entry: {
        // monaco
        'editor.worker': 'monaco-editor/esm/vs/editor/editor.worker.js',
        'json.worker': 'monaco-editor/esm/vs/language/json/json.worker',
        'typescript.worker': 'monaco-editor/esm/vs/language/typescript/ts.worker'
    },
    plugins: [
    ]
}];
