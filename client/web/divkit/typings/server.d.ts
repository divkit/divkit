import type {
    DivJson,
    ErrorCallback,
    Platform,
    Customization
} from './common';

export function render(opts: {
    json: DivJson;
    id: string;
    platform?: Platform;
    mix?: string;
    customization?: Customization;
    onError?: ErrorCallback;
}): string;
