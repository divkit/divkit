// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { animateTextGradient } from '../../src/use/animatedTextGradient';
import { resolveTextGradient } from '../../src/utils/textGradient';
import type { RadialBackground } from '../../src/types/background';

let node: HTMLElement;
let notifyResize: () => void;
const disconnect = vi.fn();
const radialGradient: RadialBackground = {
    type: 'radial_gradient', colors: ['#fff', '#000'],
    center_x: { type: 'relative', value: 0.2 },
    center_y: { type: 'relative', value: 0.25 }
};

beforeEach(() => {
    node = document.createElement('span');
    node.style.fontSize = '20px';
    Object.defineProperties(node, {
        clientWidth: { value: 100, configurable: true },
        clientHeight: { value: 80 }
    });
    vi.stubGlobal('CSS', { registerProperty: vi.fn() });
    vi.stubGlobal('ResizeObserver', class {
        constructor(callback: () => void) {
            notifyResize = callback;
        }
        observe = vi.fn();
        disconnect = disconnect;
    });
});

afterEach(() => {
    vi.unstubAllGlobals();
    vi.clearAllMocks();
});

function createAnimation(gradient: RadialBackground) {
    return resolveTextGradient({ type: 'animated', gradient }, 'ltr')?.animation;
}

const relativeRadii = [
    { inputs: { value: 'nearest_side' as const }, expected: { radius: 20 } },
    { inputs: { value: 'farthest_side' as const }, expected: { radius: 80 } },
    { inputs: { value: 'nearest_corner' as const }, expected: { radius: 28.284271247461902 } },
    { inputs: { value: 'farthest_corner' as const }, expected: { radius: 100 } }
];

describe('animated radial gradient radius', () => {
    test.each(relativeRadii)('resolves $inputs.value against the unshifted text', ({ inputs, expected }) => {
        const animation = createAnimation({ ...radialGradient, radius: { type: 'relative', value: inputs.value } });
        const action = animateTextGradient(node, { animation, fontSize: 20 });
        expect(parseFloat(node.style.getPropertyValue('--divkit-text-gradient-radius'))).toBeCloseTo(expected.radius);
        action.destroy();
    });

    test('uses farthest corner by default', () => {
        const action = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        expect(node.style.getPropertyValue('--divkit-text-gradient-radius')).toBe('100px');
        action.destroy();
    });

    test('converts a fixed radius using the text font size', () => {
        const animation = createAnimation({ ...radialGradient, radius: { type: 'fixed', value: 12 } });
        const action = animateTextGradient(node, { animation, fontSize: 20 });
        expect(node.style.getPropertyValue('--divkit-text-gradient-radius')).toBe('24px');
        action.destroy();
    });

    test('resolves fixed centers before choosing the nearest side', () => {
        const animation = createAnimation({
            ...radialGradient, center_x: { type: 'fixed', value: 5 }, radius: { type: 'relative', value: 'nearest_side' }
        });
        const action = animateTextGradient(node, { animation, fontSize: 20 });
        expect(node.style.getPropertyValue('--divkit-text-gradient-radius')).toBe('10px');
        action.destroy();
    });
});

describe('animated radial gradient lifecycle', () => {
    test('recalculates the radius when the text resizes', () => {
        const action = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        Object.defineProperty(node, 'clientWidth', { value: 200 });
        notifyResize();
        expect(parseFloat(node.style.getPropertyValue('--divkit-text-gradient-radius'))).toBeCloseTo(170.88007490635061);
        action.destroy();
    });

    test('removes animation state and stops observing after a static update', () => {
        const action = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        const { update } = action;
        update({ animation: undefined, fontSize: 20 });
        expect(node.hasAttribute('data-gradient-animation')).toBe(false);
        expect(node.style.getPropertyValue('--divkit-text-gradient-radius')).toBe('');
        expect(disconnect).toHaveBeenCalledOnce();
    });

    test('stops observing when the element is destroyed', () => {
        const action = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        const { destroy } = action;
        destroy();
        expect(disconnect).toHaveBeenCalledOnce();
    });
});

describe('animated gradient browser compatibility', () => {
    test('keeps a static radial gradient without resize observation', () => {
        vi.stubGlobal('ResizeObserver', undefined);
        const action = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        expect(node.hasAttribute('data-gradient-animation')).toBe(false);
        action.destroy();
    });
});

let fallbackAction: ReturnType<typeof animateTextGradient> | undefined;
let motionPreference: MediaQueryList;
const linearGradient = { type: 'gradient' as const, colors: ['#fff', '#000'], angle: 45 };

function setupFrameFallback(): void {
    vi.useFakeTimers();
    vi.stubGlobal('CSS', {});
    const mediaEvents = new EventTarget();
    motionPreference = Object.assign(mediaEvents, {
        matches: true,
        addListener(listener: EventListener) {
            mediaEvents.addEventListener('change', listener);
        },
        removeListener(listener: EventListener) {
            mediaEvents.removeEventListener('change', listener);
        }
    }) as unknown as MediaQueryList;
    vi.stubGlobal('matchMedia', () => motionPreference);
}

function cleanupFrameFallback(): void {
    fallbackAction?.destroy();
    fallbackAction = undefined;
    vi.useRealTimers();
}

describe('animated gradient fallback timing', () => {
    beforeEach(setupFrameFallback);
    afterEach(cleanupFrameFallback);

    test.each([
        { inputs: { elapsed: 400 }, expected: { offset: '-50%' } },
        { inputs: { elapsed: 800 }, expected: { offset: '0%' } },
        { inputs: { elapsed: 1200 }, expected: { offset: '50%' } },
        { inputs: { elapsed: 1600 }, expected: { offset: '-100%' } }
    ])('moves forward and repeats after $inputs.elapsed ms', ({ inputs, expected }) => {
        const animation = resolveTextGradient({ type: 'animated', gradient: linearGradient }, 'ltr')?.animation;
        fallbackAction = animateTextGradient(node, { animation, fontSize: 20 });
        vi.advanceTimersByTime(inputs.elapsed);
        expect(node.style.getPropertyValue('--divkit-text-gradient-offset')).toBe(expected.offset);
        expect(node.getAttribute('data-gradient-animation')).toBe('js');
    });
});

describe('animated gradient fallback geometry', () => {
    beforeEach(setupFrameFallback);
    afterEach(cleanupFrameFallback);

    test('animates even when the CSS namespace is unavailable', () => {
        vi.stubGlobal('CSS', undefined);
        fallbackAction = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        vi.advanceTimersByTime(800);
        expect(node.style.getPropertyValue('--divkit-text-gradient-offset')).toBe('0%');
        expect(node.style.getPropertyValue('--divkit-text-gradient-radius')).toBe('100px');
    });

    test('recalculates the radial radius on resize during fallback animation', () => {
        fallbackAction = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        Object.defineProperty(node, 'clientWidth', { value: 200 });
        notifyResize();
        expect(parseFloat(node.style.getPropertyValue('--divkit-text-gradient-radius'))).toBeCloseTo(170.88007490635061);
    });
});

describe('animated gradient fallback duration', () => {
    beforeEach(setupFrameFallback);
    afterEach(cleanupFrameFallback);

    test('uses the new duration after an update', () => {
        // Arrange
        fallbackAction = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        vi.advanceTimersByTime(400);
        const animation = resolveTextGradient({ type: 'animated', gradient: radialGradient, duration: 800 }, 'ltr')?.animation;
        // Act
        fallbackAction.update({ animation, fontSize: 20 });
        vi.advanceTimersByTime(400);
        // Assert
        expect(node.style.getPropertyValue('--divkit-text-gradient-offset')).toBe('0%');
        expect(vi.getTimerCount()).toBe(1);
    });
});

describe('animated gradient fallback cleanup', () => {
    beforeEach(setupFrameFallback);
    afterEach(cleanupFrameFallback);

    test('stops and clears the offset when duration becomes zero', () => {
        // Arrange
        fallbackAction = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        const animation = resolveTextGradient({ type: 'animated', gradient: radialGradient, duration: 0 }, 'ltr')?.animation;
        // Act
        fallbackAction.update({ animation, fontSize: 20 });
        vi.advanceTimersByTime(800);
        // Assert
        expect(node.hasAttribute('data-gradient-animation')).toBe(false);
        expect(node.style.getPropertyValue('--divkit-text-gradient-offset')).toBe('');
        expect(vi.getTimerCount()).toBe(0);
    });

    test('stops frame callbacks and removes the motion listener on destroy', () => {
        // Arrange
        fallbackAction = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        // Act
        fallbackAction.destroy();
        motionPreference.dispatchEvent(new Event('change'));
        vi.advanceTimersByTime(800);
        // Assert
        expect(node.hasAttribute('data-gradient-animation')).toBe(false);
        expect(node.style.getPropertyValue('--divkit-text-gradient-offset')).toBe('');
        expect(vi.getTimerCount()).toBe(0);
    });
});

describe('animated gradient fallback reduced motion', () => {
    beforeEach(setupFrameFallback);
    afterEach(cleanupFrameFallback);

    test('does not schedule frames when reduced motion is enabled initially', () => {
        Object.assign(motionPreference, { matches: false });
        fallbackAction = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        expect(node.hasAttribute('data-gradient-animation')).toBe(false);
        expect(vi.getTimerCount()).toBe(0);
    });

    test('stops the animation when reduced motion is enabled', () => {
        // Arrange
        fallbackAction = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        // Act
        Object.assign(motionPreference, { matches: false });
        motionPreference.dispatchEvent(new Event('change'));
        // Assert
        expect(node.hasAttribute('data-gradient-animation')).toBe(false);
        expect(node.style.getPropertyValue('--divkit-text-gradient-offset')).toBe('');
        expect(vi.getTimerCount()).toBe(0);
    });

    test('restarts the animation when reduced motion is disabled', () => {
        // Arrange
        Object.assign(motionPreference, { matches: false });
        fallbackAction = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        // Act
        Object.assign(motionPreference, { matches: true });
        motionPreference.dispatchEvent(new Event('change'));
        vi.advanceTimersByTime(800);
        // Assert
        expect(node.getAttribute('data-gradient-animation')).toBe('js');
        expect(node.style.getPropertyValue('--divkit-text-gradient-offset')).toBe('0%');
    });
});

describe('animated gradient animation routing', () => {
    beforeEach(setupFrameFallback);
    afterEach(cleanupFrameFallback);

    test('uses frame animation when CSS property registration fails', async() => {
        // Arrange
        vi.resetModules();
        vi.stubGlobal('CSS', { registerProperty: vi.fn(() => { throw new Error('Registration failed') }) });
        const { animateTextGradient: createGradientAnimation } = await vi.importActual<{
            animateTextGradient: typeof animateTextGradient;
        }>('../../src/use/animatedTextGradient');
        const animation = createAnimation(radialGradient);
        // Act
        fallbackAction = createGradientAnimation(node, { animation, fontSize: 20 });
        vi.advanceTimersByTime(800);
        // Assert
        expect(node.getAttribute('data-gradient-animation')).toBe('js');
        expect(node.style.getPropertyValue('--divkit-text-gradient-offset')).toBe('0%');
    });

    test('uses CSS animation without scheduling frames when registration is supported', () => {
        vi.stubGlobal('CSS', { registerProperty: vi.fn() });
        fallbackAction = animateTextGradient(node, { animation: createAnimation(radialGradient), fontSize: 20 });
        expect(node.getAttribute('data-gradient-animation')).toBe('css');
        expect(vi.getTimerCount()).toBe(0);
    });
});
