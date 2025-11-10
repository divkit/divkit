declare global {
    interface Window {
        postMessage<T>(message: { pluginMessage: T }, targetOrigin: string, transfer?: Transferable[]): void;
        postMessage<T>(message: { pluginMessage: T }, options?: WindowPostMessageOptions): void;
    }

    interface UIAPI {
        postMessage<T>(message: T, options?: UIPostMessageOptions): void;
    }
}

export {};
