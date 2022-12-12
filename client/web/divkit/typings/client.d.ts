import type {
    DivJson,
    StatCallback,
    ErrorCallback,
    DivkitInstance,
    Platform,
    CustomActionCallback,
    Theme,
    Customization
} from './common';
import type { GlobalVariablesController } from './variables';

export function render(opts: {
    target: HTMLElement;
    json: DivJson;
    id: string;
    globalVariablesController?: GlobalVariablesController;
    mix?: string;
    onStat?: StatCallback;
    onCustomAction?: CustomActionCallback;
    onError?: ErrorCallback;
    platform?: Platform;
    customization?: Customization;
    /** EXPERIMENTAL SUPPORT */
    theme?: Theme;
}): DivkitInstance;

export { createVariable, createGlobalVariablesController } from './variables';
