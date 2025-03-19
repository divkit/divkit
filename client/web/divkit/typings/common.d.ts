import type { ComponentContext } from '../src/types/componentContext';
import type { Variable } from './variables';

export type Subscriber<T> = (value: T) => void;
export type Unsubscriber = () => void;

export type Interpolation = 'linear' | 'ease' | 'ease_in' | 'ease_out' | 'ease_in_out' | 'spring';

export type AnimatorRepeatCount = {
    type: 'infinity';
} | {
    type: 'fixed';
    value: number;
};

export type AnimatorDirection = 'normal' | 'reverse' | 'alternate' | 'alternate_reverse';

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

export interface DivDictVariable {
    type: 'dict';
    name: string;
    value: object;
}

export interface DivArrayVariable {
    type: 'array';
    name: string;
    value: unknown[];
}

export type DivVariable = DivStrVariable | DivIntVariable | DivNumberVariable |
    DivBooleanVariable | DivColorVariable | DivUrlVariable | DivDictVariable |
    DivArrayVariable;

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

export interface ActionAnimatorStart {
    type: 'animator_start';
    animator_id: string;
    duration?: number;
    start_delay?: number;
    interpolator?: Interpolation;
    direction?: AnimatorDirection;
    repeat_count?: AnimatorRepeatCount;
    start_value?: TypedValue;
    end_value?: TypedValue;
}

export interface ActionAnimatorStop {
    type: 'animator_stop';
    animator_id: string;
}

export interface ActionShowTooltip {
    type: 'show_tooltip';
    id: string;
    multiple: boolean;
}

export interface ActionHideTooltip {
    type: 'hide_tooltip';
    id: string;
}

export interface ActionTimer {
    type: 'timer';
    id: string;
    action: 'start' | 'stop' | 'pause' | 'resume' | 'cancel' | 'reset';
}

export interface ActionDownload {
    type: 'download';
    url: string;
    on_fail_actions?: Action[];
    on_success_actions?: Action[];
}

export interface ActionVideo {
    type: 'video';
    id: string;
    action: 'start' | 'pause';
}

export interface ActionStore {
    type: 'set_stored_value';
    name: string;
    value: TypedValue;
    lifetime: number;
}

export interface ActionSetState {
    type: 'set_state';
    state_id: string;
    // temporary
}

export interface ActionSubmitHeader {
    name: string;
    value: string;
}

export type ActionSubmitMethod = 'get' | 'post' | 'put' | 'patch' | 'delete' | 'head' | 'options';

export interface ActionSubmitRequest {
    url: string;
    headers?: ActionSubmitHeader[];
    method?: ActionSubmitMethod;
}

export interface ActionSubmit {
    type: 'submit';
    container_id: string;
    request: ActionSubmitRequest;
    on_success_actions?: Action[];
    on_fail_actions?: Action[];
}

export type Overflow = 'clamp' | 'ring';

export interface ActionScrollBy {
    type: 'scroll_by';
    id: string;
    animated?: boolean;
    overflow?: Overflow;
    offset?: number;
    item_count?: number;
}

export interface ActionScrollToDestinationOffset {
    type: 'offset';
    value: number;
}

export interface ActionScrollToDestinationIndex {
    type: 'index';
    value: number;
}

export interface ActionScrollToDestinationStart {
    type: 'start';
}

export interface ActionScrollToDestinationEnd {
    type: 'end';
}

export type ActionScrollToDestination = ActionScrollToDestinationOffset | ActionScrollToDestinationIndex |
    ActionScrollToDestinationStart | ActionScrollToDestinationEnd;

export interface ActionScrollTo {
    type: 'scroll_to';
    id: string;
    animated?: boolean;
    destination: ActionScrollToDestination;
}

export type TypedAction = ActionSetVariable | ActionArrayRemoveValue | ActionArrayInsertValue |
    ActionCopyToClipboard | ActionFocusElement | ActionClearFocus | ActionDictSetValue | ActionArraySetValue |
    ActionAnimatorStart | ActionAnimatorStop | ActionShowTooltip | ActionHideTooltip | ActionTimer | ActionDownload |
    ActionVideo | ActionStore | ActionSetState | ActionSubmit | ActionScrollBy | ActionScrollTo;

export interface ActionBase {
    log_id: string;
    scope_id?: string;
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
    componentContext: ComponentContext;
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

export type SubmitCallback = (action: ActionSubmit, values: Record<string, unknown>) => Promise<void>;

export interface DivkitInstance {
    $destroy(): void;
    execAction(action: Action | VisibilityAction | DisappearAction): void;
    /** @deprecated */
    setTheme(theme: Theme): void;
    /** Experimental */
    setData(json: DivJson): void;
    applyPatch(patch: Patch): boolean;
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

export interface DerivedExpression<T> {
    subscribe(cb: Subscriber<T>): Unsubscriber;
}

export interface DivExtensionContext {
    variables: Map<string, Variable>;
    direction: Direction;
    processExpressions<T>(t: T): T;
    derviedExpression<T>(t: T): DerivedExpression<T>;
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

export interface PatchChange {
    id: string;
    items?: DivBase[];
}

export interface Patch {
    templates?: Record<string, DivBase>;
    patch: {
        mode?: 'transactional' | 'partial';
        changes: PatchChange[];
        on_applied_actions?: Action[];
        on_failed_actions?: Action[];
    };
}

export interface VideoSource {
    type: 'video_source';

    url: string;
    mime_type: string;
    resolution?: {
        type: 'resolution';
        width: number;
        height: number;
    };
    bitrate?: number;
}

export type VideoScale = 'fill' | 'no_scale' | 'fit';

export interface VideoPlayerProviderData {
    sources: VideoSource[];
    repeatable?: boolean;
    autostart?: boolean;
    preloadRequired?: boolean;
    muted?: boolean;
    preview?: string;
    aspect?: number;
    scale?: VideoScale;
    payload?: Record<string, unknown>;
}

export interface VideoPlayerInstance {
    update?(data: VideoPlayerProviderData): void;
    seek?(positionInMS: number): void;
    pause(): void;
    play(): void;
    destroy(): void;
}

export interface VideoPlayerProviderClient {
    instance: (parent: HTMLElement, data: VideoPlayerProviderData) => VideoPlayerInstance | undefined | void;
}

export interface VideoPlayerProviderServer {
    template: string | ((data: VideoPlayerProviderData) => string);
}

export type VideoPlayerProvider = VideoPlayerProviderClient | VideoPlayerProviderServer;
