const path = require('path');
const isProd = process.env.NODE_ENV === 'production';

const base = {
    mode: isProd ? 'production' : 'development',
    devtool: isProd ? 'source-map' : 'eval-cheap-source-map',
    resolve: {
        extensions: ['.ts', '.d.ts', '.js'],
    },
    module: {
        rules: [
            {
                test: /\.[jt]sx?$/,
                exclude: /(node_modules|bower_components)/,
                use: {
                    loader: require.resolve('babel-loader'),
                    options: {
                        presets: [
                            ['@babel/preset-env', {
                                targets: {
                                    chrome: 58,
                                    safari: 11,
                                    firefox: 67
                                }
                            }],
                            ['@babel/preset-typescript', {
                                isTSX: true,
                                allExtensions: true,
                                onlyRemoveTypeImports: true
                            }],
                            '@babel/preset-react'
                        ]
                    }
                }
            }
        ]
    },
    externals: [
        'react',
        '@divkitframework/divkit/server',
        '@divkitframework/divkit/client-hydratable'
    ]
};

module.exports = [
    {
        ...base,
        target: 'web',
        entry: {
            divkit: './src/divkit-client.tsx',
        },
        output: {
            path: path.resolve(__dirname, 'dist/client/cjs'),
            library: {
                type: 'commonjs2'
            }
        },
    },
    {
        ...base,
        target: 'web',
        entry: {
            divkit: './src/divkit-client.tsx',
        },
        output: {
            path: path.resolve(__dirname, 'dist/client/esm'),
            library: {
                type: 'module'
            }
        },
        experiments: {
            outputModule: true
        }
    },
    {
        ...base,
        target: 'node',
        entry: {
            divkit: './src/divkit-server.tsx',
        },
        output: {
            path: path.resolve(__dirname, 'dist/server/cjs'),
            library: {
                type: 'commonjs2'
            }
        },
    },
    {
        ...base,
        target: 'node',
        entry: {
            divkit: './src/divkit-server.tsx',
        },
        output: {
            path: path.resolve(__dirname, 'dist/server/esm'),
            library: {
                type: 'module'
            },
            chunkFormat: 'module'
        },
        experiments: {
            outputModule: true
        }
    }
]
