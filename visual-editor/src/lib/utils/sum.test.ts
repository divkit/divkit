import { describe, test, expect } from 'vitest';
import { sum } from './sum';

describe('sum', () => {
    test('simple', () => {
        expect(sum([])).toEqual(0);
        expect(sum([1, 2, 3])).toEqual(6);
    });
});
