import { Div } from './generated/Div';
import { treeWalkDFS } from './helper';
import { ITemplates, TemplateBlock } from './template';

/**
 * Search for a used templates list
 * @param template
 */
function findPlainDeps(template: Div): Set<string> {
    const deps = new Set<string>();

    treeWalkDFS(template, (node) => {
        if (node instanceof TemplateBlock) {
            deps.add(node.type);
        }
    });

    return deps;
}

const inProgress = 1;
const isFinished = 2;

export type TemplateResolvedAction<T> = (props: {
    name: string;
    template: Div;
    plainDeps: string[];
    depsResolved: { [k: string]: T };
}) => T;

/**
 * Templates walker. It resolves template dependencies on each step, and also can run a callback after that
 * @param templates
 * @param resolvedAction Callback for the template. It will be called after resolving dependencies
 * @param depsResolved Deps resolve cache, for example, for common templates
 */
export function runResolveDeps<T>(
    templates: ITemplates,
    resolvedAction: TemplateResolvedAction<T>,
    depsResolved: { [k: string]: T } = {},
): { [k: string]: T } {
    depsResolved = { ...depsResolved };
    const progress: { [k: string]: undefined | typeof inProgress | typeof isFinished } = {};

    for (const k of Object.keys(depsResolved)) {
        progress[k] = isFinished;
    }

    const stack = Object.keys(templates);

    while (stack.length) {
        const name = stack.pop() as string;

        if (progress[name] === isFinished) {
            continue;
        }

        const plainDeps = [...findPlainDeps(templates[name])];
        const unresolvedDeps = plainDeps.filter((dep) => !(progress[dep] === isFinished));
        const unsolvableDeps = unresolvedDeps.filter((depName) => !templates[depName]);

        if (unsolvableDeps.length !== 0) {
            throw new Error(
                `template '${name}' unsolvable dependencies: ${unsolvableDeps.map((b) => `'${b}'`).join(',')}`,
            );
        }

        if (unresolvedDeps.length === 0) {
            const template = templates[name];
            depsResolved[name] = resolvedAction({ name, template, plainDeps, depsResolved });
            progress[name] = isFinished;
        } else if (progress[name] === inProgress) {
            throw new Error(`template ${name}: cyclic depdendencies`);
        } else {
            progress[name] = inProgress;
            stack.push(name, ...unresolvedDeps);
        }
    }

    return depsResolved as { [k: string]: T };
}

/**
 * Search for a templates dependencies
 * @param templates
 * @param depsResolved Deps resolution cache
 * @returns Map with deps for each template
 */
export function templatesDepsMap(
    templates: ITemplates,
    depsResolved: { [k: string]: string[] } = {},
): { [k: string]: string[] } {
    return runResolveDeps(
        templates,
        ({ name, plainDeps, depsResolved }) => {
            const deps = new Set([name]);

            for (const plainDep of plainDeps) {
                for (const dep of depsResolved[plainDep]) {
                    deps.add(dep);
                }
            }

            return [...deps];
        },
        depsResolved,
    );
}
