export function getCroppedBbox(node: HTMLElement): DOMRect {
    const initialBbox = node.getBoundingClientRect();
    const box = {
        top: initialBbox.top,
        right: initialBbox.right,
        bottom: initialBbox.bottom,
        left: initialBbox.left
    };
    let current = node.parentElement;

    while (current && current !== document.documentElement) {
        const computed = getComputedStyle(current);

        if (computed.overflow !== 'visible') {
            const parentBbox = current.getBoundingClientRect();
            box.left = Math.max(box.left, parentBbox.left);
            box.right = Math.min(box.right, parentBbox.right);
            box.top = Math.max(box.top, parentBbox.top);
            box.bottom = Math.min(box.bottom, parentBbox.bottom);
        }

        current = current.parentElement;
    }

    return DOMRect.fromRect({
        x: box.left,
        y: box.top,
        width: box.right - box.left,
        height: box.bottom - box.top
    });
}
