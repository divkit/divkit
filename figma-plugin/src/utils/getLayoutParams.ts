import type { LayoutParams } from '../types/divkit';
import type { NodeWithLayoutParams } from '../types/common';

export const supportsLayoutParams = (node: BaseNode): node is NodeWithLayoutParams => (
    node.type === 'FRAME' ||
    node.type === 'COMPONENT' ||
    node.type === 'GROUP' ||
    node.type === 'INSTANCE' ||
    node.type === 'TEXT' ||
    node.type === 'RECTANGLE'
);

export const getLayoutParams = (node: NodeWithLayoutParams): LayoutParams => {
    const layoutParams: LayoutParams = {};
    let parentNode = node.parent as FrameNode | InstanceNode | GroupNode | null;

    while (parentNode && parentNode.type === 'GROUP') {
        parentNode = parentNode.parent as FrameNode | InstanceNode | GroupNode | null;
    }

    if (!parentNode) {
        return layoutParams;
    }

    const margins: {
        top?: number;
        left?: number;
        bottom?: number;
        right?: number;
    } = {};

    if (node.layoutPositioning === 'ABSOLUTE' || parentNode.layoutMode === 'NONE') {
        if (node.x < parentNode.width / 2 - node.width / 2) {
            layoutParams.alignment_horizontal = 'left';
            margins.left = Math.round(node.x);
        } else if (node.x > parentNode.width / 2 - node.width / 2) {
            layoutParams.alignment_horizontal = 'right';
            margins.right = Math.round(parentNode.width - node.width - node.x);
        } else {
            layoutParams.alignment_horizontal = 'center';
            margins.left = Math.round(node.x);
            margins.right = Math.round(parentNode.width - node.width - node.x);
        }

        if (node.y < parentNode.height / 2 - node.height / 2) {
            layoutParams.alignment_vertical = 'top';
            margins.top = Math.round(node.y);
        } else if (node.y > parentNode.height / 2 - node.height / 2) {
            layoutParams.alignment_vertical = 'bottom';
            margins.bottom = Math.round(parentNode.height - node.height - node.y);
        } else {
            layoutParams.alignment_vertical = 'center';
            margins.top = Math.round(node.y);
            margins.bottom = Math.round(parentNode.height - node.height - node.y);
        }

        if ('constraints' in node && node.constraints.horizontal === 'STRETCH') {
            layoutParams.width = { type: 'match_parent' };
        }

        if ('constraints' in node && node.constraints.vertical === 'STRETCH') {
            layoutParams.height = { type: 'match_parent' };
        }
    } else if (parentNode.layoutMode === 'HORIZONTAL') {
        if (node.layoutGrow === 1) {
            layoutParams.width = { type: 'match_parent' };
        }

        if (node.layoutAlign === 'STRETCH') {
            layoutParams.height = { type: 'match_parent' };
        }
    } else if (parentNode.layoutMode === 'VERTICAL') {
        if (node.layoutGrow === 1) {
            layoutParams.height = { type: 'match_parent' };
        }

        if (node.layoutAlign === 'STRETCH') {
            layoutParams.width = { type: 'match_parent' };
        }
    }

    for (const [k, v] of Object.entries(margins)) {
        if (v) {
            margins[k as keyof typeof margins] = Math.max(v, 0);
        }
    }

    if (Object.keys(margins).length >= 1) {
        layoutParams.margins = margins;
    }

    if (node.opacity < 1) {
        layoutParams.alpha = Number(node.opacity.toFixed(2));
    }

    return layoutParams;
};
