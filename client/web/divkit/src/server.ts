import Root from './components/Root.svelte';
import type { Customization, Direction, DivJson, ErrorCallback, Platform, TypefaceProvider, VideoPlayerProviderServer } from '../typings/common';
import type { GlobalVariablesController } from './expressions/globalVariablesController';
import type { CustomComponentDescription } from '../typings/custom';
import type { Store } from '../typings/store';

export function render(opts: {
    json: DivJson;
    id: string;
    globalVariablesController?: GlobalVariablesController;
    mix?: string;
    customization?: Customization;
    builtinProtocols?: string[];
    onError?: ErrorCallback;
    typefaceProvider?: TypefaceProvider;
    platform?: Platform;
    customComponents?: Map<string, CustomComponentDescription> | undefined;
    direction?: Direction;
    store?: Store;
    weekStartDay?: number;
    pagerChildrenClipEnabled?: boolean;
    videoPlayerProvider?: VideoPlayerProviderServer;
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
