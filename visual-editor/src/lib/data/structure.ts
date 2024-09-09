import type { TreeLeaf } from '../ctx/tree';
import { isSimpleElement } from './schema';
import { walk } from '../utils/tree';

export interface ComponentProps {
    json: {
        type: string;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        [key: string]: any;
    };
    processedJson: {
        type: string;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        [key: string]: any;
    };
    node: HTMLElement;
}

export type HighlightMode = '' | 'margins' | 'paddings';

export function getDepth(elem: HTMLElement): number {
    let depth = 0;
    let elem2 = elem;
    while (elem2.parentElement) {
        ++depth;
        elem2 = elem2.parentElement;
    }
    return depth;
}

export function getLeafDepth(leaf: TreeLeaf): number {
    let temp: TreeLeaf | undefined = leaf;
    let count = 0;

    while (temp) {
        temp = temp.parent;
        ++count;
    }

    return count;
}

export function calcComponentTree(tree: TreeLeaf) {
    const treeMap = new Map<HTMLElement, string>();
    const simpleMap = new Map<HTMLElement, TreeLeaf>();

    walk(tree, leaf => {
        if (leaf.props.node) {
            treeMap.set(leaf.props.node, leaf.id);
            if (isSimpleElement(leaf.props.json.type)) {
                simpleMap.set(leaf.props.node, leaf);
            }
        }
    });

    return {
        map: treeMap,
        simpleMap
    };
}
