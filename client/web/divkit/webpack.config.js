const path = require('path');
const webpack = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
// const HtmlWebpackPlugin = require('html-webpack-plugin');
const { typescript/*, postcss*/ } = require('svelte-preprocess');
const { asMarkupPreprocessor } = require('svelte-as-markup-preprocessor');
// const cssModules = require('svelte-preprocess-cssmodules');

const {COMPONENTS} = require('./src/components');

// const { version } = require('../package.json');

const isProd = process.env.NODE_ENV === 'production';

const enabledComponents = new Set(process.env.ENABLED_COMPONENTS?.split(/[,;\s]+/) || COMPONENTS);
const componentsEnvVars = COMPONENTS.reduce((acc, item) => {
    acc[`process.env.ENABLE_COMPONENT_${item.toUpperCase()}`] = JSON.stringify(enabledComponents.has(item));
    return acc;
}, {});
const enableExpressions = process.env.ENABLE_EXPRESSIONS !== '0';

const configCommon = ({
    isServer,
    hydratable,
    skipCss,
    devtool
}) => ({
    target: isServer ? ['node8'] : ['web', 'es6'],
    mode: isProd ? 'production' : 'development',
    devtool: isProd ? 'source-map' : 'cheap-module-source-map',
    optimization: {
        minimizer: [
            '...',
            new CssMinimizerPlugin()
        ]
    },
    resolve: {
        extensions: ['.ts', '.js', '.svelte', '.d.ts'],
    },
    module: {
        rules: [
            {
                test: /\.css$/i,
                use: [
                    {
                        loader: MiniCssExtractPlugin.loader,
                        options: {
                            emit: !skipCss
                        }
                    },
                    {
                        loader: 'css-loader',
                        options: {
                            modules: {
                                localIdentName: 'divkit-[hash:5]'
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
                    }
                    // require.resolve('postcss-loader')
                    // 'style-loader',
                    // 'css-loader'
                ],
            },

            {
                test: /\.([jt]s|svelte)$/,
                exclude: /(node_modules|bower_components)/,
                use: {
                    loader: require.resolve('babel-loader'),
                    options: {
                        presets: [
                            ['@babel/preset-env', {
                                browserslistEnv: isServer ? 'ssr' : 'production'
                            }],
                            '@babel/preset-typescript'
                        ]
                    }
                }
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
                                // localIdentName: 'divkit-[local]'
                                localIdentName: 'divkit-[hash:4]',
                                // localIdentName: 'divkit-[local]-[hash:3]',
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
                            generate: 'ssr',
                            dev: !isProd,
                            immutable: true,
                            accessors: false
                        } : {
                            dev: !isProd,
                            hydratable,
                            immutable: true,
                            accessors: false
                        },

                        emitCss: !skipCss && isProd,
                        // emitCss: true,
                    }
                },
            }
        ].filter(Boolean)
    },
    plugins: [
        new MiniCssExtractPlugin(),
        // new webpack.IgnorePlugin({resourceRegExp: /\.css$/i})
        new webpack.DefinePlugin({
            'process.env.DEVTOOL': JSON.stringify(Boolean(devtool)),
            'process.env.IS_PROD': JSON.stringify(isProd),
            'process.env.ENABLE_EXPRESSIONS': JSON.stringify(enableExpressions),
            ...componentsEnvVars
        })
    ].filter(Boolean)
});

module.exports = [{
    ...configCommon({ isServer: false, hydratable: false, skipCss: false }),
    entry: {
        client: './src/client.ts',
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        library: {
            type: 'commonjs'
        }
    }
}, {
    ...configCommon({ isServer: false, hydratable: true, skipCss: true }),
    entry: {
        'client-hydratable': './src/client.ts',
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        library: {
            type: 'commonjs'
        }
    }
}, {
    ...configCommon({ isServer: false, hydratable: true, skipCss: true, devtool: true }),
    entry: {
        'client-devtool': './src/client-devtool.ts',
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        library: {
            type: 'commonjs'
        }
    }
}, {
    ...configCommon({ isServer: true, skipCss: true }),
    entry: {
        server: './src/server.ts',
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        library: {
            type: 'commonjs'
        }
    }
}, {
    ...configCommon({ isServer: false, hydratable: false, skipCss: true }),
    entry: {
        client: './src/client.ts',
    },
    output: {
        path: path.resolve(__dirname, 'dist/esm'),
        library: {
            type: 'module'
        }
    },
    experiments: {
        outputModule: true
    }
}, {
    ...configCommon({ isServer: false, hydratable: true, skipCss: true }),
    entry: {
        'client-hydratable': './src/client.ts',
    },
    output: {
        path: path.resolve(__dirname, 'dist/esm'),
        library: {
            type: 'module'
        }
    },
    experiments: {
        outputModule: true
    }
}, {
    ...configCommon({ isServer: false, hydratable: true, skipCss: true, devtool: true }),
    entry: {
        'client-devtool': './src/client-devtool.ts',
    },
    output: {
        path: path.resolve(__dirname, 'dist/esm'),
        library: {
            type: 'module'
        }
    },
    experiments: {
        outputModule: true
    }
}, {
    ...configCommon({ isServer: true, skipCss: true }),
    target: ['node14'],
    entry: {
        server: './src/server.ts',
    },
    output: {
        path: path.resolve(__dirname, 'dist/esm'),
        library: {
            type: 'module'
        }
    },
    experiments: {
        outputModule: true
    }
}, {
    ...configCommon({ isServer: false, hydratable: false, skipCss: true }),
    entry: {
        client: './src/client.ts',
    },
    output: {
        path: path.resolve(__dirname, 'dist/browser'),
        library: {
            type: 'window',
            name: ['Ya', 'Divkit']
        }
    }
}, {
    ...configCommon({ isServer: false, hydratable: true, skipCss: true }),
    entry: {
        'client-hydratable': './src/client.ts',
    },
    output: {
        path: path.resolve(__dirname, 'dist/browser'),
        library: {
            type: 'window',
            name: ['Ya', 'Divkit']
        }
    }
}, {
    ...configCommon({ isServer: false, hydratable: true, skipCss: true, devtool: true }),
    entry: {
        'client-devtool': './src/client-devtool.ts',
    },
    output: {
        path: path.resolve(__dirname, 'dist/browser'),
        library: {
            type: 'window',
            name: ['Ya', 'Divkit']
        }
    }
}];

module.exports.configCommon = configCommon;
