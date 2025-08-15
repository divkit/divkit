import { describe, test, expect } from 'vitest';
import { escapeExpression } from './expressions';

describe('escapeExpression', () => {
    test('simple', () => {
        expect(escapeExpression('')).toEqual('');
        expect(escapeExpression('Hello')).toEqual('Hello');
        expect(escapeExpression('John\'s orange')).toEqual('John\'s orange');
        expect(escapeExpression('ABC \\ 123')).toEqual('ABC \\\\ 123');
        expect(escapeExpression('Hello @{')).toEqual('Hello \\@{');
    });
});
