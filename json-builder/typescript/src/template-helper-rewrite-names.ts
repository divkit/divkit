import { copyTemplates, treeWalkDFS } from './helper';
import { ITemplates, TemplateBlock } from './template';
import { isExternalTemplate, runResolveDeps } from './template-helper-deps';

/**
 * Переписывание шаблонов с заменой имен вложенных шаблонов
 * Например, в шаблоне
 * new DivContainer({
 *   items: [
 *       template('template2')
 *   ]
 * })
 * 'template2' будет заменен на `template2/${hash(templates.template2)}`
 * @param templates шаблоны для перезаписи
 * @param rename функция, возвращающая новое имя
 * @param depsResolved ранее вычисленные имена, например общих шаблонов
 * @returns переписанные шаблоны + хэшмап новых имен
 */
export function rewriteNames<T extends ITemplates>(
    templates: T,
    rename: (name: string, templates: T) => string,
    resolvedNames: { [k: string]: string } = {},
): {
    templates: T;
    resolvedNames: { [k: string]: string };
} {
    templates = copyTemplates(templates);

    resolvedNames = runResolveDeps(
        templates,
        ({ name, template, depsResolved }) => {
            treeWalkDFS(template, (node) => {
                if (node instanceof TemplateBlock && !isExternalTemplate(node.type)) {
                    (node as any).type = depsResolved[node.type];
                }
            });

            return rename(name, templates);
        },
        resolvedNames,
    );

    return { templates, resolvedNames };
}
