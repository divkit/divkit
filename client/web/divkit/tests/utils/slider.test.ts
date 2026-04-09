import {
    describe,
    expect,
    test
} from 'vitest';

import { fillTicks } from '../../src/utils/slider';

describe('fillTicks', () => {
    describe('inside mode', () => {
        test('basic range', () => {
            const result = fillTicks(2, 8, 0, 10, true);
            expect(result).toEqual([0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8]);
        });

        test('full range', () => {
            const result = fillTicks(0, 10, 0, 10, true);
            expect(result).toEqual([0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1]);
        });

        test('negative range', () => {
            const result = fillTicks(-5, 5, -10, 10, true);
            expect(result).toEqual([0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75]);
        });

        test('large range with scaling', () => {
            const result = fillTicks(0, 2000, 0, 2000, true);
            expect(result.length).toBeGreaterThan(900);
            expect(result.length).toBeLessThan(1100);
            expect(result[0]).toBe(0);
            expect(result[result.length - 1]).toBe(1);
        });

        test('fractional values', () => {
            const result = fillTicks(2.5, 7.5, 0, 10, true);
            expect(result).toEqual([0.3, 0.4, 0.5, 0.6, 0.7, 0.75]);
        });
    });

    describe('outside mode', () => {
        test('basic range', () => {
            const result = fillTicks(2, 8, 0, 10, false);
            expect(result).toEqual([0, 0.1, 0.9, 1]);
        });

        test('full range (no outside ticks)', () => {
            const result = fillTicks(0, 10, 0, 10, false);
            expect(result).toEqual([]);
        });

        test('middle range', () => {
            const result = fillTicks(3, 7, 0, 10, false);
            expect(result).toEqual([0, 0.1, 0.2, 0.8, 0.9, 1]);
        });

        test('large range with scaling', () => {
            const result = fillTicks(500, 1500, 0, 2000, false);
            expect(result.length).toBeGreaterThan(200);
            expect(result.length).toBeLessThan(800);
            expect(result[0]).toBe(0);
            expect(result[result.length - 1]).toBe(1);
        });
    });

    describe('scaling behavior', () => {
        test('at MAX_TICKS threshold', () => {
            const result = fillTicks(0, 1000, 0, 1000, true);
            expect(result.length).toBe(1001);
        });

        test('over MAX_TICKS threshold', () => {
            const result = fillTicks(0, 1001, 0, 1001, true);
            expect(result.length).toBeLessThan(1001);
            expect(result.length).toBeGreaterThan(500);
        });

        test('startOffset alignment with non-zero minValue', () => {
            // When minValue is not aligned to scale, startOffset ensures proper grid alignment
            const result = fillTicks(0, 10, 3, 13, true);
            // With scale=1, startOffset = round(3, 1, 3) - 3 = 3 - 3 = 0
            // Ticks should start from minValue (3) and go to maxValue (13)
            expect(result).toEqual([-0.3, -0.2, -0.1, 0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7]);
        });

        test('startOffset with scaling and offset minValue', () => {
            // With large range and offset minValue, startOffset aligns to scale grid
            const result = fillTicks(1001, 2001, 1001, 3001, true);
            // Range is 2000, scale = 2
            // startOffset = round(1001, 2, 1001) - 1001 = 1002 - 1001 = 1
            expect(result.length).toBe(501);
            expect(result[0]).toBe(0);
            expect(result[result.length - 1]).toBe(0.5);
        });
    });
});

