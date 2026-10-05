import {
    describe,
    expect,
    test
} from 'vitest';

import { resolveTextGradient } from '../../src/utils/textGradient';

const linearGradient = {
    type: 'gradient' as const,
    colors: ['#fc0', '#f00']
};

const radialGradient = {
    type: 'radial_gradient' as const,
    colors: ['#fc0', '#f00']
};

const linearBackgroundImage = 'linear-gradient(90deg,#ffcc00,#ff0000)';
const radialBackgroundImage = 'radial-gradient(circle farthest-corner at 50% 50%,#ffcc00,#ff0000)';

function animatedGradient(duration?: number) {
    return {
        type: 'animated' as const,
        gradient: linearGradient,
        duration
    };
}

describe('static text gradient', () => {
    test('resolves a linear gradient', () => {
        expect(resolveTextGradient(linearGradient, 'ltr')).toEqual({
            backgroundImage: linearBackgroundImage
        });
    });

    test('resolves a radial gradient', () => {
        expect(resolveTextGradient(radialGradient, 'ltr')).toEqual({
            backgroundImage: radialBackgroundImage
        });
    });
});

const animatedLinearBackground = 'linear-gradient(90deg,#ffcc00 calc(0% + var(--divkit-text-gradient-offset)),#ff0000 calc(100% + var(--divkit-text-gradient-offset)))';

describe('animated text gradient duration', () => {
    test('uses the default duration', () => {
        expect(resolveTextGradient(animatedGradient(), 'ltr')?.animation).toEqual({
            duration: 1600,
            backgroundImage: animatedLinearBackground
        });
    });

    test('keeps a zero-duration gradient visible and static', () => {
        expect(resolveTextGradient(animatedGradient(0), 'ltr')).toEqual({
            backgroundImage: linearBackgroundImage
        });
    });

    test.each([
        { name: 'negative duration', inputs: { duration: -1 } },
        { name: 'NaN duration', inputs: { duration: Number.NaN } },
        { name: 'infinite duration', inputs: { duration: Number.POSITIVE_INFINITY } }
    ])('uses the default duration for $name', ({ inputs }) => {
        expect(resolveTextGradient(animatedGradient(inputs.duration), 'ltr')?.animation?.duration).toBe(1600);
    });
});

describe('animated radial text gradient', () => {
    test('moves the center while retaining a separately resolved radius', () => {
        const result = resolveTextGradient({
            type: 'animated', gradient: radialGradient, duration: 2400
        }, 'ltr');
        expect(result?.animation).toEqual({
            duration: 2400,
            backgroundImage: 'radial-gradient(circle var(--divkit-text-gradient-radius) at calc(50% + var(--divkit-text-gradient-offset)) 50%,#ffcc00,#ff0000)',
            radialGradient
        });
    });

    test('retains off-center coordinates', () => {
        const result = resolveTextGradient({
            type: 'animated',
            gradient: { ...radialGradient, center_x: { type: 'relative', value: 0 }, center_y: { type: 'fixed', value: 10 } }
        }, 'ltr');
        expect(result?.animation?.backgroundImage).toBe(
            'radial-gradient(circle var(--divkit-text-gradient-radius) at calc(0% + var(--divkit-text-gradient-offset)) 1em,#ffcc00,#ff0000)'
        );
    });
});

const gradientDirections = [
    { name: 'horizontal', inputs: { angle: 0 }, expected: { cssAngle: '90deg' } },
    { name: 'vertical', inputs: { angle: 90 }, expected: { cssAngle: '0deg' } },
    { name: 'diagonal', inputs: { angle: 45 }, expected: { cssAngle: '45deg' } }
];

describe('animated linear text gradient', () => {
    test.each(gradientDirections)('shifts stops along the $name gradient', ({ inputs, expected }) => {
        const result = resolveTextGradient({
            type: 'animated', gradient: { ...linearGradient, angle: inputs.angle }
        }, 'ltr');
        expect(result?.animation?.backgroundImage).toBe(
            `linear-gradient(${expected.cssAngle},#ffcc00 calc(0% + var(--divkit-text-gradient-offset)),#ff0000 calc(100% + var(--divkit-text-gradient-offset)))`
        );
    });

    test('preserves explicit color-map positions during translation', () => {
        const result = resolveTextGradient({
            type: 'animated',
            gradient: { type: 'gradient', color_map: [{ color: '#fff', position: 0.25 }, { color: '#000', position: 0.75 }] }
        }, 'ltr');
        expect(result?.animation?.backgroundImage).toBe(
            'linear-gradient(90deg,#ffffff calc(25.00% + var(--divkit-text-gradient-offset)),#000000 calc(75.00% + var(--divkit-text-gradient-offset)))'
        );
    });
});

describe('invalid text gradient', () => {
    test('does not resolve an absent gradient', () => {
        expect(resolveTextGradient(undefined, 'ltr')).toBeUndefined();
    });

    test('does not resolve a missing nested gradient', () => {
        expect(resolveTextGradient({
            type: 'animated'
        }, 'ltr')).toBeUndefined();
    });

    test('does not resolve an unsupported nested gradient', () => {
        expect(resolveTextGradient({
            type: 'animated',
            gradient: {
                type: 'solid',
                color: '#f00'
            }
        } as never, 'ltr')).toBeUndefined();
    });
});
