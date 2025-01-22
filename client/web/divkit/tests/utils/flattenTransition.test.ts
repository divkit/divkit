import {
    describe,
    expect,
    test
} from 'vitest';

import { flattenTransition } from '../../src/utils/flattenTransition';

describe('flattenTransition', () => {
    test('simple', () => {
        expect(flattenTransition({
            type: 'slide'
        })).toEqual([{
            type: 'slide'
        }]);
        expect(flattenTransition({
            type: 'set',
            items: [{
                type: 'slide'
            }]
        })).toEqual([{
            type: 'slide'
        }]);
        expect(flattenTransition({
            type: 'set',
            items: [{
                type: 'slide'
            }, {
                type: 'set',
                items: [{
                    type: 'fade'
                }]
            }]
        })).toEqual([{
            type: 'slide'
        }, {
            type: 'fade'
        }]);
    });
});
