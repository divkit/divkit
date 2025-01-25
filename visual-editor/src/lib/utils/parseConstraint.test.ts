import { describe, test, expect } from 'vitest';
import { parseConstraint } from './parseConstraint';

describe('parseConstraint', () => {
    test('simple', () => {
        expect(parseConstraint(undefined, undefined)).toEqual({});
        expect(parseConstraint('integer', undefined)).toEqual({});
        expect(parseConstraint('number', undefined)).toEqual({});
        expect(parseConstraint('integer', 'number > 0')).toEqual({
            min: 1
        });
        expect(parseConstraint('number', 'number > 0')).toEqual({
            min: .01
        });
        expect(parseConstraint('integer', 'number >= 0')).toEqual({
            min: 0
        });
        expect(parseConstraint('number', 'number >= 0')).toEqual({
            min: 0
        });
        expect(parseConstraint('integer', 'number > 0 && number < 100')).toEqual({
            min: 1,
            max: 99
        });
        expect(parseConstraint('number', 'number > 0 && number < 1')).toEqual({
            min: .01,
            max: .99
        });
    });
});
