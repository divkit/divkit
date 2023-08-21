const path = require('path');
const webpack = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const { typescript/*, postcss*/ } = require('svelte-preprocess');
const { asMarkupPreprocessor } = require('svelte-as-markup-preprocessor');
// const cssModules = require('svelte-preprocess-cssmodules');

const { COMPONENTS } = require('./src/components');

// const { version } = require('../package.json');

const isProd = process.env.NODE_ENV === 'production';

const enabledComponents = new Set(process.env.ENABLED_COMPONENTS?.split(/[,;\s]+/) || COMPONENTS);
const componentsEnvVars = COMPONENTS.reduce((acc, item) => {
    acc[`process.env.ENABLE_COMPONENT_${item.toUpperCase()}`] = JSON.stringify(enabledComponents.has(item));
    return acc;
}, {});
const enableExpressions = process.env.ENABLE_EXPRESSIONS !== '0';

const configCommon = isServer => ({
    mode: isProd ? 'production' : 'development',
    devtool: isProd ? false : 'cheap-module-source-map',
    output: {
        publicPath: '/',
        path: path.resolve(__dirname, 'dist')
    },
    optimization: {
        minimizer: [
            '...',
            new CssMinimizerPlugin()
        ]
    },
    resolve: {
        extensions: ['.ts', '.js', '.svelte', '.d.ts'],
        conditionNames: ['...',  'svelte']
    },
    module: {
        rules: [
            {
                test: /\.css$/i,
                use: [
                    MiniCssExtractPlugin.loader,
                    {
                        loader: 'css-loader',
                        options: {
                            modules: {
                                localIdentName: isProd ? 'divkit-[hash:4]' : '[local]-[hash:4]'
                            }
                        }
                    },
                    {
                        loader: 'postcss-loader',
                        options: {
                            postcssOptions: {
                                plugins: [
                                    require('autoprefixer')
                                ]
                            }
                        }
                    },
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
                        preprocess: [
                            asMarkupPreprocessor([
                                typescript(),
                                /*postcss({
                                    plugins: [
                                        require('autoprefixer')
                                    ]
                                })*/
                            ]),
                            /*cssModules({
                                useAsDefaultScoping: true,
                                // localIdentName: 'divkit-[local]',
                                localIdentName: 'divkit-[local]-[hash:3]',
                                // localIdentName: 'divkit-[hash:4]',
                                // localIdentName: '[hash:4]',
                                includeAttributes: ['cls'],
                                parseExternalStylesheet: true
                            })*/
                        ],
                        /*preprocess: sveltePreprocess({
                            typescript: true
                        }),*/

                        onwarn(e, cb) {
                            if (e.message.includes('has unused export property')) {
                                return;
                            }
                            cb(e);
                        },

                        compilerOptions: isServer ? {
                            generate: 'ssr'
                        } : {
                            dev: !isProd,
                            hydratable: true,
                            immutable: true,
                            accessors: true
                        },

                        emitCss: isProd,
                        // emitCss: true,
                        hotReload: !isProd,
                        hotOptions: isProd ? undefined : {
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
                use: {
                    loader: require.resolve('babel-loader'),
                    options: {
                        presets: [
                            ['@babel/preset-env', {
                                browserslistEnv: isServer ? 'ssr' : 'development'
                            }],
                            '@babel/preset-typescript'
                        ]
                    }
                }
            }
        ]
    }
});

module.exports = [{
    ...configCommon(false),
    target: 'web',
    entry: {
        client: './src/dev.ts',
    },
    output: {
        // libraryTarget: 'commonjs'
    },
    plugins: [
        new MiniCssExtractPlugin(),
        new HtmlWebpackPlugin({
            template: path.resolve(__dirname, 'src', 'dev.html'),
            scriptLoading: 'blocking'
        }),
        !isProd && new webpack.HotModuleReplacementPlugin(),
        new webpack.DefinePlugin({
            'process.env.DEVTOOL': JSON.stringify(true),
            'process.env.IS_PROD': JSON.stringify(isProd),
            'process.env.ENABLE_EXPRESSIONS': JSON.stringify(enableExpressions),
            ...componentsEnvVars
        })
    ].filter(Boolean),
    devServer: isProd ? undefined : {
        // hot: true,
        // contentBase: path.resolve(__dirname, 'src')
    }
}];
