// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { tick } from 'svelte';
import { render } from '../../src/client';
import * as textStyleAction from '../../src/use/applyTextStyle';
import { textGradientProperties } from '../../src/use/animatedTextGradient';
import type { DivkitInstance } from '../../typings/common';

let instance: DivkitInstance | undefined;
let target: HTMLElement;

beforeEach(() => {
    vi.spyOn(textStyleAction, 'applyTextStyle');
    target = document.createElement('div');
    document.body.append(target);
    vi.stubGlobal('CSS', { registerProperty: vi.fn() });
    vi.stubGlobal('ResizeObserver', class {
        observe = vi.fn();
        unobserve = vi.fn();
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
    vi.useRealTimers();
});

function renderText(): HTMLElement[] {
    instance = render({ target, id: 'text-style', json: { card: {
        log_id: 'test',
        variables: [{ name: 'content', type: 'string', value: 'Before' },
            { name: 'color', type: 'color', value: '#ff0000' }],
        states: [{ state_id: 0, div: {
            type: 'text', text: '@{content}', font_size: 20, focused_text_color: '@{color}', paddings: { left: 8 },
            text_gradient: { type: 'animated', gradient: {
                type: 'radial_gradient', colors: ['#fff', '#000'],
                center_x: { type: 'relative', value: 0.2 }, center_y: { type: 'relative', value: 0.25 }
            } },
            ranges: [{ start: 0, end: 100, background: { type: 'cloud', color: '#80000000', corner_radius: 4 } }]
        } }]
    } } });
    return vi.mocked(textStyleAction.applyTextStyle).mock.calls.map(([node]) => node);
}

describe('Text style ownership', () => {
    test('keeps animation properties out of actual plain and cloud style objects', () => {
        // Act
        renderText();
        // Assert
        const styles = vi.mocked(textStyleAction.applyTextStyle).mock.calls.map(([, style]) => style);
        expect(styles).toHaveLength(2);
        expect(styles.flatMap(style => Object.keys(style).filter(key =>
            Object.values(textGradientProperties).some(property => property === key)))).toEqual([]);
        expect(styles[0]).toMatchObject({
            padding: '0 0 0 0.4em', opacity: 128 / 255, filter: expect.stringContaining('url(#')
        });
    });

    test.each([
        { inputs: { mode: 'css', css: { registerProperty: vi.fn() } }, expected: { offset: '' } },
        { inputs: { mode: 'js', css: {} }, expected: { offset: '-50%' } }
    ])('preserves $inputs.mode animation during text style updates', async({ inputs, expected }) => {
        // Arrange
        vi.useFakeTimers();
        vi.stubGlobal('CSS', inputs.css);
        vi.stubGlobal('matchMedia', () => ({ matches: true, addListener: vi.fn(), removeListener: vi.fn() }));
        const nodes = renderText();
        vi.advanceTimersByTime(400);
        // Act
        instance?.execAction({ log_id: 'text', typed: { type: 'set_variable', variable_name: 'content',
            value: { type: 'string', value: 'After' } } });
        instance?.execAction({ log_id: 'color', typed: { type: 'set_variable', variable_name: 'color',
            value: { type: 'color', value: '#0000ff' } } });
        await tick();
        // Assert
        expect(nodes.map(node => node.textContent)).toEqual(['After', 'After']);
        expect(nodes.map(node => ['--divkit-text-focus-color', '--divkit-text-gradient-offset',
            '--divkit-text-gradient-radius'].map(key => node.style.getPropertyValue(key))))
            .toEqual([['#0000ff', expected.offset, '100px'], ['#0000ff', expected.offset, '100px']]);
        expect(nodes.map(node => node.getAttribute('data-gradient-animation'))).toEqual([inputs.mode, inputs.mode]);
    });
});
