import { describe, test, expect } from 'vitest';
import { isRangeStyleEqual } from './range';

describe('range', () => {
    test('isRangeStyleEqual', () => {
        expect(isRangeStyleEqual({
            start: 0,
            end: 10,
            font_weight: 'bold'
        }, {
            start: 20,
            end: 30,
            font_weight: 'bold'
        })).toEqual(true);

        expect(isRangeStyleEqual({
            start: 0,
            end: 10,
            font_weight: 'bold'
        }, {
            start: 20,
            end: 30,
            font_weight: 'light'
        })).toEqual(false);
    });
});
