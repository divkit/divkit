export const ACTION_CTX = Symbol('action');

export interface ActionCtxValue {
    hasAction(): boolean;
}
