export interface Box {
    top: number;
    right: number;
    bottom: number;
    left: number;
}

export function getMarginBox(elem: HTMLElement): Box {
    const bbox = elem.getBoundingClientRect();
    const computed = getComputedStyle(elem);

    return {
        top: bbox.top - parseFloat(computed.marginTop),
        right: bbox.right + parseFloat(computed.marginRight),
        bottom: bbox.bottom + parseFloat(computed.marginBottom),
        left: bbox.left - parseFloat(computed.marginLeft)
    };
}
