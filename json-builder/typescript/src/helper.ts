import { ITemplates } from './template';

export type NonEmptyArray<T> = T[];

export type Exact<TBase, TExt extends TBase> = TExt extends unknown ? Exactly<TBase, TExt> : never;

type Exactly<TBase, TExt extends TBase> = {
    [K in keyof TExt]: K extends keyof TBase ? TExt[K] : never;
};

export type IntBoolean = 1 | 0;

/**
 * DFS-Обход js-объекта как дерева с выполнением в каждом
 * узле заданного действия
 * @param tree js-объект
 * @param nodeAction действие, выполняемое для каждого узла дерева
 */
export function treeWalkDFS(tree: unknown, nodeAction: (x: unknown, path: string[]) => void): void {
    const stack: [unknown, string[]][] = [[tree, []]];

    while (stack.length) {
        const [node, path] = stack.shift() as [unknown, string[]];
        nodeAction(node, path);

        if (typeof node === 'object' && node !== null) {
            stack.unshift(
                ...Object.entries(node as { [k: string]: unknown }).map(
                    ([key, obj]) => [obj, [...path, key]] as [unknown, string[]],
                ),
            );
        }
    }
}

export function copyTemplates<T extends ITemplates>(templates: T): T {
    const copy: ITemplates = {};

    const placeHolder = (node: unknown): unknown => {
        if (!node || ['string', 'number'].includes(typeof node)) {
            return node;
        }

        if (Array.isArray(node)) {
            return [];
        }

        return Object.create(Object.getPrototypeOf(node));
    };

    const clone = (node: unknown, path: string[]): void => {
        let parentPointer: object = copy;

        path = [...path];
        while (path.length > 1) {
            parentPointer = parentPointer[path.shift() as string];
        }

        const cur = path[0];

        if (!cur) {
            return;
        }

        parentPointer[cur] = placeHolder(node);
    };

    treeWalkDFS(templates, clone);

    return copy as T;
}
