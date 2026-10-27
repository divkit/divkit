// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { tick } from 'svelte';
import { render } from '../../src/client';
import { scrollWithDuration } from '../../src/utils/scrollWithDuration';
import type { ActionScrollBy, ActionScrollTo, DivkitInstance } from '../../typings/common';

let instance: DivkitInstance | undefined;
let target: HTMLElement;

beforeEach(() => {
    target = document.createElement('div');
    document.body.append(target);
    vi.stubGlobal('ResizeObserver', class {
        observe = vi.fn();
        unobserve = vi.fn();
        disconnect = vi.fn();
    });
});

afterEach(() => {
    instance?.$destroy();
    instance = undefined;
    target.remove();
    vi.unstubAllGlobals();
    vi.restoreAllMocks();
    vi.useRealTimers();
    Reflect.deleteProperty(HTMLElement.prototype, 'scroll');
});

function renderScrollable(type: 'pager' | 'gallery' | 'tabs'): void {
    instance = render({ target, id: 'scroll-duration', json: { card: {
        log_id: 'scroll-duration',
        states: [{ state_id: 0, div: {
            type,
            id: 'scrollable',
            ...(type === 'pager' ? { layout_mode: { type: 'wrap_content' as const } } : {}),
            ...(type === 'tabs' ? { tab_title_style: { animation_duration: 450 } } : {}),
            items: type === 'tabs' ? [
                { title: 'One', div: { type: 'text', text: 'One' } },
                { title: 'Two', div: { type: 'text', text: 'Two' } }
            ] : [{ type: 'text', text: 'One' }, { type: 'text', text: 'Two' }]
        } }]
    } } });
}

const pagerScrollActions: { label: string; typed: ActionScrollTo | ActionScrollBy }[] = [
    { label: 'scroll_to index', typed: { type: 'scroll_to', id: 'scrollable', destination: { type: 'index', value: 2 }, duration: 725 } },
    { label: 'scroll_to start', typed: { type: 'scroll_to', id: 'scrollable', destination: { type: 'start' }, duration: 725 } },
    { label: 'scroll_to end', typed: { type: 'scroll_to', id: 'scrollable', destination: { type: 'end' }, duration: 725 } },
    { label: 'scroll_to item_id', typed: { type: 'scroll_to', id: 'scrollable', destination: { type: 'item_id', value: 'page-1' }, duration: 725 } },
    { label: 'scroll_by', typed: { type: 'scroll_by', id: 'scrollable', item_count: 1, duration: 725 } }
];

describe('scroll action duration', () => {
    for (const stage of ['swipe', 'fling']) {
        test.each(pagerScrollActions)(`pager $label interrupts an infinite ${stage} and owns the following animation`, async({ typed }) => {
            const frames: FrameRequestCallback[] = [];
            let frameId = 0;
            vi.stubGlobal('requestAnimationFrame', (callback: FrameRequestCallback) => {
                frames.push(callback);
                return ++frameId;
            });
            vi.stubGlobal('cancelAnimationFrame', vi.fn());
            vi.spyOn(HTMLElement.prototype, 'offsetWidth', 'get').mockReturnValue(100);
            vi.spyOn(HTMLElement.prototype, 'offsetLeft', 'get').mockImplementation(function(this: HTMLElement) {
                return this.parentElement ? Array.from(this.parentElement.children).indexOf(this) * 100 : 0;
            });
            instance = render({ target, id: 'swipe-interruption', json: { card: {
                log_id: 'swipe-interruption', states: [{ state_id: 0, div: {
                    type: 'pager', id: 'scrollable', layout_mode: { type: 'wrap_content' },
                    infinite_scroll: 1, multi_page_scroll: 1,
                    items: [0, 1, 2].map(index => ({ type: 'text', id: `page-${index}`, text: `Page ${index}` }))
                } }]
            } } });
            await tick();
            const items = target.querySelector<HTMLElement>('[class*="pager__items"]')!;
            const pointer = (type: string, pageX: number) => {
                const event = new Event(type, { bubbles: true, cancelable: true });
                Object.assign(event, { isPrimary: true, button: 0, pointerType: 'touch', pointerId: 1, pageX, pageY: 0 });
                return event;
            };
            items.dispatchEvent(pointer('pointerdown', 100));
            window.dispatchEvent(pointer('pointermove', 70));
            await tick();
            if (stage === 'fling') {
                window.dispatchEvent(pointer('pointerup', 70));
                frames.shift()?.(0);
                frames.shift()?.(50);
                await tick();
                expect(frames).toHaveLength(1);
            }

            instance.execAction({ log_id: 'interrupt', typed });
            await tick();
            await tick();
            const destination = items.style.transform;
            window.dispatchEvent(pointer('pointerup', 70));
            for (const staleFrame of frames.splice(0)) {
                staleFrame(200);
            }
            await tick();

            expect(frames).toHaveLength(0);
            expect(items.style.transform).toBe(destination);
            expect(items.parentElement?.className).toContain('pager_animated');
        });
    }

    test('tabs applies duration to content and restores defaults on a title click', async() => {
        renderScrollable('tabs');
        await tick();
        instance?.execAction({ log_id: 'scroll', typed: {
            type: 'scroll_to', id: 'scrollable', destination: { type: 'index', value: 1 }, duration: 600
        } });
        await tick();

        const swiper = target.querySelector<HTMLElement>('[class*="tabs__swiper"]');
        const titles = target.querySelector<HTMLElement>('[role="tablist"]');
        expect(swiper).not.toBeNull();
        expect(getComputedStyle(swiper!).transitionDuration).toBe('600ms');
        expect(titles?.style.getPropertyValue('--divkit-tabs-animation-duration')).toBe('600ms');

        target.querySelector<HTMLElement>('[role="tab"]')?.click();
        await tick();
        expect(swiper?.style.transitionDuration).toBe('');
        expect(swiper?.className).toContain('tabs__swiper_animated');
        expect(titles?.style.getPropertyValue('--divkit-tabs-animation-duration')).toBe('450ms');
    });

    test('tabs keeps outgoing content visible until the custom animation completes', async() => {
        vi.useFakeTimers();
        renderScrollable('tabs');
        await tick();
        instance?.execAction({ log_id: 'scroll', typed: {
            type: 'scroll_to', id: 'scrollable', destination: { type: 'index', value: 1 }, duration: 600
        } });
        await tick();

        const panels = target.querySelectorAll<HTMLElement>('[role="tabpanel"]');
        await vi.advanceTimersByTimeAsync(400);
        expect(panels[0].className).toContain('tabs__panel_visible');
        expect(panels[1].className).toContain('tabs__panel_visible');

        await vi.advanceTimersByTimeAsync(300);
        expect(panels[0].className).not.toContain('tabs__panel_visible');
        expect(panels[1].className).toContain('tabs__panel_visible');
    });

    test.each([
        { duration: 0, animated: true },
        { duration: 600, animated: false }
    ])('tabs does not animate with duration $duration and animated $animated', async({ duration, animated }) => {
        renderScrollable('tabs');
        await tick();
        instance?.execAction({ log_id: 'scroll', typed: {
            type: 'scroll_to', id: 'scrollable', destination: { type: 'index', value: 1 }, duration, animated
        } });
        await tick();

        const swiper = target.querySelector<HTMLElement>('[class*="tabs__swiper"]');
        expect(swiper?.style.transform).toBe('translate3d(-100%,0,0)');
        expect(swiper?.style.transitionDuration).toBe('');
        expect(swiper?.className).not.toContain('tabs__swiper_animated');
    });

    test('pager uses duration and returns to default for the next action', async() => {
        renderScrollable('pager');
        await tick();
        instance?.execAction({ log_id: 'scroll', typed: {
            type: 'scroll_to', id: 'scrollable', destination: { type: 'index', value: 1 }, duration: 600
        } });
        await tick();

        const items = target.querySelector<HTMLElement>('[class*="pager__items"]');
        expect(items?.style.getPropertyValue('--divkit-pager-scroll-duration')).toBe('600ms');

        instance?.execAction({ log_id: 'scroll-again', typed: {
            type: 'scroll_to', id: 'scrollable', destination: { type: 'index', value: 0 }
        } });
        await tick();
        expect(items?.style.getPropertyValue('--divkit-pager-scroll-duration')).toBe('');
    });

    test('gallery duration zero scrolls instantly', async() => {
        const scroll = vi.fn();
        Object.defineProperty(HTMLElement.prototype, 'scroll', { configurable: true, value: scroll });
        renderScrollable('gallery');
        await tick();

        instance?.execAction({ log_id: 'scroll', typed: {
            type: 'scroll_to', id: 'scrollable', destination: { type: 'offset', value: 100 }, duration: 0
        } });

        expect(scroll).toHaveBeenCalledWith({ left: 100, behavior: 'instant' });
    });

    test.each([
        { duration: 500, endTime: 500 },
        { duration: undefined, endTime: 300 }
    ])('gallery scroll by uses animation duration $endTime ms', async({ duration, endTime }) => {
        const frames: FrameRequestCallback[] = [];
        vi.stubGlobal('requestAnimationFrame', (callback: FrameRequestCallback) => {
            frames.push(callback);
            return frames.length;
        });
        vi.stubGlobal('cancelAnimationFrame', vi.fn());
        vi.spyOn(HTMLElement.prototype, 'scrollWidth', 'get').mockReturnValue(500);
        vi.spyOn(HTMLElement.prototype, 'clientWidth', 'get').mockReturnValue(100);
        const scroll = vi.fn(function(this: HTMLElement, options: ScrollToOptions) {
            this.scrollLeft = options.left ?? this.scrollLeft;
        });
        Object.defineProperty(HTMLElement.prototype, 'scroll', { configurable: true, value: scroll });
        renderScrollable('gallery');
        await tick();

        instance?.execAction({ log_id: 'scroll', typed: {
            type: 'scroll_by', id: 'scrollable', offset: 100, duration
        } });
        const scroller = target.querySelector<HTMLElement>('[class*="gallery__scroller"]');
        expect(frames).toHaveLength(1);
        frames.shift()?.(0);
        frames.shift()?.(endTime / 2);
        expect(scroller?.scrollLeft).toBeGreaterThan(0);
        expect(scroller?.scrollLeft).toBeLessThan(100);
        frames.shift()?.(endTime);
        expect(scroller?.scrollLeft).toBe(100);
        expect(scroll.mock.calls.every(([options]) => options.behavior === 'instant')).toBe(true);
    });

    test('animates to the snapped position without moving at completion', () => {
        const scroller = document.createElement('div');
        scroller.scrollLeft = 20;
        Object.defineProperty(scroller, 'scrollWidth', { value: 500 });
        Object.defineProperty(scroller, 'clientWidth', { value: 100 });
        vi.spyOn(window, 'getComputedStyle').mockImplementation(element => ({
            scrollSnapType: (element as HTMLElement).style.scrollSnapType || 'x mandatory'
        } as CSSStyleDeclaration));
        const positions: number[] = [];
        Object.defineProperty(scroller, 'scroll', { configurable: true, value: ({ left }: ScrollToOptions) => {
            const position = left ?? scroller.scrollLeft;
            scroller.scrollLeft = scroller.style.scrollSnapType === 'none' ? position : Math.round(position / 100) * 100;
            positions.push(scroller.scrollLeft);
        } });
        const frames: FrameRequestCallback[] = [];
        vi.stubGlobal('requestAnimationFrame', (callback: FrameRequestCallback) => {
            frames.push(callback);
            return frames.length;
        });
        vi.stubGlobal('cancelAnimationFrame', vi.fn());

        scrollWithDuration(scroller, 145, { axis: 'left', duration: 100 });
        expect(positions).toEqual([100, 20]);
        frames.shift()?.(0);
        frames.shift()?.(50);
        expect(scroller.scrollLeft).toBeGreaterThan(20);
        expect(scroller.scrollLeft).toBeLessThan(100);
        frames.shift()?.(100);
        expect(scroller.scrollLeft).toBe(100);
        expect(scroller.style.scrollSnapType).toBe('');
        expect(positions.at(-1)).toBe(100);
    });
});
