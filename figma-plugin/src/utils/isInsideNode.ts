export const isInsideNode = (part: BaseNode, rootNode: BaseNode): boolean => {
    const parent = part.parent;
    if (!parent) {
        return false;
    }

    if (parent === rootNode) {
        return true;
    } else if (parent.type === 'PAGE') {
        return false;
    }

    return isInsideNode(parent, rootNode);
};
