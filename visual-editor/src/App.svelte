<script lang="ts">
    import { setContext } from 'svelte';
    import { derived, get, writable } from 'svelte/store';
    import SplitView from './lib/components/SplitView.svelte';
    import type { LanguageContext } from './lib/ctx/languageContext';
    import { LANGUAGE_CTX } from './lib/ctx/languageContext';
    import translations from './auto/lang.json';
    import ContextMenu from './lib/components/ContextMenu.svelte';
    import type { CardLocale, EditorInstance, EditorOptions, FileLimits, FontFaceDesc, GetTranslationKey, GetTranslationSuggest, Layout, Locale } from './lib';
    import LayoutColumn from './lib/components/LayoutColumn.svelte';
    import { APP_CTX, type AppContext, type ContextMenuApi, type RendererApi, type ShowErrors } from './lib/ctx/appContext';
    import { editorFabric as editorFabricInternal } from './lib/data/editorWrapper';
    import CustomTooltip from './lib/components/CustomTooltip.svelte';
    import Background2Dialog from './lib/components/prop-dialog/Background2Dialog.svelte';
    import Color2Dialog from './lib/components/prop-dialog/Color2Dialog.svelte';
    import File2Dialog from './lib/components/prop-dialog/File2Dialog.svelte';
    import Link2Dialog from './lib/components/prop-dialog/Link2Dialog.svelte';
    import Actions2Dialog from './lib/components/prop-dialog/Actions2Dialog.svelte';
    import TextAlign2Dialog from './lib/components/prop-dialog/TextAlign2Dialog.svelte';
    import InplaceEditorDialog from './lib/components/InplaceEditorDialog.svelte';
    import Tanker2Dialog from './lib/components/prop-dialog/Tanker2Dialog.svelte';
    import Expression2Dialog from './lib/components/prop-dialog/Expression2Dialog.svelte';
    import { onGlobalKeydown } from './lib/utils/keybinder/keybinder';
    import { loadFileAsBase64 } from './lib/utils/loadFileAsBase64';
    import VideoSourcesDialog from './lib/components/prop-dialog/VideoSourcesDialog.svelte';
    import type { State } from './lib/data/state';
    import SelectOptionsDialog from './lib/components/prop-dialog/SelectOptionsDialog.svelte';

    export let state: State;

    export let layout: Layout = [
        {
            items: ['new-component', 'component-tree']
        },
        {
            items: ['preview'],
            weight: 3
        },
        {
            items: ['component-props:code']
        }
    ];
    export let locale: Locale = 'en';

    export let shadowRoot: ShadowRoot | undefined = undefined;

    export let actionLogUrlVariable = '';

    export let cardLocales: CardLocale[] = [];

    export let previewWarnFileLimit = 10000;
    export let previewErrorFileLimit = Infinity;
    export let warnFileLimit = Infinity;
    export let errorFileLimit = Infinity;

    export let fileLimits: FileLimits | undefined = undefined;

    export let rootConfigurable = false;

    export let customFontFaces: FontFaceDesc[] = [];

    export let directionSelector = false;

    export let perThemeProps = get(state.paletteEnabled);

    export let uploadFile: (file: File) => Promise<string> = loadFileAsBase64;

    export let editorFabric: (opts: EditorOptions) => EditorInstance = editorFabricInternal;

    export let getTranslationSuggest: GetTranslationSuggest | undefined = undefined;

    export let getTranslationKey: GetTranslationKey | undefined = undefined;

    export function setLayout(newLayout: Layout): void {
        layout = newLayout;
    }

    export function setLocale(newLocale: Locale): void {
        lang.set(newLocale);
    }

    const { themeStore } = state;

    const l10nGetter = (lang: string, key: string) => {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        const translation = (translations as any)[lang]?.[key];

        if (!translation) {
            // todo expose error
            console.error(`Missing translation for key "${key}"`);
        }

        return translation || '';
    };

    let lang = writable(locale);
    const l10n = derived(lang, lang => {
        return (key: string, overrideLang?: string) => {
            return l10nGetter(overrideLang || lang, key);
        };
    });

    const l10nString = derived(lang, lang => {
        return (key: string, overrideLang?: string) => {
            const val = l10nGetter(overrideLang || lang, key);

            if (typeof val !== 'string') {
                // todo expose error
                console.error(`Missing translation for key "${key}"`);
                return '';
            }

            return val;
        };
    });

    let rendererApi: RendererApi = {
        containerProps() {
            return {
                width: 0,
                height: 0
            };
        },
        evalJson(_json) {
            return {};
        },
        focus() {
        },
        selectedElemProps() {
            return null;
        },
    };

    let showErrors: ShowErrors = () => {};

    let contextMenu: ContextMenuApi = {
        toggle() {},
        hide() {},
    };

    let inplaceEditor: InplaceEditorDialog;
    let actions2Dialog: Actions2Dialog;
    let background2Dialog: Background2Dialog;
    let color2Dialog: Color2Dialog;
    let expression2Dialog: Expression2Dialog;
    let file2Dialog: File2Dialog;
    let link2Dialog: Link2Dialog;
    let tanker2Dialog: Tanker2Dialog;
    let textAlign2Dialog: TextAlign2Dialog;
    let videoSourcesDialog: VideoSourcesDialog;
    let selectOptionsDialog: SelectOptionsDialog;

    setContext<LanguageContext>(LANGUAGE_CTX, {
        lang,
        getLanguage(): string {
            return get(lang);
        },
        l10n,
        l10nString,
    });

    setContext<AppContext>(APP_CTX, {
        state,
        uploadFile,
        editorFabric,
        shadowRoot,
        ownerDocument: shadowRoot || document,
        getSelection() {
            // Incorrect TypeScript DOM typings
            if (shadowRoot && 'getSelection' in shadowRoot && typeof shadowRoot.getSelection === 'function') {
                return shadowRoot.getSelection();
            }
            return window.getSelection();
        },
        actionLogUrlVariable,
        cardLocales,
        getTranslationSuggest,
        getTranslationKey,
        previewWarnFileLimit,
        previewErrorFileLimit,
        warnFileLimit,
        errorFileLimit,
        fileLimits,
        rootConfigurable,
        customFontFaces,
        directionSelector,
        perThemeProps,

        rendererApi() {
            return rendererApi;
        },
        setRendererApi(api) {
            rendererApi = api;
        },

        showErrors: (...args) => {
            showErrors(...args);
        },
        setShowErrors(fn) {
            showErrors = fn;
        },

        contextMenu() {
            return contextMenu;
        },

        setContextMenuApi(api) {
            contextMenu = api;
        },

        inplaceEditor() {
            return {
                show(props) {
                    inplaceEditor.show(props);
                },
                hide() {
                    inplaceEditor.hide();
                },
                setPosition(left, top) {
                    inplaceEditor.setPosition(left, top);
                }
            };
        },

        actions2Dialog() {
            return {
                show(props) {
                    actions2Dialog.show(props);
                },
                hide() {
                    actions2Dialog.hide();
                }
            };
        },

        background2Dialog() {
            return {
                show(props) {
                    background2Dialog.show(props);
                },
                hide() {
                    background2Dialog.hide();
                }
            };
        },

        color2Dialog() {
            return {
                show(props) {
                    color2Dialog.show(props);
                },
                hide() {
                    color2Dialog.hide();
                }
            };
        },

        expression2Dialog() {
            return {
                show(props) {
                    expression2Dialog.show(props);
                },
                hide() {
                    expression2Dialog.hide();
                },
            };
        },

        file2Dialog() {
            return {
                show(props) {
                    file2Dialog.show(props);
                },
                hide() {
                    file2Dialog.hide();
                }
            };
        },

        link2Dialog() {
            return {
                show(props) {
                    link2Dialog.show(props);
                },
                hide() {
                    link2Dialog.hide();
                }
            };
        },

        tanker2Dialog() {
            return {
                show(props) {
                    tanker2Dialog.show(props);
                },
                hide() {
                    tanker2Dialog.hide();
                }
            };
        },

        textAlign2Dialog() {
            return {
                show(props) {
                    textAlign2Dialog.show(props);
                },
                hide() {
                    textAlign2Dialog.hide();
                }
            };
        },

        videoSource2Dialog() {
            return {
                show(props) {
                    videoSourcesDialog.show(props);
                },
                hide() {
                    videoSourcesDialog.hide();
                }
            };
        },

        selectOptionsDialog() {
            return {
                show(props) {
                    selectOptionsDialog.show(props);
                },
                hide() {
                    selectOptionsDialog.hide();
                }
            };
        },
    });

    $: components = layout.map(column => {
        return {
            component: LayoutColumn,
            weight: column.weight || 1,
            minSize: column.minWidth,
            key: column.items.join(','),
            props: {
                items: column.items
            }
        };
    });

    let currentTooltipOwner: HTMLElement | null = null;

    function onMouseOver(event: MouseEvent): void {
        const target = event.target;
        if (!(target instanceof HTMLElement)) {
            return;
        }
        const owner = target.closest('[data-custom-tooltip]');
        if (owner && owner instanceof HTMLElement && owner !== currentTooltipOwner) {
            currentTooltipOwner = owner;
        }
    }

    function onMouseOut(event: MouseEvent): void {
        const target = event.target;

        if (!(target instanceof Node)) {
            return;
        }

        if (currentTooltipOwner && !currentTooltipOwner.contains(target)) {
            currentTooltipOwner = null;
        }
    }
</script>

<!-- svelte-ignore a11y-mouse-events-have-key-events -->
<!-- svelte-ignore a11y-no-static-element-interactions -->
<div
    class="app"
    class:app_dark={$themeStore === 'dark'}
    on:mouseover={onMouseOver}
    on:mouseout={onMouseOut}
    on:keydown={onGlobalKeydown}
>
    <main class="main">
        <SplitView {components} />
    </main>

    <ContextMenu />
    <Background2Dialog bind:this={background2Dialog} />
    <Color2Dialog bind:this={color2Dialog} />
    <File2Dialog bind:this={file2Dialog} />
    <Link2Dialog bind:this={link2Dialog} />
    <Actions2Dialog bind:this={actions2Dialog} />
    <TextAlign2Dialog bind:this={textAlign2Dialog} />
    <Tanker2Dialog bind:this={tanker2Dialog} />
    <Expression2Dialog bind:this={expression2Dialog} />
    <InplaceEditorDialog bind:this={inplaceEditor} />
    <VideoSourcesDialog bind:this={videoSourcesDialog} />
    <SelectOptionsDialog bind:this={selectOptionsDialog} />

    <CustomTooltip owner={currentTooltipOwner} />
</div>

<style>
    .app {
        position: relative;
        z-index: 0;
        width: 100%;
        height: 100%;
    }

    .main {
        display: flex;
        flex-direction: column;
        flex: 1 1 auto;
        width: 100%;
        height: 100%;
        color: var(--text-primary);
        background-color: var(--background-primary);
    }

    .app {
        color-scheme: light;

        --monospace-font: Droid Sans Mono, "monospace", monospace,
            Droid Sans Fallback;
    }

    .app_dark {
        color-scheme: dark;
    }

    .app {
        --bg-primary: #fff;
        --bg-secondary: #eee; /* #d8d8d8; */ /*rgba(0, 0, 0, .15);*/
        --bg-tertiary: #ccc;

        --separator: #ccc;
        --separator-secondary: #aaa;

        --icon-filter: invert(1);

        --background-overflow: #F7F7F8;
        --background-overflow-transparent: rgba(246, 246, 247, .85);
        --background-primary: #FFFFFF;
        --background-secondary: #FFFFFF;
        --background-tertiary: #FFFFFF;

        --fill-opaque-minus-1: #fff;
        --fill-transparent-minus-1: rgba(255, 255, 255, 0.12);
        --fill-transparent-05: rgba(0, 0, 0, 0.03);
        --fill-transparent-1: rgba(0, 0, 0, 0.06);
        --fill-transparent-2: rgba(0, 0, 0, 0.09);
        --fill-transparent-25: rgba(0, 0, 0, 0.11);
        --fill-transparent-3: rgba(0, 0, 0, 0.12);
        --fill-transparent-4: rgba(0, 0, 0, 0.18);
        --fill-opaque-1: #f0f0f0;

        --fill-accent-1: rgba(89, 89, 232, 0.08);
        --fill-accent-2: rgba(89, 89, 232, 0.13);
        --fill-accent-3: rgba(89, 89, 232, 0.25);
        --fill-accent-4: rgba(89, 89, 232, 0.31);

        --applied-splitter: #CCCCCC;
        --applied-overlay: rgba(0, 0, 0, 0.12);

        --text-primary: #1D222B;
        --text-secondary: #70747A;
        --text-tertiary: #B0B0B0;

        --accent-purple: #5959E8;
        --accent-purple-sub: #3D3DE3;
        --accent-purple-hover: #545BD6;
        --accent-purple-active: #4E54C9;
        --accent-orange: #FF9000;
        --accent-red: #FC3F6C;
        --accent-red-hover: #e02b55;

        --fill-orange-1: rgba(255, 144, 0, 0.11);
        --fill-orange-2: rgba(255, 144, 0, 0.22);

        --fill-red-1: rgba(252, 63, 108, 0.11);
        --fill-red-2: rgba(252, 63, 108, 0.22);

        --shadow-16: 0px 2px 8px rgba(0, 0, 0, 0.14);
        --shadow-32: 0px 8px 16px rgba(0, 0, 0, 0.14);

        --icons-gray: #A7AAB1;
    }

    .app_dark {
        --bg-primary: #000;
        --bg-secondary: #333333; /*rgba(255, 255, 255, .2);*/
        --bg-tertiary: #444;

        --separator: #777;
        --separator-secondary: #888;

        --icon-filter: none;

        --background-overflow: #0F0F0F;
        --background-overflow-transparent: rgba(15, 15, 15, .85);
        --background-primary: #1C1C1C;
        --background-secondary: #262626;
        --background-tertiary: #363636;

        --fill-opaque-minus-1: #191919;
        --fill-transparent-minus-1: rgba(0, 0, 0, 0.12);
        --fill-transparent-05: rgba(255, 255, 255, 0.05);
        --fill-transparent-1: rgba(255, 255, 255, 0.1);
        --fill-transparent-2: rgba(255, 255, 255, 0.15);
        --fill-transparent-25: rgba(255, 255, 255, 0.18);
        --fill-transparent-3: rgba(255, 255, 255, 0.2);
        --fill-transparent-4: rgba(255, 255, 255, 0.36);

        --fill-accent-1: rgba(89, 89, 232, 0.13);
        --fill-accent-2: rgba(89, 89, 232, 0.32);
        --fill-accent-3: rgba(89, 89, 232, 0.52);
        --fill-accent-4: rgba(89, 89, 232, 0.72);
        --fill-opaque-1: #333333;

        --applied-splitter: #626262;
        --applied-overlay: rgba(0, 0, 0, 0.4);

        --text-primary: #FFFFFF;
        --text-secondary: rgba(255, 255, 255, 0.5);
        --text-tertiary: rgba(255, 255, 255, 0.23);

        --accent-purple: #595DFF;
        --accent-purple-sub: #7A7DFF;
        --accent-purple-hover: #6A6DFF;
        --accent-purple-active: #7A7DFF;
        --accent-orange: #FF9000;
        --accent-red: #FC3F6C;
        --accent-red-hover: #ff6186;

        --fill-orange-1: rgba(255, 144, 0, 0.2);
        --fill-orange-2: rgba(255, 144, 0, 0.3);

        --fill-red-1: rgba(252, 63, 108, .2);
        --fill-red-2: rgba(252, 63, 108, .3);

        --shadow-16: 0px 4px 8px rgba(0, 0, 0, 0.26);
        --shadow-32: 0px 8px 16px rgba(0, 0, 0, 0.26);

        --icons-gray: #656565;
    }

    /* ::backgrop doesn't inherit vars */
    .app :global(dialog::backdrop) {
        /* applied-overlay */
        --dialog-backdrop: rgba(0, 0, 0, 0.12);
    }

    .app_dark :global(dialog::backdrop) {
        /* applied-overlay */
        --dialog-backdrop: rgba(0, 0, 0, 0.4);
    }

    .app :global(::-webkit-scrollbar) {
        display: block;
        width: 6px;
        height: 6px;
    }

    .app :global(::-webkit-scrollbar-button) {
        display: none;
    }

    .app :global(::-webkit-scrollbar-track) {
        background-color: transparent;
    }

    .app :global(::-webkit-scrollbar-track-piece) {
        background-color: transparent;
    }

    .app :global(::-webkit-scrollbar-thumb) {
        background-color: var(--fill-transparent-2);
    }

    .app :global(:hover::-webkit-scrollbar-thumb) {
        background-color: var(--fill-transparent-25);
    }

    .app :global(::-webkit-scrollbar-thumb:hover) {
        background-color: var(--fill-transparent-3);
    }

    .app :global(::-webkit-scrollbar-thumb:active) {
        background-color: var(--fill-transparent-4);
    }
</style>
