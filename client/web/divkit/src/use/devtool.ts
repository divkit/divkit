import type { RootCtxValue } from '../context/root';
import type { DivBase, TemplateContext } from '../../typings/common';
import type { DivBaseData } from '../types/base';

export interface DevtoolResult {
    update({
        json,
        origJson,
        templateContext
    }: {
        json: Partial<DivBaseData>;
        origJson?: DivBase | undefined;
        templateContext: TemplateContext;
    }): void;
    destroy(): void;
}

function devtoolReal(node: HTMLElement, {
    json,
    origJson,
    templateContext,
    rootCtx
}: {
    json: Partial<DivBaseData>;
    origJson?: DivBase | undefined;
    templateContext: TemplateContext;
    rootCtx: RootCtxValue;
}): DevtoolResult {
    rootCtx.componentDevtool?.({
        type: 'mount',
        node,
        json,
        origJson,
        templateContext
    });

    return {
        update({
            json,
            origJson,
            templateContext
        }: {
            json: Partial<DivBaseData>;
            origJson?: DivBase | undefined;
            templateContext: TemplateContext;
        }) {
            rootCtx.componentDevtool?.({
                type: 'update',
                node,
                json,
                origJson,
                templateContext
            });
        },
        destroy() {
            rootCtx.componentDevtool?.({
                type: 'destroy',
                node,
                json,
                origJson,
                templateContext
            });
        }
    };
}

export const devtool = process.env.DEVTOOL ? devtoolReal : undefined;
