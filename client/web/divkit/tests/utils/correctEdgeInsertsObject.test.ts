import {
    describe,
    expect,
    test
} from 'vitest';

import { correctEdgeInsertsObject } from '../../src/utils/correctEdgeInsertsObject';

describe('correctEdgeInsertsObject', () => {
    test('simple', () => {
        expect(correctEdgeInsertsObject(undefined, null)).toBe(null);
        // @ts-expect-error wrong data
        expect(correctEdgeInsertsObject(123, null)).toBe(null);
        expect(correctEdgeInsertsObject({
            top: 1
        }, {
            bottom: 23
        })).toEqual({
            top: 1
        });
        expect(correctEdgeInsertsObject({
            top: 1,
            bottom: 1
        }, {
            bottom: 23
        })).toEqual({
            top: 1,
            bottom: 1
        });
        expect(correctEdgeInsertsObject({
            top: 1,
            bottom: -1
        }, {
            bottom: 23
        })).toEqual({
            bottom: 23
        });
        expect(correctEdgeInsertsObject({
            top: 1,
            bottom: 0
        }, {
            bottom: 23
        })).toEqual({
            top: 1,
            bottom: 0
        });
    });
});
