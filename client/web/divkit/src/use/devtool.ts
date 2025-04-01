import type { RootCtxValue } from '../context/root';
import type { ComponentContext } from '../types/componentContext';

export interface DevtoolResult {
    update(componentContext: ComponentContext): void;
    destroy(): void;
}

function devtoolReal(
    node: HTMLElement | null,
    rootCtx: RootCtxValue,
    componentContext: ComponentContext
): DevtoolResult {
    rootCtx.componentDevtool?.({
        type: 'mount',
        node,
        json: componentContext.json,
        origJson: componentContext.origJson,
        templateContext: componentContext.templateContext,
        componentContext,
    });

    return {
        update(componentContext) {
            rootCtx.componentDevtool?.({
                type: 'update',
                node,
                json: componentContext.json,
                origJson: componentContext.origJson,
                templateContext: componentContext.templateContext,
                componentContext
            });
        },
        destroy() {
            rootCtx.componentDevtool?.({
                type: 'destroy',
                node,
                json: componentContext.json,
                origJson: componentContext.origJson,
                templateContext: componentContext.templateContext,
                componentContext
            });
        }
    };
}

export const devtool = process.env.DEVTOOL ? devtoolReal : undefined;
