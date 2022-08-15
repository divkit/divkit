module.exports = {
    extends: [
        './.eslintrc.base.js',
        'plugin:@typescript-eslint/recommended'
    ],
    parser: '@typescript-eslint/parser',
    parserOptions: {
        ecmaVersion: 2019,
        sourceType: 'module',
        extraFileExtensions: ['.svelte']
    },
    env: {
        es6: true,
        browser: true
    },
    plugins: [
        'svelte3',
        '@typescript-eslint'
    ],
    settings: {
        'svelte3/typescript': () => require('typescript')
    },
    ignorePatterns: [
        'node_modules/**',
        'dist/**',
        'reports/**',
        'hermione-gui-report/**',
        'src/dev.ts',
        '**/*.js'
    ],

    rules: {
        camelcase: 'off',
        'comma-dangle': 'off',

        '@typescript-eslint/no-var-requires': 'off',
        'no-multiple-empty-lines': 'off'
    },

    overrides: [{
        files: ['*.svelte'],
        processor: 'svelte3/svelte3'
    }]
};
