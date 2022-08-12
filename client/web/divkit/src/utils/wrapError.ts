export interface WrappedError extends Error {
    level: 'error' | 'warn';
    additional?: Record<string, unknown>;
}

export type LogError = (error: WrappedError) => void;

export function wrapError(error: Error, params: {
    level?: 'error' | 'warn';
    additional?: Record<string, unknown>;
} = {}): WrappedError {
    const wrapped = error as WrappedError;

    wrapped.level = params.level || 'error';

    if (params.additional) {
        wrapped.additional = params.additional;
    }

    return wrapped;
}
