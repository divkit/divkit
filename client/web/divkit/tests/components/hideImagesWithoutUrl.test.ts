// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { tick } from 'svelte';
import { render } from '../../src/client';
import type { DivBase, DivJson, DivkitInstance } from '../../typings/common';
import * as visibilityAction from '../../src/use/visibilityAction';
import outerCss from '../../src/components/utilities/Outer.module.css';
import galleryCss from '../../src/components/gallery/Gallery.module.css';
import imageCss from '../../src/components/image/Image.module.css';

vi.mock('svelte', () => vi.importActual<typeof import('svelte')>('svelte/internal'));

let instance: DivkitInstance | undefined;
let target: HTMLElement;
let nodes: Map<string, HTMLElement>;
const onError = vi.fn();

beforeEach(() => {
    target = document.createElement('div');
    document.body.append(target);
    nodes = new Map();
    onError.mockClear();
    vi.spyOn(visibilityAction, 'visibilityAction');
    vi.stubGlobal('ResizeObserver', class {
        observe = vi.fn();
        unobserve = vi.fn();
        disconnect = vi.fn();
    });
    vi.stubGlobal('IntersectionObserver', class {
        observe = vi.fn();
        disconnect = vi.fn();
    });
    vi.spyOn(HTMLElement.prototype, 'clientWidth', 'get').mockReturnValue(100);
    vi.spyOn(HTMLElement.prototype, 'clientHeight', 'get').mockReturnValue(80);
});

afterEach(() => {
    instance?.$destroy();
    instance = undefined;
    target.remove();
    vi.unstubAllGlobals();
    vi.restoreAllMocks();
});

function card(div: DivBase): DivJson {
    return { card: { log_id: 'test', variables: [
        { name: 'shown', type: 'boolean', value: true }
    ], states: [{ state_id: 0, div }] } };
}

function renderCard(div: DivBase, hideImagesWithoutUrl?: boolean): void {
    instance = render({
        target, id: 'images', json: card(div), hideImagesWithoutUrl, onError,
        onComponent({ type, node, json }) {
            if (type !== 'destroy' && typeof json.id === 'string' && node) {
                nodes.set(json.id, node);
            }
        }
    });
}

function image(type: 'image' | 'gif', id: string, props: Record<string, unknown> = {}): DivBase {
    return { type, id, width: { type: 'fixed', value: 30 }, height: { type: 'fixed', value: 20 },
        margins: { left: 10, top: 10 }, ...props } as DivBase;
}

async function settle(): Promise<void> {
    for (let i = 0; i < 5; ++i) {
        await tick();
    }
}

describe('hideImagesWithoutUrl', () => {
    test.each([
        { flag: undefined, hidden: [] },
        { flag: false, hidden: [] },
        { flag: true, hidden: ['image-missing', 'image-null', 'gif-missing', 'gif-null'] }
    ])('hides only absent and null raw URLs with flag $flag', async({ flag, hidden }) => {
        const items = (['image', 'gif'] as const).flatMap(type => {
            const url = type === 'image' ? 'image_url' : 'gif_url';
            return [
                image(type, `${type}-missing`, { preview: '#ff0000', placeholder_color: '#00ff00' }),
                image(type, `${type}-null`, { [url]: null }),
                image(type, `${type}-empty`, { [url]: '' }),
                image(type, `${type}-placeholder`, { [url]: 'empty://' }),
                image(type, `${type}-expression`, { [url]: "@{''}" })
            ];
        });
        renderCard({ type: 'container', items }, flag);
        await settle();
        expect([...nodes].filter(([, node]) => node.classList.contains(outerCss.outer_visibility_gone))
            .map(([id]) => id)).toEqual(hidden);
        expect([...nodes.keys()]).toEqual(expect.arrayContaining(items.map(item => item.id)));
        expect(onError).not.toHaveBeenCalled();
    });

    test.each(['gallery', 'pager'] as const)('uses gone visibility in %s layout bookkeeping', async type => {
        renderCard({
            type, id: 'collection', width: { type: 'fixed', value: 100 },
            height: { type: 'fixed', value: 80 },
            ...(type === 'pager' ? { layout_mode: { type: 'percentage', page_width: { value: 100 } } } : {}),
            items: [image('image', 'missing'), image('gif', 'null', { gif_url: null }),
                image('image', 'kept', { image_url: 'empty://' })]
        } as DivBase, true);
        await settle();
        expect(nodes.has('kept')).toBe(true);
        if (type === 'gallery') {
            expect(target.querySelectorAll(`.${galleryCss.gallery__gap}`)).toHaveLength(1);
            expect(nodes.get('missing')?.classList.contains(outerCss.outer_visibility_gone)).toBe(true);
        } else {
            expect(nodes.has('missing')).toBe(false);
            expect(nodes.has('null')).toBe(false);
        }
        expect(onError).not.toHaveBeenCalled();
    });

    test('suppresses visibility actions and transitions and restores an image when its URL is provided', async() => {
        const props = {
            visibility: "@{shown ? 'visible' : 'gone'}",
            transition_triggers: ['visibility_change'],
            transition_in: { type: 'fade', duration: 100 },
            transition_out: { type: 'fade', duration: 100 },
            visibility_actions: [{ log_id: 'visible', visibility_duration: 0 }]
        };
        const scene = (withUrl = false): DivBase => ({ type: 'state', id: 'screen', states: [{
            state_id: 'main', div: image('image', 'changing', { ...props, ...(withUrl ? { image_url: 'empty://' } : {}) })
        }] });
        renderCard(scene(), true);
        await settle();
        for (const shown of [false, true]) {
            instance?.execAction({ log_id: 'toggle', typed: { type: 'set_variable', variable_name: 'shown',
                value: { type: 'boolean', value: shown } } });
            await settle();
        }
        expect(visibilityAction.visibilityAction).not.toHaveBeenCalled();
        expect(nodes.get('changing')?.classList.contains(outerCss.outer_visibility_gone)).toBe(true);
        expect(target.querySelectorAll(`.${imageCss.image}`)).toHaveLength(1);
        instance?.setData(card(scene(true)));
        await settle();
        expect(nodes.get('changing')?.classList.contains(outerCss.outer_visibility_gone)).toBe(false);
        expect(visibilityAction.visibilityAction).toHaveBeenCalled();
        instance?.setData(card(scene()));
        await settle();
        expect(nodes.get('changing')?.classList.contains(outerCss.outer_visibility_gone)).toBe(true);
        expect(onError).not.toHaveBeenCalled();
    });
});
