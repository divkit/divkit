import type { TreeLeaf } from '../ctx/tree';
import { stringifyWithLoc } from '../utils/stringifyWithLoc';

export const EMPTY_IMAGE = 'empty://';
export const DIVKIT_EMPY_IMAGE = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7';
export const CHESS_EMPTY_IMAGE = 'https://yastatic.net/s3/home/divkit/empty2.png';

export function stringifyObjectAndStoreRanges({
    object,
    mapJsonToLeaf
}: {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    object: any;
    mapJsonToLeaf: Map<unknown, TreeLeaf>;
}): string {
    return stringifyWithLoc(object, (key, val) => {
        if (key === '__leafId' || key === '__key') {
            return undefined;
        }
        return val;
    }, 2, (value, range) => {
        const leaf = mapJsonToLeaf.get(value);

        if (leaf) {
            leaf.props.range = range;
        }
    });
}

export function appendChild(parent: TreeLeaf, child: TreeLeaf): void {
    let insertIndex = parent.childs.length;
    // todo
    while (insertIndex >= 1 && parent.childs[insertIndex - 1].props?.json?.type === '_template_close') {
        --insertIndex;
    }
    parent.childs = parent.childs.slice();
    parent.childs.splice(insertIndex, 0, child);
}
