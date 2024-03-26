<script lang="ts">
    import { getContext, onDestroy, onMount } from 'svelte';
    import type * as monaco from 'monaco-editor';
    import type { DivProEditorInstance, EditorOptions, Layout, LayoutItem } from '@yandex-portal/divkit-editor';
    import { loadMonaco } from './Editor.svelte';
    import { valueStore } from '../data/valueStore';
    import { initPromise } from '../data/sessionController';
    import ToolbarItem from './ToolbarItem.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../data/languageContext';

    const {l10n, lang} = getContext<LanguageContext>(LANGUAGE_CTX);

    let root: HTMLElement;
    let instance: DivProEditorInstance;
    let alive = false;
    let selectedPanel = 'components';

    const TOOLBAR_ITEMS = [
        'components',
        'palette',
        'sources',
    ] as const;
    $: TOOLBAR_TEXTS = {
        components: $l10n('designComponents'),
        palette: $l10n('designPalette'),
        sources: $l10n('designVariables'),
    };

    function getEditorLayoutByPanel(panel: string): Layout {
        let leftList: LayoutItem[];

        if (panel === 'palette') {
            leftList = ['palette'];
        } else if (panel === 'sources') {
            leftList = ['custom-variables'];
        } else if (panel === 'tanker') {
            leftList = ['tanker-overview'];
        } else {
            leftList = ['new-component', 'component-tree'];
        }

        return [
            {
                items: leftList,
                minWidth: 400,
            },
            {
                items: ['preview'],
                weight: 3,
            },
            {
                items: ['component-props:code'],
                minWidth: 360,
            },
        ];
    }

    function onToolbarClick(item: string): void {
        selectedPanel = item;
        if (instance) {
            instance.setLayout(getEditorLayoutByPanel(selectedPanel));
        }
    }

    onMount(() => {
        alive = true;

        Promise.all([
            initPromise,
            import('../data/editorModule'),
            loadMonaco()
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
        ]).then(([_, {DivProEditor}, { monaco, jsonModelUri }]) => {
            if (!alive) {
                return;
            }

            instance = DivProEditor.init({
                renderTo: root,
                value: $valueStore,
                theme: 'light',
                locale: $lang,
                sources: [],
                layout: getEditorLayoutByPanel(selectedPanel),
                paletteEnabled: true,
                api: {
                    onChange() {
                        valueStore.set(instance.getValue());
                    },
                    editorFabric(options: EditorOptions) {
                        const model = monaco.editor.getModel(jsonModelUri) || monaco.editor.createModel(options.value, 'json', jsonModelUri);
                        const opts: monaco.editor.IStandaloneEditorConstructionOptions = {
                            theme: 'vs',
                            minimap: {
                                enabled: false
                            },
                            automaticLayout: true,
                            wordWrap: 'on',
                            model,
                            lineNumbers: 'off',
                            bracketPairColorization: {
                                enabled: true,
                                independentColorPoolPerBracketType: true
                            },
                            smoothScrolling: true
                        };

                        const editor = monaco.editor.create(options.node, opts);
                        editor.setValue(options.value);
                        let editorDecorations: monaco.editor.IEditorDecorationsCollection | null = null;

                        editor.onDidChangeModelContent(() => {
                            let val = editor && editor.getModel()?.getValue();
                            options.onChange(val || '');
                        });

                        editor.onMouseMove(function (e) {
                            const pos = e.target.position;
                            if (!pos || !pos.lineNumber) {
                                return;
                            }

                            options.onOver(model.getOffsetAt(pos));
                        });

                        editor.onMouseLeave(() => {
                            options.onOver(null);
                        });

                        editor.onMouseDown((e) => {
                            const pos = e.target.position;
                            if (!pos || !pos.lineNumber) {
                                return;
                            }

                            options.onClick(model.getOffsetAt(pos));
                        });

                        return {
                            isFocused() {
                                return editor.hasTextFocus();
                            },
                            setValue(value) {
                                if (!editor.hasTextFocus()) {
                                    editor.setValue(value);
                                }
                            },
                            setTheme() {
                                // not realized
                            },
                            setReadOnly(/* readOnly */) {
                                // not realized
                            },
                            revealLoc(loc) {
                                editor.revealPositionNearTop({ lineNumber: loc.line, column: loc.column });
                            },
                            decorateRanges(typedRanges) {
                                if (editorDecorations) {
                                    editorDecorations.clear();
                                    editorDecorations = null;
                                }
                                if (typedRanges) {
                                    editorDecorations = editor.createDecorationsCollection(typedRanges.map(typedRange => {
                                        return {
                                            range: new monaco.Range(
                                                typedRange.range.start.line,
                                                typedRange.range.start.column,
                                                typedRange.range.end.line,
                                                typedRange.range.end.column
                                            ),
                                            options: {
                                                blockClassName: typedRange.type === 'highlight' ? 'design__code-highlight' : 'design__code-selection'
                                            }
                                        };
                                    }));
                                }
                            },
                            destroy() {
                                editor.dispose();
                            }
                        }
                    }
                }
            });
        });
    });

    onDestroy(() => {
        alive = false;
        instance?.destroy();
    });
</script>

<div class="design">
    <div class="design__sidebar">
        {#each TOOLBAR_ITEMS as item}
            <ToolbarItem
                icon={item}
                selected={selectedPanel === item}
                text={TOOLBAR_TEXTS[item]}
                on:click={() => onToolbarClick(item)}
            />
        {/each}
    </div>
    <div class="design__editor" bind:this={root}></div>
</div>

<style>
    .design {
        display: flex;
        flex: 1 1 auto;
        min-height: 0;
    }

    .design__sidebar {
        display: flex;
        flex: 0 0 auto;
        flex-direction: column;
        width: 56px;
        padding-top: 10px;
        background: var(--bg-secondary);
    }

    .design__editor {
        flex: 1 1 auto;
    }

    :global(.design__code-highlight) {
        background: rgba(0, 0, 0, 0.05);
    }

    :global(.design__code-selection) {
        background: rgba(0, 0, 0, 0.1);
    }
</style>
