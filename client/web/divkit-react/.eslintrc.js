module.exports = {
    extends: [
        './node_modules/@yandex-int/lint/eslintrc.js',
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
        'dist/**',
        'html-reporter/**',
        'mobile-alice-library-crossplatform/**',
        'src/dev.ts'
    ],

    rules: {
        camelcase: 'off',
        'comma-dangle': 'off',

        '@typescript-eslint/no-var-requires': 'off'
    }
};
