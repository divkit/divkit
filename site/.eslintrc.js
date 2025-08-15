module.exports = {
    env: {
        node: true,
        'es2021': true
    },
    extends: [
        'eslint:recommended'
    ],
    parserOptions: {
        'ecmaVersion': 12,
        'sourceType': 'module'
    },
    rules: {
        quotes: [
            2,
            'single',
            {avoidEscape: true}
        ]
    },
    overrides: [
        {
            files: ['*.svelte'],
            processor: 'svelte3/svelte3'
        },
        {
            env: {
                node: false,
                commonjs: true,
                browser: true
            },
            files: [
                'client/src/**'
            ],
            extends: [
                'eslint:recommended',
                'plugin:@typescript-eslint/recommended'
            ],
            parser: '@typescript-eslint/parser',
            plugins: [
                'svelte3',
                '@typescript-eslint'
            ],
            settings: {
                'svelte3/typescript': true
            }
        }
    ],
    globals: {
        'Ya': true,
        'process': true
    }
};
