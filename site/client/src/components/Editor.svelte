<script lang="ts" context="module">
    import { urlPath } from '../utils/const';
    import type * as monaco from 'monaco-editor';
    import tsBuilderTypes from '../../artifacts/jsonbuilder.d.ts?inline';

    let monacoPromose: Promise<{
        monaco: typeof import('monaco-editor');
        jsonModelUri: monaco.Uri;
        tsModelUri: monaco.Uri;
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
                                [/\[/, 'delimiter.bracket', '@obj'],
                                { include: 'common' }
                            ],

                            obj: [
                                [/\}/, 'delimiter.bracket', '@pop'],
                                [/\]/, 'delimiter.bracket', '@pop'],
                                [/"([^"\\]|\\.)*$/, 'string.invalid'],  // non-teminated string
                                [/'([^'\\]|\\.)*$/, 'string.invalid'],  // non-teminated string
                                [/"/, 'string.key', '@string_double_key'],
                                [/:/, 'delimiter', '@obj_value'],
                                { include: 'common' }
                            ],

                            string_double_key: [
                                [/[^\\"]+/, 'string.key'],
                                [/@escapes/, 'string.escape'],
                                [/\\./, 'string.escape.invalid'],
                                [/"/, 'string.key', '@pop']
                            ],

                            obj_value: [
                                [/[{]/, 'delimiter.bracket', '@obj'],
                                [/[,]/, 'delimiter', '@obj'],
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
                                [/'/, 'string.value', '@divkit_string'],

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
            const tsModelUri = monaco.Uri.parse('file:///main.tsx');

            const schemas = require.context('../../../../schema/', false, /\.json$/);
            let schema = schemas.keys().map((key: string) => {
                const filename = key.replace(/^\.\//, '');

                return {
                    uri: 'schema://div2/' + filename,
                    fileMatch: [] as string [],
                    schema: schemas(key)
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

            // validation settings
            monaco.languages.typescript.javascriptDefaults.setDiagnosticsOptions({
                noSemanticValidation: true,
                noSyntaxValidation: false
            });

            // compiler options
            monaco.languages.typescript.javascriptDefaults.setCompilerOptions({
                target: monaco.languages.typescript.ScriptTarget.ES2015,
                allowNonTsExtensions: true
            });

            monaco.languages.typescript.typescriptDefaults.addExtraLib(
                `declare module '@divkitframework/jsonbuilder' {${tsBuilderTypes}}`,
                '@types/divcard2/index.d.ts'
            );

            window.MonacoEnvironment = {
                getWorkerUrl(_moduleId: string, label: string) {
                    if (label === 'json') {
                        return urlPath + '/json.worker.js';
                    } else if (label === 'typescript') {
                        return urlPath + '/typescript.worker.js';
                    }
                    return urlPath + '/editor.worker.js';
                }
            };

            return {
                monaco,
                jsonModelUri,
                tsModelUri,
            };
        });

        return monacoPromose;
    }
</script>

<script lang="ts">
    import { getContext, onDestroy, onMount } from 'svelte';
    import { editorMode } from '../data/editorMode';
    import { codeRunStore, valueStore } from '../data/valueStore';
    import PanelHeader from './PanelHeader.svelte';
    import Select from './Select.svelte';
    import { serverHostPath } from '../utils/const';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';
    import { runCode as runCodeShortcut } from '../utils/shortcuts';
    import { jsonStore } from '../data/jsonStore';
    import { DEFAULT_JSON_VALUE, DEFAULT_TS_VALUE } from '../data/initialValue';
    import { session } from '../data/session';
    import { show } from './PointingPopup.svelte';
    import type { ShortcutList } from '../utils/useShortcuts';
    import { shortcuts } from '../utils/useShortcuts';
    import { divkitColorToCss } from '../utils/colors';

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);

    type Language = 'json' | 'ts';

    const LANGUAGE_NAMES: Record<Language, string> = {
        json: 'JSON',
        ts: 'TypeScript'
    };

    const LANGUAGE_KEYS: Language[] = ['json', 'ts'];

    let editor: monaco.editor.IStandaloneCodeEditor | null = null;
    let node: HTMLElement;
    let currentLanguage: Language = 'json';
    let isRunning = false;
    let runButton: HTMLElement;
    let colorDecorations: monaco.editor.IEditorDecorationsCollection | null = null;
    let globalColors: string[] = [];

    function onLanguageChange(): void {
        if (currentLanguage === $editorMode) {
            return;
        }

        if (currentLanguage === 'json') {
            if (confirm($l10n('toJsonWarning'))) {
                let newVal = JSON.stringify($jsonStore, null, 4);
                valueStore.set(newVal === '{}' ? DEFAULT_JSON_VALUE : newVal);
                editorMode.set(currentLanguage);
            } else if ($editorMode) {
                currentLanguage = $editorMode;
            }
        } else if (currentLanguage === 'ts') {
            if (confirm($l10n('toTsWarning'))) {
                valueStore.set(DEFAULT_TS_VALUE);
                editorMode.set(currentLanguage);
                codeRunStore.set(false);
            } else if ($editorMode) {
                currentLanguage = $editorMode;
            }
        }
    }

    function runCode(): void {
        if (isRunning) {
            return;
        }

        isRunning = true;

        fetch(`//${serverHostPath}api/runTs`, {
            body: JSON.stringify({
                code: $valueStore,
                uuid: $session.uuid,
                writeKey: $session.writeKey
            }),
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(res => res.json())
            .then(json => {
                if (json.ok) {
                    try {
                        jsonStore.set(JSON.parse(json.result));
                        codeRunStore.set(true);
                    } catch (err) {
                        show(runButton, String(err));
                    }
                } else {
                    throw new Error(String(json.error));
                }
            })
            .catch(err => {
                show(runButton, String(err));
            })
            .finally(() => {
                isRunning = false;
            });
    }

    onMount(async() => {
        const { monaco, jsonModelUri, tsModelUri } = await loadMonaco();

        function getModel(type: 'ts' | 'json' | null): monaco.editor.ITextModel {
            if (type === 'json') {
                const model = monaco.editor.getModel(jsonModelUri) ||
                    monaco.editor.createModel($valueStore, 'json', jsonModelUri);
                model.setValue($valueStore);
                return model;
            } else {
                const model = monaco.editor.getModel(tsModelUri) ||
                    monaco.editor.createModel($valueStore, 'typescript', tsModelUri);
                model.setValue($valueStore);
                return model;
            }
        }

        valueStore.subscribe(val => {
            if (editor && editor.getValue() !== val) {
                editor.setValue(val);
                recolorJson();
            }
        });

        editorMode.subscribe(val => {
            if (val) {
                if (editor) {
                    editor.setModel(getModel(val));
                }
                currentLanguage = val;
                recolorJson();
            }
        });

        const opts: monaco.editor.IStandaloneEditorConstructionOptions = {
            theme: 'playground-theme',
            minimap: {
                enabled: false
            },
            automaticLayout: true,
            wordWrap: 'off',
            scrollBeyondLastLine: false,
            model: getModel($editorMode),
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

    const SHORTCUTS: ShortcutList = [
        [runCodeShortcut, runCode]
    ];
</script>

<svelte:head>
    <svelte:element this="style">
        {@html globalColors.join('')}
    </svelte:element>
</svelte:head>

<div class="editor" use:shortcuts={SHORTCUTS}>
    <PanelHeader>
        <Select
            bind:value={currentLanguage}
            items={LANGUAGE_KEYS.map(value => ({value, name: LANGUAGE_NAMES[value]}))}
            toggledClass="editor__toggled-select" slot="left"
            on:change={onLanguageChange}
        >
            <div class="editor__select">
                {LANGUAGE_NAMES[currentLanguage]}
                <svg xmlns="http://www.w3.org/2000/svg" class="editor__select-icon" viewBox="0 0 23 23"><path fill="currentColor" d="M3.96 6.648c-.434 0-1.033.717-.499 1.439l7.663 7.888c.33.371.81.396 1.15.025l6.934-8.103c.275-.318.149-1.265-.575-1.28z"/></svg>
            </div>
        </Select>
        <div slot="right">
            {#if currentLanguage === 'ts'}
                <button
                    class="editor__run"
                    class:editor__run_progress={isRunning}
                    on:click={runCode}
                    bind:this={runButton}
                    title={$l10n('runTitle').replace('%s', runCodeShortcut.toString())}
                >
                    {#if isRunning}
                        <svg
                            class="editor__run-icon"
                            xmlns="http://www.w3.org/2000/svg"
                            width="16"
                            height="16"
                            viewBox="0 0 16 16"
                        >
                            <path fill="none" stroke-width="1.7" stroke-linecap="round" stroke="currentColor" d="M13 8a5 5 0 0 1-5 5M3 8a5 5 0 0 1 5-5"></path>
                        </svg>
                    {:else}
                        <svg
                            class="editor__run-icon"
                            xmlns="http://www.w3.org/2000/svg"
                            width="24"
                            height="24"
                            viewBox="0 0 24 24"
                        >
                            <path fill="currentColor" stroke="currentColor" stroke-width="1" stroke-linejoin="round" d="M4 23V1l19 11z"></path>
                        </svg>
                    {/if}
                    {$l10n('run')}
                </button>
            {/if}
        </div>
    </PanelHeader>
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

    .editor__select {
        position: relative;
        display: flex;
        align-items: center;
        height: 32px;
        padding: 0 32px 0 12px;
        appearance: none;
        font: inherit;
        line-height: inherit;
        background: none;
        /*background: no-repeat 50% 50% #346e6d url(../assets/selectIcon.svg);*/
        background-position: calc(100% - 6px) 50%;
        transition: .1s ease-in-out;
        transition-property: color, background-color;
        border: 1px solid var(--accent0);
        border-radius: 1024px;
        color: var(--accent0);
        cursor: pointer;
        user-select: none;
    }

    .editor__select-icon {
        position: absolute;
        top: 0;
        right: 7px;
        bottom: 0;
        width: 18px;
        height: 18px;
        margin: auto;
        color: var(--accent0);
        transition: color .1s ease-in-out;
    }

    .editor__select:hover,
    :global(.editor__toggled-select) .editor__select {
        background-color: var(--accent0);
        color: var(--accent0-text);
    }

    .editor__select:hover .editor__select-icon,
    :global(.editor__toggled-select) .editor__select-icon {
        color: var(--accent0-text);
    }

    .editor__run {
        margin-left: auto;
    }

    .editor__run {
        display: flex;
        align-items: center;
        height: 32px;
        padding: 0 12px;
        font: inherit;
        line-height: inherit;
        appearance: none;
        border: 1px solid transparent;
        border-radius: 1024px;
        cursor: pointer;
        background: var(--accent1);
        transition: .1s ease-in-out;
        transition-property: background-color, border, color;
        color: var(--accent1-text);
    }

    .editor__run:hover {
        background-color: var(--bg-primary);
        border-color: var(--accent1);
        color: var(--accent1);
    }

    .editor__run-icon {
        width: 16px;
        height: 16px;
        margin-right: 8px;
    }

    .editor__run_progress .editor__run-icon {
        animation: run-rotate 1s infinite;
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
