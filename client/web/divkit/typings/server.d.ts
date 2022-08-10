import type { DivJson, ErrorCallback, Platform } from './common';

export function render(opts: {
    json: DivJson;
    id: string;
    onError?: ErrorCallback;
    platform?: Platform;
}): string;
