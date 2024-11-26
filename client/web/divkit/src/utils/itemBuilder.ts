import type { RootCtxValue } from '../context/root';
import type { MaybeMissing } from '../expressions/json';
import type { Variable } from '../expressions/variable';
import type { DivBaseData } from '../types/base';
import type { ComponentContext } from '../types/componentContext';
import type { CollectionItemBuilder } from '../types/itemBuilder';

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
                    vars: additionalVars
                });
            }
        });
    }

    return items;
}
