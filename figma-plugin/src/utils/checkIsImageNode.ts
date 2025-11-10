export const checkIsImageNode = (node: BaseNode): boolean => {
    if (node.getPluginData('type') === 'image') {
        return true;
    }

    if (node.type === 'VECTOR') {
        return true;
    }

    if (node.type === 'FRAME' || node.type === 'RECTANGLE') {
        if ((node.fills as Paint[]).find(paint => paint.type === 'IMAGE') !== undefined) {
            return true;
        }
    }

    return false;
};
