import type {
    DivJson,
    ErrorCallback,
    Platform,
    Customization,
    TypefaceProvider
} from './common';
import type { CustomComponentDescription } from './custom';
import type { Store } from './store';

export function render(opts: {
    json: DivJson;
    id: string;
    platform?: Platform;
    mix?: string;
    customization?: Customization;
    builtinProtocols?: string[];
    onError?: ErrorCallback;
    typefaceProvider?: TypefaceProvider;
    customComponents?: Map<string, CustomComponentDescription> | undefined;
    store?: Store;
    weekStartDay?: number;
}): string;
