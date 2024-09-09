module.exports = {
    extends: [
        './.eslintrc.base.cjs',
        'plugin:@typescript-eslint/recommended',
        'plugin:svelte/recommended'
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
        'svelte',
        '@typescript-eslint'
    ],
    settings: {
        'svelte/typescript': () => require('typescript')
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
        'no-multiple-empty-lines': 'off',

        'valid-jsdoc': 'off',

        'max-len': [
            2,
            {
                code: 140,
                ignoreUrls: true,
                ignoreComments: false,
                ignoreRegExpLiterals: true,
                ignoreStrings: true,
                ignoreTemplateLiterals: true,
                ignorePattern: 'require',
            },
        ]
    },

    overrides: [
        {
            files: ['*.svelte'],
            parser: 'svelte-eslint-parser',
            // Parse the `<script>` in `.svelte` as TypeScript by adding the following configuration.
            parserOptions: {
                parser: '@typescript-eslint/parser'
            }
        }
    ]
};
