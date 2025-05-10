import { get, type Unsubscriber } from 'svelte/store';
import App from './App.svelte';
import type { Loc } from './lib/utils/stringifyWithLoc';
import type { TypedRange } from './lib/data/editor';
import { type PaletteItem } from './lib/data/palette';
import { type JsonVariable } from './lib/data/customVariables';
import { State } from './lib/data/state';

export type Theme = 'light' | 'dark';

export type LayoutItem = 'new-component' | 'component-tree' | 'palette' |
    'preview' | 'component-props' | 'code' | 'component-props:code' |
    'tanker-overview' | 'sources-overview' | 'custom-variables' |
    'timers';

export type LayoutColumn = {
    items: LayoutItem[];
    minWidth?: number;
    weight?: number;
};
export type Layout = LayoutColumn[];

export type Locale = 'ru' | 'en';

export interface ActionArgBase {
    type: string;
    name: string;
    text: Record<string, string>;
}

export interface ActionArgString extends ActionArgBase {
    type: 'string';
}

export type ActionArg = ActionArgString;

export interface ActionDesc {
    baseUrl: string;
    text: Record<string, string>;
    args?: ActionArg[];
}

export interface FontFaceDesc {
    value: string;
    text: Record<string, string>;
    cssValue: string;
}

export type TankerMeta = Record<string, Record<string, string>>;

export interface Meta {
    tanker?: TankerMeta;
}

export interface Card {
    json: string;
    meta?: Meta;
}

export interface CardLocale {
    id: string;
    text: Record<string, string>;
}

export interface EditorOptions {
    node: HTMLElement;
    shadowRoot?: ShadowRoot;
    value: string;
    theme: 'light' | 'dark';
    readOnly?: boolean;
    onChange(value: string): void;
    onOver(offset: number | null): void;
    onClick(offset: number): void;
}

export interface EditorInstance {
    setValue(value: string): void;
    setTheme(theme: 'light' | 'dark'): void;
    setReadOnly(readOnly: boolean): void;
    revealLoc(loc: Loc): void;
    decorateRanges(typedRanges: TypedRange[]): void;
    isFocused(): boolean;
    destroy(): void;
}

export interface TranslationVariant {
    key: string;
    text?: string;
}

export type GetTranslationSuggest = (query: string, locale: string) => Promise<TranslationVariant[]>;

export type GetTranslationKey = (key: string) => Promise<Record<string, string> | undefined>;

export interface DivProEditorApi {
    uploadFile?(file: File): Promise<string>;
    editorFabric?(opts: EditorOptions): EditorInstance;
    onChange?(): void;
    getTranslationSuggest?: GetTranslationSuggest;
    getTranslationKey?: GetTranslationKey;
}

export interface Source {
    key: string;
    url: string;
    example: object;
}

export interface FileLimit {
    warn?: number;
    error?: number;
}

export interface FileLimits {
    preview?: FileLimit;
    image?: FileLimit;
    lottie?: FileLimit;
    video?: FileLimit;
    upload?: FileLimit;
}

export interface DivProEditorOptions {
    renderTo: HTMLElement;
    shadowRoot?: ShadowRoot;
    value?: string;
    card?: Card;
    theme?: Theme;
    layout?: Layout;
    locale?: Locale;
    api?: DivProEditorApi;
    actionLogUrlVariable?: string;
    readOnly?: boolean;
    customActions?: ActionDesc[];
    paletteEnabled?: boolean;
    cardLocales?: CardLocale[];
    sources?: Source[];
    /* @deprecated */
    previewWarnFileLimit?: number;
    /* @deprecated */
    previewErrorFileLimit?: number;
    /* @deprecated */
    warnFileLimit?: number;
    /* @deprecated */
    errorFileLimit?: number;
    fileLimits?: FileLimits;
    rootConfigurable?: boolean;
    customFontFaces?: FontFaceDesc[];
    directionSelector?: boolean;
    direction?: 'ltr' | 'rtl';
    perThemeProps?: boolean;
}

export interface EditorError {
    message: string;
    level: 'error' | 'warn';
}

export interface DivProEditorInstance {
    getValue(): string;
    getCard(): Card;
    getErrors(): EditorError[];
    setTheme(theme: Theme): void;
    setLayout(layout: Layout): void;
    setLocale(locale: Locale): void;
    setReadOnly(readOnly: boolean): void;
    setCustomActions(customActions: ActionDesc[]): void;
    setPaletteEnabled(toggle: boolean): void;
    destroy(): void;
}

export const DivProEditor = {
    init(opts: DivProEditorOptions): DivProEditorInstance {
        const json = JSON.parse(opts.card?.json || opts.value || '{}');

        const state = new State({
            locale: opts.locale || 'en',
            fileLimits: opts.fileLimits,
            getTranslationKey: opts.api?.getTranslationKey
        });

        state.paletteEnabled.set(opts.paletteEnabled ?? true);
        state.sources.set(opts.sources || []);
        state.customActions.set(opts.customActions || []);
        state.readOnly.set(opts.readOnly || false);
        state.themeStore.set(opts.theme || 'light');
        state.locale.set(opts.cardLocales?.[0]?.id || '');
        state.direction.set(opts.direction || 'ltr');

        if (Array.isArray(json?.card?.variables)) {
            const localPalette = json.card.variables.find((it?: JsonVariable) => it?.type === 'dict' && it.name === 'local_palette');
            if (localPalette && localPalette.value && typeof localPalette.value === 'object') {
                const list: PaletteItem[] = [];
                for (const id in localPalette.value) {
                    const item = localPalette.value[id];
                    if (id && item?.name && item.light && item.dark) {
                        list.push({
                            id,
                            name: item.name,
                            light: item.light,
                            dark: item.dark
                        });
                    }
                }
                state.palette.set(list);
            }
        }

        if (opts.card?.meta?.tanker) {
            state.tanker.set(opts.card.meta.tanker);
            for (const key in opts.card.meta.tanker) {
                state.storeTankerKey(key);
            }
        }

        const logId = json?.card?.log_id;
        if (logId) {
            state.rootLogId.set(logId);
        } else {
            state.rootLogId.set('div2_sample_card');
        }

        state.setDivJson(json);

        const app = new App({
            target: opts.renderTo,
            props: {
                state,
                layout: opts.layout,
                shadowRoot: opts.shadowRoot,
                actionLogUrlVariable: opts.actionLogUrlVariable,
                cardLocales: opts.cardLocales,
                previewWarnFileLimit: opts.previewWarnFileLimit,
                previewErrorFileLimit: opts.previewErrorFileLimit,
                warnFileLimit: opts.warnFileLimit,
                errorFileLimit: opts.errorFileLimit,
                fileLimits: opts.fileLimits,
                rootConfigurable: opts.rootConfigurable,
                customFontFaces: opts.customFontFaces,
                directionSelector: opts.directionSelector,
                perThemeProps: opts.perThemeProps,
                uploadFile: opts.api?.uploadFile,
                editorFabric: opts.api?.editorFabric,
                getTranslationKey: opts.api?.getTranslationKey,
                getTranslationSuggest: opts.api?.getTranslationSuggest
            }
        });

        let unsubscribeStore: Unsubscriber | undefined;
        if (opts.api?.onChange) {
            let isFirst = true;
            unsubscribeStore = state.divjsonStore.subscribe(() => {
                if (isFirst) {
                    isFirst = false;
                } else {
                    opts.api?.onChange?.();
                }
            });
        }

        return {
            getValue() {
                return get(state.divjsonStore).fullString;
            },
            getCard() {
                return {
                    json: get(state.divjsonStore).fullString,
                    meta: {
                        tanker: get(state.tanker)
                    }
                };
            },
            setTheme(theme) {
                state.themeStore.set(theme);
            },
            setLayout(layout) {
                app.setLayout(layout);
            },
            setLocale(locale) {
                state.lang.set(locale);
            },
            setReadOnly(readOnly) {
                state.readOnly.set(readOnly);
            },
            setCustomActions(customActions) {
                state.customActions.set(customActions);
            },
            setPaletteEnabled(toggle) {
                state.paletteEnabled.set(toggle);
            },
            getErrors() {
                return state.getEditorErrors();
            },
            destroy() {
                app.$destroy();
                unsubscribeStore?.();
            }
        };
    }
};

export { convertDictToPalette, convertPaletteToDict } from './lib/utils/convertPalette';
export { addTemplatesSuffix, removeTemplatesSuffix } from './lib/utils/renameTemplates';
