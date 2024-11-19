import { formatSize } from './formatSize';

const SUFFIXES = ['B', 'KB', 'MB', 'GB', 'TB'];

export function formatFileSize(size: number): string {
    if (size < 1) {
        return '';
    }

    let index = 0;

    while (size > 1024 && index + 1 < SUFFIXES.length) {
        ++index;
        size /= 1024;
    }

    return formatSize(size, 1) + ' ' + SUFFIXES[index];
}
