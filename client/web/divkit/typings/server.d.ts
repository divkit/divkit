import type {
    DivJson,
    ErrorCallback,
    Platform,
    Customization,
    TypefaceProvider
} from './common';
import type { CustomComponentDescription } from './custom';

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
}): string;
