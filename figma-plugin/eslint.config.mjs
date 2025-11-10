import { defineConfig } from 'eslint/config';
import js from '@eslint/js';
import tseslint from 'typescript-eslint';
import svelte from 'eslint-plugin-svelte';
import globals from 'globals';
import svelteConfig from './svelte.config.mjs';

export default defineConfig([
    js.configs.recommended,
    tseslint.configs.recommended,
    ...svelte.configs.recommended,
    {
        languageOptions: {
            globals: {
                ...globals.browser,
                ...globals.node,
            },
        },
    },
    {
        files: ['**/*.svelte'],
        languageOptions: {
            parserOptions: {
                projectService: true,
                extraFileExtensions: ['.svelte'],
                parser: tseslint.parser,
                svelteConfig,
            },
        },
    },
    {
        rules: {
            '@typescript-eslint/no-require-imports': 'off',
            'svelte/require-each-key': 'off',

            indent: [
                2,
                4,
                {
                    SwitchCase: 1,
                    VariableDeclarator: 1,
                    outerIIFEBody: 1,
                    // MemberExpression: null,
                    FunctionDeclaration: {
                        parameters: 1,
                        body: 1,
                    },
                    FunctionExpression: {
                        parameters: 1,
                        body: 1,
                    },
                    CallExpression: {
                        arguments: 1,
                    },
                    ArrayExpression: 1,
                    ObjectExpression: 1,
                    ImportDeclaration: 1,
                    flatTernaryExpressions: false,
                    // list derived from https://github.com/benjamn/ast-types/blob/HEAD/def/jsx.js
                    ignoredNodes: [
                        'JSXElement',
                        'JSXElement > *',
                        'JSXAttribute',
                        'JSXIdentifier',
                        'JSXNamespacedName',
                        'JSXMemberExpression',
                        'JSXSpreadAttribute',
                        'JSXExpressionContainer',
                        'JSXOpeningElement',
                        'JSXClosingElement',
                        'JSXText',
                        'JSXEmptyExpression',
                        'JSXSpreadChild',
                    ],
                    ignoreComments: false,
                },
            ],

            semi: [2, 'always', { omitLastInOneLineBlock: true }],
            'semi-spacing': [2, { before: false, after: true }],
            'wrap-iife': [2, 'inside'],
            // "new-cap": [2, { "properties": false }], // Сомнительное правило, слишком много левых ошибок
            'no-caller': 2,
            'no-cond-assign': [2, 'except-parens'],
            'no-constant-condition': 2,
            'no-debugger': 2,
            'no-dupe-args': 2,
            'no-dupe-keys': 2,
            'no-duplicate-case': 2,
            'no-empty': [2, { allowEmptyCatch: true }],
            'no-extra-boolean-cast': 2,
            // "no-extra-parens": [2, "all"],
            'no-extra-semi': 2,
            'no-func-assign': 2,
            'no-new': 2,
            'no-sparse-arrays': 2,
            'no-unexpected-multiline': 2,
            'no-unreachable': 2,

            'max-params': [2, 5],
            'max-depth': [1, 4],
            'no-eq-null': 0,
            'no-unused-expressions': 0,
            'dot-notation': 2,
            'use-isnan': 2,

            // Best practices
            // "array-callback-return" is not allowed empty "return" statement in map/reduce etc.
            // "array-callback-return": 2,
            'block-scoped-var': 2,
            complexity: [0, 11],
            curly: [2, 'multi-line'],
            eqeqeq: [2, 'always', { null: 'ignore' }],
            'no-else-return': 2,
            'no-extra-bind': 2,
            'no-implicit-coercion': 2,
            'no-return-assign': 0,
            'no-sequences': 2,
            yoda: 2,

            // Variables
            'no-restricted-globals': [2, 'fdescribe', 'fit'],
            'no-var': 1,

            // Codestyle
            'arrow-parens': [2, 'as-needed'],
            'array-bracket-spacing': [2, 'never'],
            'brace-style': [2, '1tbs', { allowSingleLine: true }],
            camelcase: [2, { properties: 'never' }],
            'comma-dangle': ['error', {
                arrays: 'always-multiline',
                objects: 'always-multiline',
                imports: 'never',
                exports: 'never',
                functions: 'never',
            }],
            'comma-spacing': [2, { before: false, after: true }],
            'eol-last': 2,
            'func-call-spacing': [2, 'never'],
            'block-spacing': 2,
            'keyword-spacing': [2, { before: true, after: true }],
            'max-len': [
                2,
                {
                    code: 120,
                    ignoreUrls: true,
                    ignoreComments: false,
                    ignoreRegExpLiterals: true,
                    ignoreStrings: true,
                    ignoreTemplateLiterals: true,
                    ignorePattern: 'require',
                },
            ],
            'no-lonely-if': 2,
            'no-mixed-spaces-and-tabs': 2,
            'no-multi-spaces': 2,
            'no-multiple-empty-lines': [2, { max: 1, maxBOF: 0, maxEOF: 0 }],
            'no-trailing-spaces': 2,
            'no-unneeded-ternary': 2,
            'no-nested-ternary': 2,
            'object-curly-spacing': [2, 'always'],
            'one-var-declaration-per-line': [2, 'initializations'],
            'one-var': [2, { let: 'never', const: 'never' }],
            'operator-linebreak': [2, 'after'],
            'padded-blocks': [2, 'never'],
            'quote-props': [2, 'as-needed', { numbers: true }],
            quotes: [2, 'single', { avoidEscape: true }],
            'space-before-blocks': [2, 'always'],
            'space-before-function-paren': [2, { anonymous: 'never', named: 'never', asyncArrow: 'always' }],
            'space-in-parens': 2,
            'no-console': [2, { allow: ['assert', 'error', 'warn'] }],
            'key-spacing': [2, { beforeColon: false, afterColon: true, mode: 'strict' }],
            'space-infix-ops': 2,

            // https://github.com/typescript-eslint/typescript-eslint/tree/master/packages/eslint-plugin#supported-rules
            '@typescript-eslint/consistent-type-assertions': 2,
            '@typescript-eslint/no-empty-interface': 2,
            '@typescript-eslint/no-explicit-any': 2,
            '@typescript-eslint/no-unused-vars': [
                2,
                {
                    args: 'after-used',
                    argsIgnorePattern: '^_',
                    ignoreRestSiblings: true,
                    vars: 'all',
                    varsIgnorePattern: '^_',
                },
            ],
            '@typescript-eslint/no-non-null-assertion': 2,
        },
    },
]);
