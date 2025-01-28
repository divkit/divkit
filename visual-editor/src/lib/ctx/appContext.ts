import type { Action } from '@divkitframework/divkit/typings/common';
import type { CardLocale, EditorInstance, EditorOptions, FontFaceDesc, GetTranslationKey, GetTranslationSuggest } from '../../lib';
import type { State } from '../data/state';
import type { TreeLeaf } from './tree';
import type { Background } from '../data/background';
import type { VideoSource } from '../utils/video';
import type { SelectOption } from '../utils/select';

export const APP_CTX = Symbol('app');

export interface MenuItem {
    text: string;
    icon?: string;
    enabled?: boolean;
    callback: () => boolean | void;
    submenu?: MenuItem[];
}

export interface InplaceEditorShowProps {
    text: string;
    json: object;
    leaf: TreeLeaf;
    rotation: number;
    disabled?: boolean;
    textDisabled?: boolean;
    style: Record<string, string>;
    callback(value: {
        text: string;
        ranges: object[];
        images: object[];
        textAlign: string;
    }): void;
    onResoze(size: {
        width: number;
        height: number;
    }): void;
}

export interface Actions2DialogShowProps {
    value: Action;
    target: HTMLElement;
    readOnly?: boolean;
    callback(val: Action): void;
}

export interface Background2DialogShowProps {
    value: Background;
    target: HTMLElement;
    readOnly?: boolean;
    callback(val: Background): void;
}

export interface Color2DialogShowProps {
    value: string;
    paletteId?: string;
    target: HTMLElement;
    disabled?: boolean;
    direction?: 'left' | 'right';
    offset?: number;
    showPalette?: boolean;
    callback(val: string, paletteId: string): void;
}

export interface Expression2ShowProps {
    value: string;
    target: HTMLElement;
    disabled?: boolean;
    callback(val: string): void;
}

export interface File2DialogValue {
    url: string;
    width?: number;
    height?: number;
}

export type File2DialogCallback = (opts: File2DialogValue) => void;

export interface File2DialogShowProps {
    value: File2DialogValue;
    title: string;
    subtype: 'image' | 'gif' | 'lottie' | 'video' | 'image_preview';
    direction?: 'left' | 'right';
    hasSize?: boolean;
    hasDelete?: boolean;
    target: HTMLElement;
    disabled?: boolean;
    generateFromVideo?: VideoSource[];
    callback: File2DialogCallback;
    onHide?(): void;
}

export interface Link2DialogShowProps {
    value: string;
    target: HTMLElement;
    disabled?: boolean;
    callback(val: string): void;
}

export interface Tanker2DialogShowProps {
    value: string;
    target: HTMLElement;
    disabled?: boolean;
    callback(val: string): void;
}

export interface TextAlign2DialogShowProps {
    value: string;
    target: HTMLElement;
    disabled?: boolean;
    callback(val: string): void;
}

export interface VideoSourceShowProps {
    value: VideoSource;
    target: HTMLElement;
    readOnly?: boolean;
    callback(val: VideoSource): void;
}

export interface SelectOptionsShowProps {
    value: SelectOption;
    target: HTMLElement;
    readOnly?: boolean;
    callback(val: SelectOption): void;
}

export interface SelectedElemProps {
    left: number;
    top: number;
    width: number;
    height: number;
}

export interface ContainerProps {
    width: number;
    height: number;
}

export interface RendererApi {
    containerProps(): ContainerProps;
    selectedElemProps(): SelectedElemProps | null;
    focus(): void;
    evalJson(_json: object): object;
}

export type ShowErrors = (opts?: {
    leafId: string;
}) => void;

export interface ContextMenuShowProps {
    name: string;
    owner: EventTarget | null;
    items: MenuItem[];
    offset?: {
        x?: number;
        y?: number;
    };
}

export interface ContextMenuApi {
    toggle(props: ContextMenuShowProps): void;
    hide(): void;
}

export interface InplaceEditorApi {
    show(props: InplaceEditorShowProps): void;
    hide(): void;
    setPosition(left: string, top: string): void;
}

export interface Actions2DialogApi {
    show(props: Actions2DialogShowProps): void;
    hide(): void;
}

export interface Background2DialogApi {
    show(props: Background2DialogShowProps): void;
    hide(): void;
}

export interface Color2DialogApi {
    show(props: Color2DialogShowProps): void;
    hide(): void;
}

export interface Expression2DialogApi {
    show(props: Expression2ShowProps): void;
    hide(): void;
}

export interface File2DialogApi {
    show(props: File2DialogShowProps): void;
    hide(): void;
}

export interface Link2DialogApi {
    show(props: Link2DialogShowProps): void;
    hide(): void;
}

export interface Tanker2DialogApi {
    show(props: Tanker2DialogShowProps): void;
    hide(): void;
}

export interface TextAlign2DialogApi {
    show(props: TextAlign2DialogShowProps): void;
    hide(): void;
}

export interface VideoSource2DialogApi {
    show(props: VideoSourceShowProps): void;
    hide(): void;
}

export interface SelectOptionsDialogApi {
    show(props: SelectOptionsShowProps): void;
    hide(): void;
}

export interface AppContext {
    state: State;
    uploadFile(file: File): Promise<string>;
    editorFabric(opts: EditorOptions): EditorInstance;
    shadowRoot?: ShadowRoot;
    ownerDocument: ShadowRoot | Document;
    getSelection(): Selection | null;
    actionLogUrlVariable: string;
    cardLocales: CardLocale[];
    getTranslationSuggest: GetTranslationSuggest | undefined;
    getTranslationKey: GetTranslationKey | undefined;
    previewWarnFileLimit: number;
    previewErrorFileLimit: number;
    warnFileLimit: number;
    errorFileLimit: number;
    rootConfigurable: boolean;
    customFontFaces: FontFaceDesc[];
    directionSelector: boolean;

    rendererApi: () => RendererApi;
    setRendererApi(api: RendererApi): void;

    showErrors: ShowErrors;
    setShowErrors(fn: ShowErrors): void;

    contextMenu: () => ContextMenuApi;
    setContextMenuApi: (api: ContextMenuApi) => void;

    inplaceEditor: () => InplaceEditorApi;
    actions2Dialog: () => Actions2DialogApi;
    background2Dialog: () => Background2DialogApi;
    color2Dialog: () => Color2DialogApi;
    expression2Dialog: () => Expression2DialogApi;
    file2Dialog: () => File2DialogApi;
    link2Dialog: () => Link2DialogApi;
    tanker2Dialog: () => Tanker2DialogApi;
    textAlign2Dialog: () => TextAlign2DialogApi;
    videoSource2Dialog: () => VideoSource2DialogApi;
    selectOptionsDialog: () => SelectOptionsDialogApi;
}
