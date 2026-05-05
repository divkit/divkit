import type {
    DivJson,
    ErrorCallback,
    Platform,
    Customization,
    TypefaceProvider,
    Direction,
    VideoPlayerProviderServer,
    Theme
} from './common';
import type { CustomComponentDescription } from './custom';
import type { Store } from './store';

export function render(opts: {
    json: DivJson;
    id: string;
    platform?: Platform;
    theme?: Theme;
    themeVariableName?: string;
    mix?: string;
    customization?: Customization;
    builtinProtocols?: string[];
    onError?: ErrorCallback;
    typefaceProvider?: TypefaceProvider;
    customComponents?: Map<string, CustomComponentDescription> | undefined;
    direction?: Direction;
    store?: Store;
    weekStartDay?: number;
    pagerChildrenClipEnabled?: boolean;
    videoPlayerProvider?: VideoPlayerProviderServer;
}): string;
