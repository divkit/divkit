import { isPrefersReducedMotion } from './isPrefersReducedMotion';

const DEFAULT_DURATION = 300;

export function scrollWithDuration(
    scroller: HTMLElement,
    offset: number,
    {
        axis,
        rtl = false,
        animated = true,
        duration = DEFAULT_DURATION
    }: {
        axis: 'left' | 'top';
        rtl?: boolean;
        animated?: boolean;
        duration?: number;
    }
): (() => void) | undefined {
    if (!animated || duration === 0 || isPrefersReducedMotion()) {
        scroller.scroll({ [axis]: offset, behavior: 'instant' });
        return undefined;
    }

    const horizontal = axis === 'left';
    const start = horizontal ? scroller.scrollLeft : scroller.scrollTop;
    const maxScroll = Math.max(0, horizontal ?
        scroller.scrollWidth - scroller.clientWidth : scroller.scrollHeight - scroller.clientHeight);
    const min = horizontal && rtl ? -maxScroll : 0;
    const max = horizontal && rtl ? 0 : maxScroll;
    let target = Math.max(min, Math.min(max, offset));
    const snapType = getComputedStyle(scroller).scrollSnapType;
    const hasSnap = Boolean(snapType && snapType !== 'none');
    const previousSnapType = scroller.style.scrollSnapType;
    let frame = 0;
    let startedAt: number | undefined;
    let active = true;

    if (hasSnap) {
        // Measure the snapped target, then restore the start with snapping disabled.
        // Animating to that exact target prevents a jump when snapping is restored.
        scroller.scroll({ [axis]: target, behavior: 'instant' });
        target = horizontal ? scroller.scrollLeft : scroller.scrollTop;
        scroller.style.scrollSnapType = 'none';
        scroller.scroll({ [axis]: start, behavior: 'instant' });
    }

    const cancel = (): void => {
        if (active) {
            active = false;
            cancelAnimationFrame(frame);
            if (hasSnap) {
                scroller.style.scrollSnapType = previousSnapType;
            }
        }
    };

    const tick = (now: number): void => {
        startedAt ??= now;
        const progress = Math.min((now - startedAt) / duration, 1);
        const eased = 1 - Math.pow(1 - progress, 3);
        scroller.scroll({ [axis]: start + (target - start) * eased, behavior: 'instant' });
        if (progress < 1) {
            frame = requestAnimationFrame(tick);
        } else {
            cancel();
        }
    };
    frame = requestAnimationFrame(tick);
    return cancel;
}
