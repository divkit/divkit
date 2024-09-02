import type { Variable } from './variables';

export type BooleanInt = 0 | 1 | false | true;

export type TemplateContext = Record<string, unknown>;

export interface DivBase {
    type: string;
    [key: string]: unknown;
}

export interface DivJsonState {
    state_id: number;
    div: DivBase;
}

export interface DivStrVariable {
    type: 'string';
    name: string;
    value: string;
}

export interface DivIntVariable {
    type: 'integer';
    name: string;
    value: number | bigint;
}

export interface DivNumberVariable {
    type: 'number';
    name: string;
    value: number;
}

export interface DivBooleanVariable {
    type: 'boolean';
    name: string;
    value: BooleanInt;
}

export interface DivColorVariable {
    type: 'color';
    name: string;
    value: string;
}

export interface DivUrlVariable {
    type: 'url';
    name: string;
    value: string;
}

export type DivVariable = DivStrVariable | DivIntVariable | DivNumberVariable |
    DivBooleanVariable | DivColorVariable | DivUrlVariable;

export interface VariableTrigger {
    mode?: 'on_condition' | 'on_variable';
    condition: BooleanInt | string;
    actions: Action[];
}

export interface DivTimer {
    id: string;
    duration?: number;
    tick_interval?: number;
    value_variable?: string;
    tick_actions?: Action[];
    end_actions?: Action[];
}

export interface PaletteColor {
    name: string;
    color: string;
}

export type PaletteList = PaletteColor[];

export interface Palette {
    light: PaletteList;
    dark: PaletteList;
}

export interface DivJson {
    templates?: Record<string, unknown>;
    card: {
        log_id: string;
        states: DivJsonState[];
        variables?: DivVariable[];
        variable_triggers?: VariableTrigger[];
        timers?: DivTimer[];
    }
    /** @deprecated */
    palette?: Palette;
}

export interface DownloadCallbacks {
    on_fail_actions?: Action[];
    on_success_actions?: Action[];
}

export interface StringValue {
    type: 'string';
    value: string;
}

export interface IntegerValue {
    type: 'integer';
    value: number;
}

export interface NumberValue {
    type: 'number';
    value: number;
}

export interface ColorValue {
    type: 'color';
    value: string;
}

export interface BooleanValue {
    type: 'boolean';
    value: boolean;
}

export interface UrlValue {
    type: 'url';
    value: string;
}

export interface DictValue {
    type: 'dict';
    value: Record<string, unknown>;
}

export interface ArrayValue {
    type: 'array';
    value: unknown[];
}

export type TypedValue = StringValue | IntegerValue | NumberValue | ColorValue |
    BooleanValue | UrlValue | DictValue | ArrayValue;

export interface ActionSetVariable {
    type: 'set_variable';
    variable_name: string;
    value: TypedValue;
}

export interface ActionArrayRemoveValue {
    type: 'array_remove_value';
    variable_name: string;
    index: number;
}

export interface ActionArrayInsertValue {
    type: 'array_insert_value';
    variable_name: string;
    index?: number;
    value: TypedValue;
}

export interface CopyToClipboardContentText {
    type: 'text';
    value: string;
}

export interface CopyToClipboardContentUrl {
    type: 'url';
    value: string;
}

export type CopyToClipboardContent = CopyToClipboardContentText | CopyToClipboardContentUrl;

export interface ActionCopyToClipboard {
    type: 'copy_to_clipboard';
    content: CopyToClipboardContent;
}

export interface ActionFocusElement {
    type: 'focus_element';
    element_id: string;
}

export interface ActionClearFocus {
    type: 'clear_focus';
}

export interface ActionDictSetValue {
    type: 'dict_set_value';
    variable_name: string;
    key: string;
    value?: TypedValue;
}

export interface ActionArraySetValue {
    type: 'array_set_value';
    variable_name: string;
    index: number;
    value: TypedValue;
}

export type TypedAction = ActionSetVariable | ActionArrayRemoveValue | ActionArrayInsertValue |
    ActionCopyToClipboard | ActionFocusElement | ActionClearFocus | ActionDictSetValue | ActionArraySetValue;

export interface ActionBase {
    log_id: string;
    url?: string;
    // referer
    payload?: Record<string, unknown>;
    download_callbacks?: DownloadCallbacks;
    typed?: TypedAction;
    is_enabled?: BooleanInt;
}

export interface ActionMenuItem {
    text: string;
    action?: Action;
    actions?: Action[];
}

export interface Action extends ActionBase {
    menu_items?: ActionMenuItem[];
    log_url?: string;
    target?: string;
}

export interface SightAction extends ActionBase {
    log_limit?: number;
}

export interface VisibilityAction extends SightAction {
    visibility_percentage?: number;
    visibility_duration?: number;
}

export interface DisappearAction extends SightAction {
    visibility_percentage?: number;
    disappear_duration?: number;
}

export type StatCallback = (details: {
    type: string;
    action: Action | VisibilityAction | DisappearAction;
}) => void;

export type CustomActionCallback = (action: Action & { url: string }) => void;

export type ComponentCallback = (details: {
    type: 'mount' | 'update' | 'destroy';
    node: HTMLElement | null;
    json: DivBase;
    origJson: DivBase | undefined;
    templateContext: TemplateContext;
}) => void;

export interface WrappedError extends Error {
    level: 'error' | 'warn';
    additional?: Record<string, unknown>;
}

export type ErrorCallback = (details: {
    error: WrappedError;
}) => void;

export type TypefaceProvider = (fontFamily: string, opts?: {
    fontWeight?: number;
}) => string;

export type FetchInit = RequestInit | ((url: string) => RequestInit);

export interface DivkitInstance {
    $destroy(): void;
    execAction(action: Action | VisibilityAction | DisappearAction): void;
    /** @deprecated */
    setTheme(theme: Theme): void;
    /** Experimental */
    setData(json: DivJson): void;
}

export type Platform = 'desktop' | 'touch' | 'auto';

export type Theme = 'system' | 'light' | 'dark';

export type Direction = 'ltr' | 'rtl';

export interface Customization {
    galleryLeftClass?: string;
    galleryRightClass?: string;
    pagerLeftClass?: string;
    pagerRightClass?: string;
    menuPopupClass?: string;
    menuItemClass?: string;
}

export interface DivExtensionContext {
    variables: Map<string, Variable>;
    processExpressions<T>(t: T): T;
    execAction(action: Action | VisibilityAction | DisappearAction): void;
    logError(error: WrappedError): void;
    getComponentProperty<T>(property: string): T;
}

export interface DivExtensionClass {
    new(params: object): DivExtension;
    prototype: DivExtension;
}

export interface DivExtension {
    mountView?(node: HTMLElement, context: DivExtensionContext): void;

    updateView?(node: HTMLElement, context: DivExtensionContext): void;

    unmountView?(node: HTMLElement, context: DivExtensionContext): void;
}
