const { transform } = require('@babel/core');

module.exports.process = function process(src, filename) {
    const plugins = [
    ];

    const result = transform(src, {
        filename,
        presets: [
            '@babel/preset-typescript',
            'jest',
            [
                '@babel/preset-env',
                {
                    targets: {
                        node: 'current'
                    }
                }
            ]
        ],
        sourceMaps: 'inline',
        plugins
    });

    return result;
};
