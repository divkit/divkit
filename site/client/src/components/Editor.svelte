<script lang="ts" context="module">
    /* eslint-disable @typescript-eslint/no-explicit-any */
    import { urlPath } from '../utils/const';
    import type * as monaco from 'monaco-editor';

    let monacoPromose: Promise<{
        monaco: typeof import('monaco-editor');
        jsonModelUri: monaco.Uri;
    }> | undefined;
    export function loadMonaco() {
        if (monacoPromose) {
            return monacoPromose;
        }

        monacoPromose = import('monaco-editor').then(monaco => {
            const origSetTokensProvider = monaco.languages.setTokensProvider;
            const origSetLanguageConfiguration = monaco.languages.setLanguageConfiguration;

            monaco.languages.setLanguageConfiguration = (id, config) => {
                if (id === 'json') {
                    config.brackets = [
                        ['{', '}'],
                        ['[', ']'],
                        ['@{', '}'],
                        ['(', ')']
                    ];
                }

                return origSetLanguageConfiguration.call(monaco.languages, id, config);
            };

            monaco.languages.setTokensProvider = (languageId, provider) => {
                const res = origSetTokensProvider.call(monaco.languages, languageId, provider);
                if (languageId === 'json') {
                    monaco.languages.setMonarchTokensProvider('json', {
                        defaultToken: 'invalid',
                        tokenPostfix: '.json',

                        keywords: [
                            'true', 'false',
                        ],

                        divkitKeywords: [
                            'true', 'false',
                        ],

                        operators: [
                            ':',
                        ],

                        symbols: /[=><!~?:&|+\-*/^%]+/,
                        escapes: /\\(?:[abfnrtv"']|u[0-9A-Fa-f]{4}|\\\\\\|\\@\{)/,
                        digits: /\d+(_+\d+)*/,

                        tokenizer: {
                            root: [
                                [/\{/, 'delimiter.bracket', '@obj'],
                                [/\[/, 'delimiter.bracket', '@arr'],
                                { include: 'common' }
                            ],

                            obj: [
                                [/\}/, 'delimiter.bracket', '@pop'],
                                [/"([^"\\]|\\.)*$/, 'string.invalid'],  // non-teminated string
                                [/'([^'\\]|\\.)*$/, 'string.invalid'],  // non-teminated string
                                [/"/, 'string.key', '@string_double_key'],
                                [/:/, 'delimiter', '@obj_value'],
                                { include: 'common' }
                            ],

                            arr: [
                                [/\{/, 'delimiter.bracket', '@obj'],
                                [/\[/, 'delimiter.bracket', '@arr'],
                                [/\]/, 'delimiter.bracket', '@pop'],
                                [/"([^"\\]|\\.)*$/, 'string.invalid'],  // non-teminated string
                                [/'([^'\\]|\\.)*$/, 'string.invalid'],  // non-teminated string
                                [/"/, 'string.value', '@string_double_value'],
                                [/[,]/, 'delimiter'],
                                { include: '@whitespace' },
                                // numbers
                                [/(@digits)[eE]([-+]?(@digits))?/, 'number.float'],
                                [/(@digits)\.(@digits)([eE][-+]?(@digits))?/, 'number.float'],
                                [/(@digits)/, 'number'],
                                // identifiers and keywords
                                [/[a-z_$][\w$]*/, {
                                    cases: {
                                        '@keywords': 'keyword',
                                        '@default': 'invalid'
                                    }
                                }],
                                // { include: 'common' }
                            ],

                            string_double_key: [
                                [/[^\\"]+/, 'string.key'],
                                [/@escapes/, 'string.escape'],
                                [/\\./, 'string.escape.invalid'],
                                [/"/, 'string.key', '@pop']
                            ],

                            obj_value: [
                                [/\{/, 'delimiter.bracket', '@obj'],
                                [/\[/, 'delimiter.bracket', '@arr'],
                                [/,/, 'delimiter', '@obj'],
                                { include: 'common' }
                            ],

                            common: [
                                // identifiers and keywords
                                [/[a-z_$][\w$]*/, {
                                    cases: {
                                        '@keywords': 'keyword',
                                        '@default': 'invalid'
                                    }
                                }],

                                // whitespace
                                { include: '@whitespace' },

                                [/@symbols/, {
                                    cases: {
                                        '@operators': 'delimiter',
                                        '@default': ''
                                    }
                                }],

                                // numbers
                                [/(@digits)[eE]([-+]?(@digits))?/, 'number.float'],
                                [/(@digits)\.(@digits)([eE][-+]?(@digits))?/, 'number.float'],
                                [/(@digits)/, 'number'],

                                [/[,]/, 'delimiter'],

                                // strings
                                [/"([^"\\]|\\.)*$/, 'string.invalid'],  // non-teminated string
                                [/'([^'\\]|\\.)*$/, 'string.invalid'],  // non-teminated string
                                [/"/, 'string.value', '@string_double_value'],
                            ],

                            whitespace: [
                                [/[ \t\r\n]+/, ''],
                            ],

                            string_double_value: [
                                [/@\{/, { token: 'delimiter.bracket', next: '@bracketCounting' }],
                                [/[^\\"]/, 'string.value'],
                                [/@escapes/, 'string.escape'],
                                [/\\./, 'string.escape.invalid'],
                                [/"/, 'string.value', '@pop']
                            ],

                            bracketCounting: [
                                [/\{/, 'delimiter.bracket', '@bracketCounting'],
                                [/\}/, 'delimiter.bracket', '@pop'],
                                { include: 'divkit' }
                            ],

                            divkit: [
                                [/[-?:!|&=><+%/*,]/, 'delimiter.divkit'],
                                [/\(/, 'delimiter.bracket', '@divkit'],
                                [/\)/, 'delimiter.bracket', '@pop'],

                                [/(@digits)[eE]([-+]?(@digits))?/, 'number.divkit'],
                                [/(@digits)\.(@digits)([eE][-+]?(@digits))?/, 'number.divkit'],
                                [/(@digits)(?:\.)?/, 'number.divkit'],

                                [/[a-z_$][\w._$]*/, {
                                    cases: {
                                        '@divkitKeywords': 'keyword.divkit',
                                        '@default': 'identifier.divkit'
                                    }
                                }],

                                [/"/, 'string.invalid'],  // non-teminated string
                                [/'([^'\\]|\\.)*$/, 'string.invalid'],  // non-teminated string
                                [/'/, 'string.divkit', '@divkit_string'],

                                { include: '@whitespace' }
                            ],

                            divkit_string: [
                                [/@\{/, { token: 'delimiter.bracket', next: '@bracketCounting' }],
                                [/[^\\']/, 'string.divkit'],
                                [/@escapes/, 'string.escape'],
                                [/\\./, 'string.escape.invalid'],
                                [/'/, 'string.divkit', '@pop']
                            ],
                        },
                    });
                }
                return res;
            };

            monaco.editor.defineTheme('playground-theme', {
                base: 'vs',
                inherit: true,
                rules: [
                    {
                        token: 'string.escape',
                        foreground: '896e18',
                    },
                    {
                        token: 'string.escape.invalid',
                        foreground: 'f44747',
                    },
                ],
                colors: {},
            });

            const jsonModelUri = monaco.Uri.parse('a://b/divview.json');

            const patchTriggerCondition = (module: any) => {
                const condition = module?.properties?.condition;
                if (condition) {
                    delete condition.$ref;
                    condition.type = 'string';
                }
            };

            const patchDictType = (obj: any) => {
                if (obj && typeof obj === 'object') {
                    if (obj.type === 'dict') {
                        obj.type = 'object';
                    }
                    for (const key in obj) {
                        patchDictType(obj[key]);
                    }
                }
            };

            const schemas = require.context('../../../../schema/', false, /\.json$/);
            let schema = schemas.keys().map((key: string) => {
                const filename = key.replace(/^\.\//, '');
                const module = schemas(key) as any;

                if (filename.includes('div-trigger.json')) {
                    patchTriggerCondition(module);
                }
                patchDictType(module);

                return {
                    uri: 'schema://div2/' + filename,
                    fileMatch: [] as string [],
                    schema: module
                };
            });

            schema.push({
                uri: 'schema://div2/root.json',
                fileMatch: [jsonModelUri.toString()],
                schema: require('../schema/root.json')
            });

            monaco.languages.json.jsonDefaults.setDiagnosticsOptions({
                validate: true,
                schemas: schema,
                allowComments: false
            });

            window.MonacoEnvironment = {
                getWorkerUrl(_moduleId: string, label: string) {
                    if (label === 'json') {
                        return urlPath + '/json.worker.js';
                    }
                    return urlPath + '/editor.worker.js';
                }
            };

            return {
                monaco,
                jsonModelUri,
            };
        });

        return monacoPromose;
    }
</script>

<script lang="ts">
    import { onDestroy, onMount } from 'svelte';
    import { editorMode } from '../data/editorMode';
    import { valueStore } from '../data/valueStore';
    import { divkitColorToCss } from '../utils/colors';

    let editor: monaco.editor.IStandaloneCodeEditor | null = null;
    let node: HTMLElement;
    let colorDecorations: monaco.editor.IEditorDecorationsCollection | null = null;
    let globalColors: string[] = [];

    onMount(async() => {
        const { monaco, jsonModelUri } = await loadMonaco();

        function getModel(): monaco.editor.ITextModel {
            const model = monaco.editor.getModel(jsonModelUri) ||
                monaco.editor.createModel($valueStore, 'json', jsonModelUri);
            model.setValue($valueStore);
            return model;
        }

        valueStore.subscribe(val => {
            if (editor && editor.getValue() !== val) {
                editor.setValue(val);
                recolorJson();
            }
        });

        /* editorMode.subscribe(val => {
            if (val) {
                if (editor) {
                    editor.setModel(getModel(val));
                }
                currentLanguage = val;
                recolorJson();
            }
        }); */

        const opts: monaco.editor.IStandaloneEditorConstructionOptions = {
            theme: 'playground-theme',
            minimap: {
                enabled: false
            },
            automaticLayout: true,
            wordWrap: 'off',
            scrollBeyondLastLine: false,
            model: getModel(),
            bracketPairColorization: {
                enabled: true,
                independentColorPoolPerBracketType: true
            },
            smoothScrolling: true,
            // hand-made
            colorDecorators: false
        };

        editor = monaco.editor.create(node, opts);

        editor.onDidChangeModelContent(() => {
            let val = editor && editor.getModel()?.getValue();
            $valueStore = val || '';

            recolorJson();
        });

        recolorJson();

        function recolorJson(): void {
            if (colorDecorations) {
                colorDecorations.clear();
                colorDecorations = null;
            }
            globalColors = [];
            if ($editorMode !== 'json' || !editor) {
                return;
            }

            const lines = editor.getValue().split('\n');
            const colors: {
                color: string;
                range: monaco.Range;
            }[] = [];
            const COLOR_RE = /("#[0-9a-f]{3,8}")/gi;
            lines.forEach((line, lineIndex) => {
                let match: RegExpExecArray | null;
                COLOR_RE.lastIndex = -1;
                while ((match = COLOR_RE.exec(line))) {
                    colors.push({
                        color: divkitColorToCss(match[0].slice(1, match[0].length - 1)),
                        range: new monaco.Range(lineIndex + 1, COLOR_RE.lastIndex - match[0].length + 1, lineIndex + 1, COLOR_RE.lastIndex - match[0].length + 1)
                    });
                }
            });

            if (colors.length) {
                globalColors = colors.map((it, index) => `.editor-color-decorator_index_${index}{background:${it.color};}`);
                colorDecorations = editor.createDecorationsCollection(colors.map((color, index) =>
                    ({
                        range: color.range,
                        options: {
                            beforeContentClassName: `editor__color-decorator editor-color-decorator_index_${index}`,
                            hoverMessage: {
                                value: 'Color in ARGB format'
                            }
                        }
                    })
                ));
            }
        }
    });

    onDestroy(() => {
        if (editor) {
            editor.dispose();
            editor = null;
        }
    });
</script>

<svelte:head>
    <svelte:element this="style">
        {@html globalColors.join('')}
    </svelte:element>
</svelte:head>

<div class="editor">
    <div class="editor__editor" bind:this={node}></div>
</div>

<style>
    .editor {
        display: flex;
        flex-direction: column;
        height: 100%;
        /* Fix monaco autoLayout, that causes recursive relayout */
        overflow: hidden;
    }

    .editor__editor {
        flex: 1 0 0;
        min-height: 0;
    }

    :global(.editor__color-decorator) {
        border: .1em solid #eee;
        box-sizing: border-box;
        /* cursor: pointer; */
        display: inline-block;
        height: .8em;
        line-height: .8em;
        margin: .1em .2em 0;
        width: .8em;
    }

    @keyframes run-rotate {
        from {
            transform: rotate(0deg);
        }
        to {
            transform: rotate(360deg);
        }
    }
</style>
