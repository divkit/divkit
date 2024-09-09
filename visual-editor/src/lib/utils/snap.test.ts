import { describe, test, expect } from 'vitest';
import { bestSnap } from './snap';

describe('snap', () => {
    test('bestSnap', () => {
        expect(bestSnap([], {
            type: 'center',
            val: 0
        }, 6)).toEqual(undefined);

        expect(bestSnap([{
            type: 'center',
            val: 100
        }, {
            type: 'center',
            val: 200
        }, {
            type: 'center',
            val: 300
        }], {
            type: 'center',
            val: 103
        }, 6)).toEqual({
            type: 'center',
            val: 100
        });

        expect(bestSnap([{
            type: 'center',
            val: 100
        }, {
            type: 'center',
            val: 200
        }, {
            type: 'center',
            val: 300
        }], {
            type: 'center',
            val: 103
        }, 1)).toEqual(undefined);

        expect(bestSnap([{
            type: 'start',
            val: 100
        }, {
            type: 'start',
            val: 200
        }, {
            type: 'start',
            val: 300
        }], {
            type: 'center',
            val: 103
        }, 6)).toEqual(undefined);
    });
});
