import { describe, test, expect } from 'vitest';
import { decline } from './decline';

describe('decline', () => {
    test('simple', () => {
        expect(decline(1, ['1', '2', '5', '0'])).toEqual('1');
        expect(decline(1, ['1', '2', '5'])).toEqual('1');

        expect(decline(3, ['1', '2', '5', '0'])).toEqual('2');
        expect(decline(3, ['1', '2', '5'])).toEqual('2');

        expect(decline(6, ['1', '2', '5', '0'])).toEqual('5');
        expect(decline(6, ['1', '2', '5'])).toEqual('5');

        expect(decline(0, ['1', '2', '5', '0'])).toEqual('0');
        expect(decline(0, ['1', '2', '5'])).toEqual('5');

        expect(decline(11, ['1', '2', '5', '0'])).toEqual('5');
        expect(decline(11, ['1', '2', '5'])).toEqual('5');

        expect(decline(14, ['1', '2', '5', '0'])).toEqual('5');
        expect(decline(14, ['1', '2', '5'])).toEqual('5');

        expect(decline(21, ['1', '2', '5', '0'])).toEqual('1');
        expect(decline(21, ['1', '2', '5'])).toEqual('1');
    });
});
