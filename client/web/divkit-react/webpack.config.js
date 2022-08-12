const path = require('path');
const isProd = process.env.NODE_ENV === 'production';

module.exports = {
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
    entry: {
        divkit: './src/divkit.tsx',
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        library: {
            type: 'commonjs2'
        }
    },
    externals: [
        'react',
        '@yandex-int/divkit/server',
        '@yandex-int/divkit/client-hydratable'
    ]
};
