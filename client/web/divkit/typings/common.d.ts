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
    /** EXPERIMENTAL SUPPORT */
    palette?: Palette;
}

export interface Action {
    log_id: string;
    url?: string;
    payload?: Record<string, unknown>;
    log_url?: string;
    target?: string;
}

export interface VisibilityAction {
    log_id: string;
    url?: string;
    referer?: string;
    payload?: Record<string, string>;
    visibility_percentage?: number;
    visibility_duration?: number;
    // download_callbacks
    log_limit?: number;
}

export interface DisappearAction {
    log_id: string;
    url?: string;
    referer?: string;
    payload?: Record<string, string>;
    visibility_percentage?: number;
    disappear_duration?: number;
    // download_callbacks
    log_limit?: number;
}

export type StatCallback = (details: {
    type: string;
    action: Action | VisibilityAction | DisappearAction;
}) => void;

export type CustomActionCallback = (action: Action & { url: string }) => void;

export type ComponentCallback = (details: {
    type: 'mount';
    node: HTMLElement;
    json: DivBase;
    origJson: DivBase | undefined;
    templateContext: TemplateContext;
} | {
    type: 'destroy';
    node: HTMLElement;
}) => void;

export interface WrappedError extends Error {
    level: 'error' | 'warn';
    additional?: Record<string, unknown>;
}

export type ErrorCallback = (details: {
    error: WrappedError;
}) => void;

export interface DivkitInstance {
    $destroy(): void;
    execAction(action: Action | VisibilityAction): void;
    setTheme(theme: Theme): void;
}

export type Platform = 'desktop' | 'touch' | 'auto';

export type Theme = 'system' | 'light' | 'dark';

export interface Customization {
    galleryLeftClass?: string;
    galleryRightClass?: string;
}

export interface DivExtensionContext {
    variables: Map<string, Variable>;
    logError(error: WrappedError): void;
}

export interface DivExtensionClass {
    new(params: object): DivExtension;
    prototype: DivExtension;
}

export interface DivExtension {
    mountView?(node: HTMLElement, context: DivExtensionContext): void;

    unmountView?(node: HTMLElement, context: DivExtensionContext): void;
}
