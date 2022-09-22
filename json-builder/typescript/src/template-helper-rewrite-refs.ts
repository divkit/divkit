import { copyTemplates } from './helper';
import { ITemplates, TemplatePropertyReference } from './template';

function rewriteRefsTemplate(template: unknown): void {
    const stack: unknown[] = [template];

    while (stack.length > 0) {
        const obj = stack.pop();

        if (obj && typeof obj === 'object') {
            for (const key in obj) {
                const objChild = obj[key];
                if (objChild && typeof objChild === 'object') {
                    if (objChild instanceof TemplatePropertyReference) {
                        obj[key] = undefined; // delete ломает валидатор

                        if (objChild.templatePropertyName !== key) {
                            obj['$' + key] = objChild.templatePropertyName;
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
 * Заменяет  в шаблонах замену параметры вида
 * prop: reference('template_prop')
 * на
 * $prop: 'template_prop'
 * @param templates Шаблоны
 * @returns Шаблоны, готовые для сериализации в формате DivKit
 */
export function rewriteRefs<T extends ITemplates>(templates: T): T {
    const result = copyTemplates(templates);

    for (const template of Object.values(result)) {
        rewriteRefsTemplate(template);
    }

    return result;
}
