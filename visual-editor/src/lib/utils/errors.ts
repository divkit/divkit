export interface ViewerError {
    message: string;
    stack: string[];
    level?: string;
    args?: {
        leafId?: string | undefined;
    } & Record<string, unknown>;
}
