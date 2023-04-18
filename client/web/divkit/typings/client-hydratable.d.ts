import type {
    DivJson,
    StatCallback,
    ErrorCallback,
    DivkitInstance,
    Platform,
    CustomActionCallback,
    Theme,
    Customization,
    DivExtensionClass
} from './common';
import type { GlobalVariablesController } from './variables';

export function render(opts: {
    target: HTMLElement;
    json: DivJson;
    id: string;
    hydrate?: boolean;
    globalVariablesController?: GlobalVariablesController;
    mix?: string;
    onStat?: StatCallback;
    onCustomAction?: CustomActionCallback;
    onError?: ErrorCallback;
    platform?: Platform;
    customization?: Customization;
    builtinProtocols?: string[];
    extensions?: Map<string, DivExtensionClass>;
    /** EXPERIMENTAL SUPPORT */
    theme?: Theme;
}): DivkitInstance;

export { createVariable, createGlobalVariablesController } from './variables';

export {
    SizeProvider,
    lottieExtensionBuilder
} from './extensions';
