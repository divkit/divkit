import Root from './components/Root.svelte';
import type { Direction, Platform, TypefaceProvider } from '../typings/common';
import type { GlobalVariablesController } from './expressions/globalVariablesController';
import type { CustomComponentDescription } from '../typings/custom';

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
    customComponents?: Map<string, CustomComponentDescription> | undefined;
    direction?: Direction;
}) {
    // Root has client-side typings, not a server one
    return (Root as any).render(opts).html;
}

export {
    createGlobalVariablesController
} from './expressions/globalVariablesController';

export {
    createVariable
} from './expressions/variable';
