import type {
    DivJson,
    ErrorCallback,
    Platform,
    Customization,
    TypefaceProvider
} from './common';

export function render(opts: {
    json: DivJson;
    id: string;
    platform?: Platform;
    mix?: string;
    customization?: Customization;
    builtinProtocols?: string[];
    onError?: ErrorCallback;
    typefaceProvider?: TypefaceProvider;
}): string;
