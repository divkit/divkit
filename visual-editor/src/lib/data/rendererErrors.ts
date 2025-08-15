import type { ViewerError } from '../utils/errors';

export function isViewerError(error: ViewerError): boolean {
    return error.level === 'error' || !error.level;
}

export function isViewerWarning(error: ViewerError): boolean {
    return error.level === 'warn';
}
