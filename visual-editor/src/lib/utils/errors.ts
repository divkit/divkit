export interface ViewerError {
    message: string;
    stack: string[];
    level?: 'error' | 'warn';
    args?: {
        leafId?: string | undefined;
    } & Record<string, unknown>;
}
