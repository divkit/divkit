import { writable } from 'svelte/store';
import type { TreeLeaf } from '../ctx/tree';

export interface ComponentProps {
    json: {
        type: string;
    };
    origJson: unknown;
    templateContext: unknown;
}

function getDepth(elem: HTMLElement): number {
    let depth = 0;
    let elem2 = elem;
    while (elem2.parentElement) {
        ++depth;
        elem2 = elem2.parentElement;
    }
    return depth;
}

function getClosestParent(elem: HTMLElement, map: Map<HTMLElement, TreeLeaf>) {
    let elem2 = elem;
    do {
        elem2 = elem2.parentElement as HTMLElement;
    } while (elem2 && !map.has(elem2));

    return map.get(elem2);
}

function calcComponentTree(components: Map<HTMLElement, ComponentProps>) {
    if (!components.size) {
        return {
            tree: null,
            map: null
        };
    }

    let list = [];
    for (const [node, props] of components) {
        list.push({
            depth: getDepth(node),
            node,
            props
        });
    }

    list = list.sort((a, b) => a.depth - b.depth);

    const treeMap = new Map<HTMLElement, TreeLeaf>();
    const root = list[0].node;
    const tree: TreeLeaf = {
        props: {
            ...list[0].props,
            node: root
        },
        childs: [],
        id: 'root'
    };
    treeMap.set(root, tree);

    const counters: Record<string, {
        [type: string]: number;
    }> = {};

    for (const item of list.slice(1)) {
        const parent = getClosestParent(item.node, treeMap);
        if (parent) {
            counters[parent.id] = counters[parent.id] || {};
            counters[parent.id][item.props.json.type] = counters[parent.id][item.props.json.type] || 0;

            const treeChild: TreeLeaf = {
                parent,
                props: {
                    ...item.props,
                    node: item.node
                },
                childs: [],
                id: parent.id + '/' + item.props.json.type + ':' + (counters[parent.id][item.props.json.type]++)
            };

            parent.childs.push(treeChild);
            treeMap.set(item.node, treeChild);
        }
    }

    return {
        tree,
        map: treeMap
    };
}

let scheduled = false;
function scheduleUpdate(func: () => void) {
    if (scheduled) {
        return;
    }
    scheduled = true;
    Promise.resolve().then(() => {
        scheduled = false;
        func();
    });
}

export const webStructure = writable<TreeLeaf | null>(null);
export let webStructureMap: Map<HTMLElement, TreeLeaf> | null = null;
export const highlightElem = writable<HTMLElement | null>(null);
export const highlightMode = writable<boolean>(false);
export const highlightModeClicked = writable<boolean>(false);
export const highlightPart = writable<number>(1 | 2 | 4);
export const selectedElem = writable<HTMLElement | null>(null);
export const selectedLeaf = writable<TreeLeaf | null>(null);

export const components = new Map<HTMLElement, ComponentProps>();
export const updateComponents = () => {
    scheduleUpdate(() => {
        const res = calcComponentTree(components);
        const tree = res.tree;
        webStructureMap = res.map;
        webStructure.set(tree);
        highlightElem.set(null);
        selectedElem.set(null);
        selectedLeaf.set(null);
    });
}
