const STRING_NODE_TYPE = 3;

function countNodeChars(node: Node): number {
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

export function calcSelectionOffset(container: HTMLElement, type: 'start' | 'end'): number {
    try {
        let res = 0;
        const sel = window.getSelection();
        if (!sel) {
            return 0;
        }
        const range = sel.getRangeAt(0);
        let node: Node | undefined;
        if (range) {
            res += type === 'end' ? range.endOffset : range.startOffset;
            node = type === 'end' ? range.endContainer : range.startContainer;
        }

        if (node instanceof HTMLElement) {
            let sum = 0;
            for (let i = 0, len = Math.min(res, node.childNodes.length); i < len; ++i) {
                sum += countNodeChars(node.childNodes[i]);
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
                res += countNodeChars(child);
            }
            if (node instanceof HTMLElement &&
                (
                    node.tagName === 'DIV' && parent.childNodes[index - 1]?.nodeType === STRING_NODE_TYPE ||
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

export function setSelectionOffset(node: Node, range: Range, type: 'start' | 'end', offset: number): void {
    if (node.nodeType === STRING_NODE_TYPE) {
        try {
            if (type === 'start') {
                range.setStart(node, offset);
            } else {
                range.setEnd(node, offset);
            }
        } catch (err) {}
        return;
    }
    if (node instanceof HTMLElement && node.tagName === 'BR') {
        try {
            const sel = window.getSelection();
            if (sel) {
                sel.removeAllRanges();
                const range = document.createRange();
                const parent = node.parentNode as Node;
                const index = Array.from(parent.childNodes).indexOf(node) + 1;
                range.setStart(parent, index);
                range.setEnd(parent, index);
                sel.addRange(range);
            }
        } catch (err) {}
        return;
    }

    for (let i = 0, len = node.childNodes.length; i < len; ++i) {
        const child = node.childNodes[i];
        const count = countNodeChars(child);
        if (offset <= count) {
            setSelectionOffset(child, range, type, offset);
            return;
        }
        offset -= count;
    }
}
