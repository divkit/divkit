<script lang="ts" context="module">
    import * as monaco from 'monaco-editor';
    import tsBuilderTypes from '../../node_modules/@divkit/jsonbuilder-internal-test/dist/jsonbuilder.d.ts?inline';

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
        `declare module '@divkit/jsonbuilder' {${tsBuilderTypes}}`,
        '@types/divcard2/index.d.ts'
    );
</script>

<script lang="ts">
    import { getContext, onDestroy, onMount } from 'svelte';
    import { editorMode } from '../data/editorMode';
    import { codeRunStore, valueStore } from '../data/valueStore';
    import PanelHeader from './PanelHeader.svelte';
    import Select from './Select.svelte';
    import { urlPath, serverHostPath } from '../utils/const';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';
    import { runCode as runCodeShortcut } from '../utils/shortcuts';
    import { jsonStore } from '../data/jsonStore';
    import { DEFAULT_JSON_VALUE, DEFAULT_TS_VALUE } from '../data/initialValue';
    import { session } from '../data/session';
    import { show } from './PointingPopup.svelte';
    import type { ShortcutList } from '../utils/useShortcuts';
    import { shortcuts } from '../utils/useShortcuts';

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

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);

    const LANGUAGE_NAMES = {
        json: 'JSON',
        ts: 'TypeScript'
    };

    let editor: monaco.editor.IStandaloneCodeEditor | null = null;
    let node: HTMLElement;
    let currentLanguage = '';
    let isRunning = false;
    let runButton: HTMLElement;

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

    editorMode.subscribe(val => {
        if (val) {
            if (editor) {
                editor.setModel(getModel(val));
            }
            currentLanguage = val;
        }
    });

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

    onMount(() => {
        const opts: monaco.editor.IStandaloneEditorConstructionOptions = {
            theme: 'vs',
            minimap: {
                enabled: false
            },
            automaticLayout: true,
            wordWrap: 'on',
            scrollBeyondLastLine: false,
            model: getModel($editorMode)
        };

        editor = monaco.editor.create(node, opts);

        editor.onDidChangeModelContent(() => {
            let val = editor && editor.getModel()?.getValue();
            $valueStore = val || '';
        });
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

<div class="editor" use:shortcuts={SHORTCUTS}>
    <PanelHeader>
        <Select
            bind:value={currentLanguage}
            items={['json', 'ts'].map(value => ({value, name: LANGUAGE_NAMES[value]}))}
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

    @keyframes run-rotate {
        from {
            transform: rotate(0deg);
        }
        to {
            transform: rotate(360deg);
        }
    }
</style>
