import type { DivJson, ErrorCallback, Platform } from './common';

export function render(opts: {
    json: DivJson;
    id: string;
    platform?: Platform;
    mix?: string;
    onError?: ErrorCallback;
}): string;
