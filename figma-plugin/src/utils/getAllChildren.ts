export const getAllChildren = (
    node: BaseNode,
    condition: (n: BaseNode) => boolean = () => true
): BaseNode[] => {
    const result = [];

    if ('children' in node && condition(node)) {
        for (const child of node.children) {
            if (condition(child)) {
                result.push(child);
            }
            result.push(...getAllChildren(child, condition));
        }
    }

    return result;
};
