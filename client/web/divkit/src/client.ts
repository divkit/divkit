import Root from './components/Root.svelte';
import type { Platform, Theme } from '../typings/common';
import type { GlobalVariablesController } from './expressions/globalVariablesController';

export function render(opts: {
    target: HTMLElement;
    json: unknown;
    id: string;
    hydrate?: boolean;
    globalVariablesController?: GlobalVariablesController;
    mix?: string;
    customization?: unknown;
    onStat?: unknown;
    onCustomAction?: unknown;
    onError?: unknown;
    onComponent?: unknown;
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
            onStat: opts.onStat,
            onCustomAction: opts.onCustomAction,
            onError: opts.onError,
            onComponent: opts.onComponent,
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
