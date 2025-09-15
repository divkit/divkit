import type { TreeLeaf } from '../ctx/tree';

export function treeLeafContains(leafA: TreeLeaf, leafB: TreeLeaf): boolean {
    if (leafA === leafB) {
        return true;
    }

    for (const child of leafA.childs) {
        if (treeLeafContains(child, leafB)) {
            return true;
        }
    }

    return false;
}

export function walk(root: TreeLeaf, cb: (leaf: TreeLeaf) => void): void {
    const queue = [root];

    while (queue.length) {
        const item = queue.shift() as TreeLeaf;
        cb(item);
        queue.push(...item.childs);
    }
}

export function findLeaf(root: TreeLeaf, childId: string): TreeLeaf | undefined {
    let res: TreeLeaf | undefined;

    walk(root, leaf => {
        if (leaf.id === childId) {
            res = leaf;
        }
    });

    return res;
}

export function structuredCopyLeaf(leaf: TreeLeaf, parent?: TreeLeaf): TreeLeaf {
    const res: TreeLeaf = {
        ...leaf,
        props: {
            info: leaf.props.info,
            json: {
                ...leaf.props.json,
                items: undefined
            }
        },
        parent: parent ? parent : undefined
    };

    res.childs = leaf.childs.map(child => structuredCopyLeaf(child, res));

    return res;
}

export interface RemoveLeafData {
    parentId: string;
    insertIndex: number;
    leaf: TreeLeaf
}

export function cascadeRemoveLeaf(root: TreeLeaf, leafId: string, cascade = true): RemoveLeafData[] {
    const leaf = findLeaf(root, leafId);
    if (!leaf || !leaf.parent) {
        return [];
    }
    let data: RemoveLeafData[] = [{
        insertIndex: leaf.parent.childs.indexOf(leaf),
        parentId: leaf.parent.id,
        leaf: structuredCopyLeaf(leaf)
    }];
    leaf.parent.childs = leaf.parent.childs.filter(it => it.id !== leafId);

    if (cascade && !leaf.parent.childs.length && leaf.parent?.parent) {
        data = [...data, ...cascadeRemoveLeaf(root, leaf.parent.id)];
    }

    return data;
}
