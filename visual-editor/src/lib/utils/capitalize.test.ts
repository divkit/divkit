import { describe, test, expect } from 'vitest';
import { capitalize } from './capitalize';

describe('capitalzie', () => {
    test('simple', () => {
        expect(capitalize('hello')).toEqual('Hello');
        expect(capitalize('Hello')).toEqual('Hello');
        expect(capitalize('привет')).toEqual('Привет');
    });
});
