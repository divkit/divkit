import Root from './components/Root.svelte';
import type {
    ComponentCallback,
    CustomActionCallback,
    Customization,
    DivExtensionClass,
    DivJson,
    ErrorCallback,
    FetchInit,
    Platform,
    StatCallback,
    Theme,
    TypefaceProvider
} from '../typings/common';
import type { GlobalVariablesController } from './expressions/globalVariablesController';
import type { CustomComponentDescription } from '../typings/custom';

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
    fetchInit?: FetchInit;
    tooltipRoot?: HTMLElement;
    customComponents?: Map<string, CustomComponentDescription> | undefined;
}) {
    const { target, hydrate, ...rest } = opts;

    return new Root({
        target: target,
        props: rest,
        hydrate: hydrate
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
