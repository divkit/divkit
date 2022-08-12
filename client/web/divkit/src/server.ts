import Root from './components/root.svelte';
import type { Platform } from '../typings/common';
import type { GlobalVariablesController } from './expressions/globalVariablesController';

export function render(opts: {
    json: unknown;
    id: string;
    globalVariablesController?: GlobalVariablesController;
    onError?: unknown;
    platform?: Platform;
}) {
    // Root has client-side typings, not a server one
    return (Root as any).render({
        id: opts.id,
        json: opts.json,
        globalVariablesController: opts.globalVariablesController,
        onError: opts.onError,
        platform: opts.platform
    }).html;
}

export {
    createGlobalVariablesController
} from './expressions/globalVariablesController';

export {
    createVariable
} from './expressions/variable';
