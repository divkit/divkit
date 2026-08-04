import { copyTemplates } from './helper';
import { ITemplates, TemplatePropertyReference } from './template';

function rewriteRefsTemplate(template: unknown): void {
    const stack: unknown[] = [template];

    while (stack.length > 0) {
        const obj = stack.pop();

        if (obj && typeof obj === 'object') {
            const castedObj = obj as Record<string, unknown>;
            for (const key in castedObj) {
                const objChild = castedObj[key];
                if (objChild && typeof objChild === 'object') {
                    if (objChild instanceof TemplatePropertyReference) {
                        castedObj[key] = undefined;

                        if (objChild.templatePropertyName !== key) {
                            castedObj['$' + key] = objChild.templatePropertyName;
                        }
                    } else {
                        stack.push(objChild);
                    }
                }
            }
        }
    }
}

/**
 * Changes the reference-objects with the actual props
 *
 * Example:
 * prop: reference('template_prop')
 * to
 * $prop: 'template_prop'
 * @param templates
 * @returns Serialization-ready templates
 */
export function rewriteRefs<T extends ITemplates>(templates: T): T {
    const result = copyTemplates(templates);

    for (const template of Object.values(result)) {
        rewriteRefsTemplate(template);
    }

    return result;
}
