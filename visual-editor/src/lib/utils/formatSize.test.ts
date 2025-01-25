import { describe, test, expect } from 'vitest';
import { formatSize } from './formatSize';

describe('formatSize', () => {
    test('simple', () => {
        expect(formatSize(123)).toEqual('123');
        expect(formatSize(123.4)).toEqual('123.4');
        expect(formatSize(123.0001)).toEqual('123');
        expect(formatSize(123.456)).toEqual('123.46');
    });
});
