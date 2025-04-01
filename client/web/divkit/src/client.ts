import Root from './components/Root.svelte';
import type {
    ComponentCallback,
    CustomActionCallback,
    Customization,
    Direction,
    DivExtensionClass,
    DivJson,
    DivkitInstance,
    ErrorCallback,
    FetchInit,
    Platform,
    StatCallback,
    Theme,
    TypefaceProvider,
    Patch,
    VideoPlayerProviderClient
} from '../typings/common';
import type { GlobalVariablesController } from './expressions/globalVariablesController';
import type { CustomComponentDescription } from '../typings/custom';
import type { Store } from '../typings/store';

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
    direction?: Direction;
    store?: Store;
    weekStartDay?: number;
    pagerChildrenClipEnabled?: boolean;
    videoPlayerProvider?: VideoPlayerProviderClient;
}): DivkitInstance {
    const { target, hydrate, ...rest } = opts;

    const instance = new Root({
        target: target,
        props: rest,
        hydrate: hydrate
    });

    return {
        $destroy() {
            instance.$destroy();
        },
        execAction(action) {
            instance.execAction(action);
        },
        setTheme(theme) {
            instance.setTheme(theme);
        },
        setData(newJson) {
            instance.setData(newJson);
        },
        applyPatch(patch: Patch) {
            return instance.applyPatch(patch);
        }
    };
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
    Gesture
} from './extensions/gesture';

export {
    lottieExtensionBuilder
} from './extensions/lottie';

export {
    markdownExtensionBuilder
} from './extensions/markdown';
