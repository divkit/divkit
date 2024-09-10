import { describe, test, expect } from 'vitest';
import { getRotationFromMatrix } from './getRotationFromMatrix';

describe('getRotationFromMatrix', () => {
    test('simple', () => {
        expect(getRotationFromMatrix(undefined)).toEqual(0);
        expect(getRotationFromMatrix('')).toEqual(0);
        expect(getRotationFromMatrix('matrix(1, 0, 0, 1, 0, 0)')).toBeCloseTo(0);
        expect(getRotationFromMatrix('matrix(0.707107, 0.707107, -0.707107, 0.707107, 0, 0)')).toBeCloseTo(Math.PI / 4);
        expect(getRotationFromMatrix('matrix(0, 1, -1, 0, 0, 0)')).toBeCloseTo(Math.PI / 2);
        expect(getRotationFromMatrix('matrix(-0.707107, 0.707107, -0.707107, -0.707107, 0, 0)')).toBeCloseTo(Math.PI * 3 / 4);
        expect(getRotationFromMatrix('matrix(-1, 0, 0, -1, 0, 0)')).toBeCloseTo(Math.PI);
        expect(getRotationFromMatrix('matrix(-0.707107, -0.707107, 0.707107, -0.707107, 0, 0)')).toBeCloseTo(Math.PI * 5 / 4);
        expect(getRotationFromMatrix('matrix(0, -1, 1, 0, 0, 0)')).toBeCloseTo(Math.PI * 3 / 2);
        expect(getRotationFromMatrix('matrix(0.707107, -0.707107, 0.707107, 0.707107, 0, 0)')).toBeCloseTo(Math.PI * 7 / 4);
    });
});
