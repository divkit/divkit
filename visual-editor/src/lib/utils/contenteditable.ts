const STRING_NODE_TYPE = 3;

function countNodeChars(node: Node, includeImages: boolean): number {
    if (node instanceof HTMLElement && node.tagName === 'IMG') {
        return includeImages ? 1 : 0;
    }

    if (node instanceof HTMLElement && includeImages) {
        return Array.from(node.childNodes).reduce((acc, item) => {
            acc += countNodeChars(item, includeImages);
            return acc;
        }, 0);
    }

    const text = node.textContent;
    let res = 0;

    if (typeof text === 'string') {
        res += text.length;
        if (node instanceof HTMLElement && (node.tagName === 'DIV' || node.tagName === 'BR')) {
            ++res;
        }
    }

    return res;
}

export function calcSelectionOffset(selection: Selection | null, container: HTMLElement, type: 'start' | 'end', includeImages: boolean): number {
    if (!selection) {
        return 0;
    }

    try {
        let res = 0;
        const range = selection.getRangeAt(0);
        let node: Node | undefined;
        if (range) {
            if (type === 'end') {
                res += range.endOffset;
                node = range.endContainer;
            } else {
                res += range.startOffset;
                node = range.startContainer;
            }
        }

        if (node instanceof HTMLElement) {
            let sum = 0;
            for (let i = 0, len = Math.min(res, node.childNodes.length); i < len; ++i) {
                sum += countNodeChars(node.childNodes[i], includeImages);
            }
            res = sum;
        }

        while (node && node !== container) {
            const parent = node.parentNode;
            if (!parent) {
                return 0;
            }
            const index = Array.from(parent.childNodes).indexOf(node as ChildNode);
            for (let i = 0; i < index; ++i) {
                const child = parent.childNodes[i];
                res += countNodeChars(child, includeImages);
            }
            const prev = parent.childNodes[index - 1];
            if (node instanceof HTMLElement &&
                (
                    node.tagName === 'DIV' && (
                        prev?.nodeType === STRING_NODE_TYPE ||
                        prev && prev instanceof HTMLElement && prev.tagName === 'SPAN'
                    ) ||
                    node.tagName === 'BR'
                )
            ) {
                ++res;
            }
            node = parent;
        }

        return res;
    } catch (err) {
        return 0;
    }
}

// eslint-disable-next-line max-params
export function setSelectionOffset(selection: Selection | null, node: Node, range: Range, type: 'start' | 'end', offset: number, includeImages: boolean): void {
    if (node.nodeType === STRING_NODE_TYPE) {
        try {
            if (selection) {
                if (type === 'start') {
                    range.setStart(node, offset);
                } else {
                    range.setEnd(node, offset);
                }
            }
        } catch (err) {}
        return;
    }
    if (node instanceof HTMLElement && (node.tagName === 'BR' || node.tagName === 'IMG')) {
        try {
            if (selection) {
                selection.removeAllRanges();
                const range = document.createRange();
                const parent = node.parentNode as Node;
                const index = Array.from(parent.childNodes).indexOf(node) + 1;
                range.setStart(parent, index);
                range.setEnd(parent, index);
                selection.addRange(range);
            }
        } catch (err) {}
        return;
    }

    for (let i = 0, len = node.childNodes.length; i < len; ++i) {
        const child = node.childNodes[i];
        const count = countNodeChars(child, includeImages);
        if (offset <= count) {
            setSelectionOffset(selection, child, range, type, offset, includeImages);
            return;
        }
        offset -= count;
    }
}

export function getInnerText(target: Node, includeImages: boolean): string {
    if (target.nodeType === STRING_NODE_TYPE) {
        return target.textContent || '';
    }

    if (!(target instanceof HTMLElement)) {
        return '';
    }

    if (target.tagName === 'BR') {
        return '\n';
    }
    if (target.tagName === 'IMG') {
        return includeImages ? '\u0000' : '';
    }

    const childs = target.childNodes;

    if (childs.length === 1 && childs[0] instanceof HTMLElement && childs[0].tagName === 'BR') {
        return '\n';
    }

    let res = '';

    for (let i = 0, len = childs.length; i < len; ++i) {
        const child = childs[i];
        const prevChild = childs[i - 1];
        if (
            child instanceof HTMLElement &&
            child.tagName === 'DIV' &&
            i > 0 &&
            (prevChild.nodeType === STRING_NODE_TYPE || prevChild instanceof HTMLElement && (prevChild.tagName === 'A' || prevChild.tagName === 'SPAN'))
        ) {
            res += '\n';
        }
        res += getInnerText(child, includeImages);
    }

    return res;
}
