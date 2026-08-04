import { ITemplates, TemplateBlock } from './template';

export type TemplateHelper<U extends ITemplates> = {
    [K in keyof U & string]: U[K] extends TemplateBlock<infer K2>
        ? K2 extends keyof U
            ? (props: unknown) => TemplateBlock<K>
            : unknown
        : (props: unknown) => TemplateBlock<K>;
};

/**
 * Creates typed functions for templates instance construction
 * @param templates templates map of the form { template_name: template }
 * @example
 * const templates = {
 *   template: new DivContainer({
 *     paddings: { left: 3 },
 *     items: [ new DivText({ text: reference('var') }) ]
 *   })
 * };
 * const helpers = templateHelper(templates);
 * helpers.template({var: '123'});
 */
export function templateHelper<T extends ITemplates>(templates: T): TemplateHelper<T> {
    const helpers: Record<string, unknown> = {};

    for (const key of Object.keys(templates)) {
        helpers[key] = (props: object): TemplateBlock<string> => new TemplateBlock(key, props);
    }

    return helpers as TemplateHelper<T>;
}
