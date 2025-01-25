import { describe, test, expect } from 'vitest';
import { sizesToFall } from './sizesToFall';

describe('sizesToFall', () => {
    test('simple', () => {
        expect(sizesToFall([10, 10, 10])).toEqual([10, 20, 30]);
    });
});
