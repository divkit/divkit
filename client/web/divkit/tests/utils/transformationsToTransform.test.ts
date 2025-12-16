import {
    describe,
    expect,
    test
} from 'vitest';

import { transformationsToTransform } from '../../src/utils/transformationsToTransform';
import type {
    RotationTransformation,
    TranslationTransformation,
    PivotFixedValue,
    PivotPercentageValue,
    FixedTranslation,
    PercentageTranslation,
    Transformation,
} from '../../src/types/base';

describe('transformationsToTransform', () => {
    test('empty array', () => {
        expect(transformationsToTransform([])).toBe('');
    });

    test('rotation with angle', () => {
        const pivotX: PivotFixedValue = { type: 'pivot-fixed', value: 10 };
        const pivotY: PivotFixedValue = { type: 'pivot-fixed', value: 20 };
        const transformations: RotationTransformation[] = [{
            type: 'rotation',
            angle: 45,
            pivot_x: pivotX,
            pivot_y: pivotY
        }];

        expect(transformationsToTransform(transformations)).toBe('translate(1em, 2em) rotate(45deg) translate(-1em, -2em)');
    });

    test('rotation without angle', () => {
        const pivotX: PivotFixedValue = { type: 'pivot-fixed', value: 10 };
        const pivotY: PivotFixedValue = { type: 'pivot-fixed', value: 20 };
        const transformations = [{
            type: 'rotation',
            angle: undefined,
            pivot_x: pivotX,
            pivot_y: pivotY
        }];

        //@ts-expect-error Incorrect data
        expect(transformationsToTransform(transformations)).toBe('');
    });

    test('rotation with percentage pivot', () => {
        const pivotX: PivotPercentageValue = { type: 'pivot-percentage', value: 50 };
        const pivotY: PivotPercentageValue = { type: 'pivot-percentage', value: 75 };
        const transformations: Transformation[] = [{
            type: 'rotation',
            angle: 30,
            pivot_x: pivotX,
            pivot_y: pivotY
        }];

        expect(transformationsToTransform(transformations)).toBe('translate(50%, 75%) rotate(30deg) translate(-50%, -75%)');
    });

    test('translation with fixed values', () => {
        const x: FixedTranslation = { type: 'translation-fixed', value: 15 };
        const y: FixedTranslation = { type: 'translation-fixed', value: 25 };
        const transformations: TranslationTransformation[] = [{
            type: 'translation',
            x,
            y
        }];

        expect(transformationsToTransform(transformations)).toBe('translate(1.5em, 2.5em)');
    });

    test('translation with percentage values', () => {
        const x: PercentageTranslation = { type: 'translation-percentage', value: 30 };
        const y: PercentageTranslation = { type: 'translation-percentage', value: 40 };
        const transformations: TranslationTransformation[] = [{
            type: 'translation',
            x,
            y
        }];

        expect(transformationsToTransform(transformations)).toBe('translate(30%, 40%)');
    });

    test('translation with missing values defaults to 0', () => {
        const transformations: TranslationTransformation[] = [{
            type: 'translation',
            x: undefined,
            y: undefined
        }];

        expect(transformationsToTransform(transformations)).toBe('translate(0, 0)');
    });

    test('mixed transformations', () => {
        const rotationPivotX: PivotFixedValue = { type: 'pivot-fixed', value: 5 };
        const rotationPivotY: PivotFixedValue = { type: 'pivot-fixed', value: 10 };
        const translationX: PercentageTranslation = { type: 'translation-percentage', value: 20 };
        const translationY: PercentageTranslation = { type: 'translation-percentage', value: 30 };

        const transformations: (RotationTransformation | TranslationTransformation)[] = [
            {
                type: 'rotation',
                angle: 90,
                pivot_x: rotationPivotX,
                pivot_y: rotationPivotY
            },
            {
                type: 'translation',
                x: translationX,
                y: translationY
            }
        ];

        expect(transformationsToTransform(transformations)).toBe('translate(0.5em, 1em) rotate(90deg) translate(-0.5em, -1em) translate(20%, 30%)');
    });

    test('transformation with invalid pivot values', () => {
        const pivotX = { type: 'pivot-fixed', value: 'invalid' };
        const pivotY = { type: 'pivot-fixed', value: 'invalid' };
        const transformations: RotationTransformation[] = [{
            type: 'rotation',
            angle: 45,
            //@ts-expect-error Incorrect data
            pivot_x: pivotX,
            //@ts-expect-error Incorrect data
            pivot_y: pivotY
        }];

        expect(transformationsToTransform(transformations)).toBe('translate(50%, 50%) rotate(45deg) translate(-50%, -50%)');
    });
});
