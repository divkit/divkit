import type { FileLimits } from '../../lib';

const cache = new Map<string, Promise<number>>();

export function getFileSize(value: string, fileType: string): Promise<number> {
    const cached = cache.get(value || '');
    if (cached) {
        return cached;
    }

    if (fileType === 'image_preview' || !value || value.startsWith('data:')) {
        return Promise.resolve(String(value || '').length);
    }

    const res = fetch(value)
        .then(res => res.blob())
        .then(blob => {
            return blob.size;
        });

    cache.set(value, res);

    return res;
}

export function calcFileSizeMod(
    currentSize: number | undefined,
    type: keyof FileLimits,
    warnLimit: number,
    errorLimit: number,
    fileLimits: FileLimits | undefined
): 'error' | 'warn' | '' {
    if (!currentSize || currentSize < 1) {
        return '';
    }

    if (fileLimits) {
        const limits = fileLimits[type];
        if (!limits) {
            return '';
        }
        if (limits.error !== undefined && currentSize > limits.error) {
            return 'error';
        }
        if (limits.warn !== undefined && currentSize > limits.warn) {
            return 'warn';
        }

        return '';
    }

    if (currentSize > errorLimit) {
        return 'error';
    }
    if (currentSize > warnLimit) {
        return 'warn';
    }

    return '';
}
