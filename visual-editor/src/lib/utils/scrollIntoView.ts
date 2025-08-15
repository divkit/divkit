export function scrollIntoView(elem: HTMLElement, offsets: {
    top?: number;
    bottom?: number;
} = {}, {
    local
}: {
    local?: boolean;
} = {}) {
    const bbox = elem.getBoundingClientRect();

    if (local) {
        let scrollElem: HTMLElement | null = null;
        let cur = elem.parentElement;
        while (cur) {
            if (getComputedStyle(cur).overflow !== 'visible') {
                scrollElem = cur;
                break;
            }
            cur = cur.parentElement;
        }
        if (scrollElem) {
            const scrollBbox = scrollElem.getBoundingClientRect();
            const scrollTop = scrollElem.scrollTop;
            const offsetTop = offsets.top || 0;
            const offsetBottom = offsets.bottom || 0;

            if (bbox.bottom > scrollBbox.bottom - offsetBottom) {
                scrollElem.scrollTo(
                    scrollElem.scrollLeft,
                    scrollTop + (bbox.bottom - (scrollBbox.bottom - offsetBottom))
                );
            } else if (bbox.top < scrollBbox.top + offsetTop) {
                scrollElem.scrollTo(scrollElem.scrollLeft, scrollTop - (scrollBbox.top + offsetTop - bbox.top));
            }
        }
    } else {
        const scrollTop = window.pageYOffset;
        const windowHeight = window.innerHeight;
        const offsetTop = offsets.top || 0;
        const offsetBottom = offsets.bottom || 0;

        if (bbox.bottom > windowHeight - offsetBottom) {
            window.scrollTo(pageXOffset, scrollTop + (bbox.bottom - (windowHeight - offsetBottom)));
        } else if (bbox.top < offsetTop) {
            window.scrollTo(pageXOffset, scrollTop - (offsetTop - bbox.top));
        }
    }
}
