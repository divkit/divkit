import { ITemplates } from './template';
import { SafeDivExpression } from './safe-expression';

export type NonEmptyArray<T> = T[];

export type Exact<TBase, TExt extends TBase> = TExt extends unknown ? Exactly<TBase, TExt> : never;

type Exactly<TBase, TExt extends TBase> = {
    [K in keyof TExt]: K extends keyof TBase ? TExt[K] : never;
};

export type IntBoolean = 1 | 0 | true | false;

/**
 * DFS for a js object with callback on each leaf
 * @param tree js object
 * @param nodeAction callback for each leaf
 */
export function treeWalkDFS(tree: unknown, nodeAction: (x: unknown, path: string[]) => void): void {
    const stack: [unknown, string[]][] = [[tree, []]];

    while (stack.length) {
        const [node, path] = stack.pop() as [unknown, string[]];
        nodeAction(node, path);

        if (typeof node === 'object' && node !== null) {
            const list = Object.entries(node as { [k: string]: unknown }).map(
                ([key, obj]) => [obj, [...path, key]] as [unknown, string[]],
            );

            for (let i = list.length - 1; i >= 0; --i) {
                stack.push(list[i]);
            }
        }
    }
}

export function copyTemplates<T extends ITemplates>(templates: T): T {
    const copy: ITemplates = {};

    const placeHolder = (node: unknown): unknown => {
        if (!node || ['string', 'number', 'boolean'].includes(typeof node) || node instanceof SafeDivExpression) {
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
