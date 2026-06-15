import type { RootCtxValue } from '../context/root';
import type { MaybeMissing } from '../expressions/json';
import type { Variable } from '../expressions/variable';
import type { DivBaseData } from '../types/base';
import type { ComponentContext, ComponentKey } from '../types/componentContext';
import type { CollectionItemBuilder } from '../types/itemBuilder';
import type { ImageBuilder, RangeBuilder, TextImage, TextRange } from '../types/text';

export function getItemsFromItemBuilder(
    data: MaybeMissing<object[]>,
    rootCtx: RootCtxValue,
    componentContext: ComponentContext,
    builder: MaybeMissing<CollectionItemBuilder>
) {
    const items: {
        div: MaybeMissing<DivBaseData>;
        id?: string | undefined;
        vars?: Map<string, Variable> | undefined;
        key: ComponentKey;
    }[] = [];
    const prototypes = builder.prototypes;

    if (prototypes) {
        data.forEach((it, index) => {
            if (it === null || typeof it !== 'object') {
                return;
            }
            const additionalVars = rootCtx.preparePrototypeVariables(builder.data_element_name || 'it', it as Record<string, unknown>, index);

            let div: MaybeMissing<DivBaseData> | undefined;
            let id: string | undefined;
            for (let i = 0; i < prototypes.length; ++i) {
                const prototype = prototypes[i];
                if (!prototype.div) {
                    continue;
                }
                if (prototype.selector === undefined) {
                    div = prototype.div;
                    id = componentContext.getJsonWithVars(prototype.id, additionalVars);
                    break;
                }

                const selectorVal = componentContext.getJsonWithVars(prototype.selector, additionalVars);
                if (selectorVal) {
                    div = prototype.div;
                    id = componentContext.getJsonWithVars(prototype.id, additionalVars);
                    break;
                }
            }

            if (div) {
                items.push({
                    div,
                    id,
                    vars: additionalVars,
                    key: id || { index, data: it }
                });
            }
        });
    }

    return items;
}

export function getElementsFromItemBuilder<B extends RangeBuilder | ImageBuilder, T extends TextRange | TextImage>(
    data: MaybeMissing<object[]>,
    rootCtx: RootCtxValue,
    componentContext: ComponentContext,
    builder: MaybeMissing<B>,
    field: string
) {
    const items: MaybeMissing<T>[] = [];
    const prototypes = builder.prototypes;

    if (prototypes) {
        data.forEach((it, index) => {
            if (it === null || typeof it !== 'object') {
                return;
            }
            const additionalVars = rootCtx.preparePrototypeVariables(builder.data_element_name || 'it', it as Record<string, unknown>, index);

            let element: MaybeMissing<T> | undefined;
            for (let i = 0; i < prototypes.length; ++i) {
                const prototype = prototypes[i];
                const item = prototype[field as keyof typeof prototype] as T;
                if (!item) {
                    continue;
                }
                if (prototype.selector === undefined) {
                    element = item;
                    break;
                }

                const selectorVal = componentContext.getJsonWithVars(prototype.selector, additionalVars);
                if (selectorVal) {
                    element = item;
                    break;
                }
            }

            if (element) {
                items.push(componentContext.getJsonWithVars(element, additionalVars) as T);
            }
        });
    }

    return items;
}
