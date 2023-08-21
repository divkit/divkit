import Root from './components/Root.svelte';
import type { Platform, TypefaceProvider } from '../typings/common';
import type { GlobalVariablesController } from './expressions/globalVariablesController';

export function render(opts: {
    json: unknown;
    id: string;
    globalVariablesController?: GlobalVariablesController;
    mix?: string;
    customization?: unknown;
    builtinProtocols?: unknown;
    onError?: unknown;
    typefaceProvider?: TypefaceProvider;
    platform?: Platform;
}) {
    // Root has client-side typings, not a server one
    return (Root as any).render({
        id: opts.id,
        json: opts.json,
        globalVariablesController: opts.globalVariablesController,
        mix: opts.mix,
        customization: opts.customization,
        builtinProtocols: opts.builtinProtocols,
        onError: opts.onError,
        typefaceProvider: opts.typefaceProvider,
        platform: opts.platform
    }).html;
}

export {
    createGlobalVariablesController
} from './expressions/globalVariablesController';

export {
    createVariable
} from './expressions/variable';
