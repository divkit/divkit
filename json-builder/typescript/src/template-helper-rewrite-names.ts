import { copyTemplates, treeWalkDFS } from './helper';
import { ITemplates, TemplateBlock } from './template';
import { runResolveDeps } from './template-helper-deps';

/**
 * Templates rewriting for a nested templates
 * For example, in template
 * new DivContainer({
 *   items: [
 *       template('template2')
 *   ]
 * })
 * 'template2' would be replaces into `template2/${hash(templates.template2)}`
 * @param templates Templates to process
 * @param rename New name generator
 * @param resolvedNames Rename cache
 * @returns Processed templates + map with transformed names
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
                if (node instanceof TemplateBlock) {
                    (node as any).type = depsResolved[node.type];
                }
            });

            return rename(name, templates);
        },
        resolvedNames,
    );

    return { templates, resolvedNames };
}
