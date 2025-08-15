module.exports = {
    extends: [
        'plugin:@typescript-eslint/recommended'
    ],
    parser: '@typescript-eslint/parser',
    parserOptions: {
        ecmaVersion: 2019,
        sourceType: 'module'
    },
    env: {
        es6: true,
        browser: true
    },
    plugins: [
        '@typescript-eslint'
    ],
    ignorePatterns: [
        'node_modules/**',
        'dist/**'
    ],

    rules: {
        camelcase: 'off',
        'comma-dangle': 'off',

        '@typescript-eslint/no-var-requires': 'off'
    }
};
