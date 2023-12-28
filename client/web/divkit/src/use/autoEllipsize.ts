import type { BooleanInt } from '../../typings/common';
import { debounce } from '../utils/debounce';

export interface AutoEllipsizeOptions {
    enabled: BooleanInt | undefined;
    lineClamp: number | undefined;
}

export function autoEllipsize(node: HTMLElement, opts: AutoEllipsizeOptions) {
    let resizeObserver: ResizeObserver | null = null;

    const recalc = () => {
        node.style.webkitLineClamp = '';
        node.style.maxHeight = '';

        const offsetHeight = node.offsetHeight;
        const scrollHeight = node.scrollHeight;
        const lineHeight = parseFloat(getComputedStyle(node).lineHeight);

        if (scrollHeight <= offsetHeight + 1e-9) {
            return;
        }

        const lines = Math.max(1, Math.floor(offsetHeight / lineHeight));

        node.style.webkitLineClamp = String(lines);
        node.style.maxHeight = lineHeight * lines + 'px';
    };
    const debouncedRecalc = debounce(recalc, 50);

    const cleanup = () => {
        if (resizeObserver) {
            resizeObserver.disconnect();
            resizeObserver = null;
        }
    };

    const update = () => {
        cleanup();

        if (opts.enabled) {
            recalc();

            if (typeof ResizeObserver !== 'undefined') {
                resizeObserver = new ResizeObserver(debouncedRecalc);
                const parent = node.parentElement;
                if (parent) {
                    resizeObserver.observe(parent);
                }
            }
        } else {
            node.style.webkitLineClamp = String(opts.lineClamp || '');
        }
    };

    update();

    return {
        update(newOpts: AutoEllipsizeOptions) {
            opts = newOpts;
            update();
        },
        destroy() {
            cleanup();
        }
    };
}
