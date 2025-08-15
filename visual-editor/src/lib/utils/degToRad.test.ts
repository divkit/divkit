import { describe, test, expect } from 'vitest';
import { degToRad } from './degToRad';

describe('degToRad', () => {
    test('simple', () => {
        expect(degToRad(0)).toEqual(0);
        expect(degToRad(45)).closeTo(Math.PI / 4, 1e-6);
        expect(degToRad(180)).closeTo(Math.PI, 1e-6);
    });
});
