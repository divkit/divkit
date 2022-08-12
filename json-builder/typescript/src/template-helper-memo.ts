import { ITemplates, TemplateBlock } from './template';
import { TemplateHelper } from './template-helper';

export interface ThelperWithMemo<T extends ITemplates> {
    thelper: TemplateHelper<T>;
    used: Set<string>;
}

/**
 * templateHelper with memoization of templates names for later
 * selection of used templates. It memoizes only explicitly used
 * names without tracking dependencies.
 * @param options
 * @param options.customName adds templates names mapping.
 * Instead of original template name adjust template's type to custom value
 * returned by customName. Can be used for templates versioning.
 */
export function thelperWithMemo<T extends ITemplates>(options?: {
    customName?: (x: string) => string;
}): ThelperWithMemo<T> {
    const used = new Set<string>();

    const proxyHandler: ProxyHandler<{ [k: string]: (o: unknown) => TemplateBlock<string> }> = {
        get(_, templateName: string) {
            const templateRenamed = options?.customName ? options.customName(templateName) : templateName;
            used.add(templateName);

            return (props: any) => ({ ...props, type: templateRenamed });
        },
    };

    const thelper = new Proxy({}, proxyHandler) as TemplateHelper<T>;

    return { used, thelper };
}
