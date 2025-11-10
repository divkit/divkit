export const getChildren = (
    root: BaseNode,
    condition: (node: BaseNode) => boolean = () => true,
    recursive = true
): BaseNode[] => {
    const result: BaseNode[] = [];

    function traverse(node: BaseNode) {
        if (!isVisible(node)) {
            return;
        }

        if (condition(node)) {
            result.push(node);

            if (!recursive) {
                return;
            }
        }

        if ('children' in node) {
            for (const child of node.children) {
                traverse(child);
            }
        }
    }

    if ('children' in root) {
        for (const node of root.children) {
            traverse(node);
        }
    }

    return result;
};

const isVisible = (node: BaseNode): boolean => 'visible' in node && node.visible;
