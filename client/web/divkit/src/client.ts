import Root from './components/Root.svelte';
import type {
    ComponentCallback,
    CustomActionCallback,
    Customization,
    DivExtensionClass,
    DivJson,
    ErrorCallback,
    Platform,
    StatCallback,
    Theme,
    TypefaceProvider
} from '../typings/common';
import type { GlobalVariablesController } from './expressions/globalVariablesController';

export function render(opts: {
    target: HTMLElement;
    json: DivJson;
    id: string;
    hydrate?: boolean;
    globalVariablesController?: GlobalVariablesController;
    mix?: string;
    customization?: Customization;
    builtinProtocols?: string[];
    extensions?: Map<string, DivExtensionClass>;
    onStat?: StatCallback;
    onCustomAction?: CustomActionCallback;
    onError?: ErrorCallback;
    onComponent?: ComponentCallback;
    typefaceProvider?: TypefaceProvider;
    platform?: Platform;
    theme?: Theme;
}) {
    return new Root({
        target: opts.target,
        props: {
            id: opts.id,
            json: opts.json,
            globalVariablesController: opts.globalVariablesController,
            mix: opts.mix,
            customization: opts.customization,
            builtinProtocols: opts.builtinProtocols,
            extensions: opts.extensions,
            onStat: opts.onStat,
            onCustomAction: opts.onCustomAction,
            onError: opts.onError,
            onComponent: opts.onComponent,
            typefaceProvider: opts.typefaceProvider,
            platform: opts.platform,
            theme: opts.theme
        },
        hydrate: opts.hydrate
    });
}

export {
    createGlobalVariablesController
} from './expressions/globalVariablesController';

export {
    createVariable
} from './expressions/variable';

export {
    SizeProvider
} from './extensions/sizeProvider';

export {
    lottieExtensionBuilder
} from './extensions/lottie';
