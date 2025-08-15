import type { ComponentContext } from '../types/componentContext';

export function getTooltipContext(componentContext: ComponentContext | undefined): ComponentContext | undefined {
    let ctx = componentContext;
    while (ctx && !ctx.isTooltipRoot) {
        ctx = ctx.parent;
    }
    return ctx;
}

export function getStateContext(componentContext: ComponentContext | undefined): ComponentContext | undefined {
    let ctx = componentContext;
    while (ctx?.parent && ctx.json.type !== 'state' && !ctx.isRootState && !ctx.isTooltipRoot) {
        ctx = ctx.parent;
    }
    return ctx;
}
