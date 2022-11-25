import { correctBorderRadiusObject } from '../../src/utils/correctBorderRadiusObject';

describe('correctBorderRadiusObject', () => {
    test('simple', () => {
        expect(correctBorderRadiusObject({
            'top-left': 1
        }, {
            'bottom-left': 23
        })).toEqual({
            'top-left': 1
        });
        expect(correctBorderRadiusObject({
            'top-left': -1
        }, {
            'bottom-left': 23
        })).toEqual({
            'bottom-left': 23
        });
        expect(correctBorderRadiusObject({
            'top-left': 1,
            'top-right': 2
        }, {
            'bottom-left': 23
        })).toEqual({
            'top-left': 1,
            'top-right': 2
        });
        expect(correctBorderRadiusObject({
            'top-left': 1,
            'top-right': 0
        }, {
            'bottom-left': 23
        })).toEqual({
            'top-left': 1,
            'top-right': 0
        });
        expect(correctBorderRadiusObject({
            'top-left': 1,
            'top-right': -2
        }, {
            'bottom-left': 23
        })).toEqual({
            'bottom-left': 23
        });
    });
});
