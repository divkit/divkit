import type { NodeWithChildren } from '../types/common';

export const shouldSkipContainer = (node: NodeWithChildren): boolean => (
    node.height === 0 || node.width === 0 || node.type === 'GROUP'
);
