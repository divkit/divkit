import { correctDrawableStyle } from '../../src/utils/correctDrawableStyles';

describe('correctDrawableStyle', () => {
    test('simple', () => {
        const defaultVal = {
            width: 10,
            height: 10,
            borderRadius: 5,
            background: '#000'
        };

        expect(correctDrawableStyle(undefined, defaultVal)).toEqual(defaultVal);

        // @ts-expect-error Incorrect data
        expect(correctDrawableStyle({}, defaultVal)).toEqual(defaultVal);

        expect(correctDrawableStyle({
            // @ts-expect-error Incorrect data
            type: 'abcde'
        }, defaultVal)).toEqual(defaultVal);

        expect(correctDrawableStyle({
            type: 'shape_drawable',
            // @ts-expect-error Incorrect data
            color: 123
        }, defaultVal)).toEqual(defaultVal);

        // @ts-expect-error Incorrect data
        expect(correctDrawableStyle({
            type: 'shape_drawable',
            color: '#fc0',
        }, defaultVal)).toEqual(defaultVal);

        expect(correctDrawableStyle({
            type: 'shape_drawable',
            color: '#fc0',
            shape: {
                type: 'rounded_rectangle'
            }
        }, defaultVal)).toEqual({
            width: 10,
            height: 10,
            borderRadius: 5,
            background: '#ffcc00'
        });

        expect(correctDrawableStyle({
            type: 'shape_drawable',
            color: '#fc0',
            shape: {
                type: 'rounded_rectangle',
                item_width: {
                    type: 'fixed',
                    value: 100
                }
            }
        }, defaultVal)).toEqual({
            width: 100,
            height: 10,
            borderRadius: 5,
            background: '#ffcc00'
        });

        expect(correctDrawableStyle({
            type: 'shape_drawable',
            color: '#fc0',
            shape: {
                type: 'rounded_rectangle',
                item_width: {
                    type: 'fixed',
                    value: 100
                },
                item_height: {
                    type: 'fixed',
                    value: 200
                },
                corner_radius: {
                    type: 'fixed',
                    value: 10
                }
            }
        }, defaultVal)).toEqual({
            width: 100,
            height: 200,
            borderRadius: 10,
            background: '#ffcc00'
        });

        expect(correctDrawableStyle({
            type: 'shape_drawable',
            color: '#fc0',
            shape: {
                type: 'rounded_rectangle',
                item_width: {
                    type: 'fixed',
                    value: 100
                },
                item_height: {
                    type: 'fixed',
                    value: 200
                },
                corner_radius: {
                    type: 'fixed',
                    value: 10
                }
            },
            stroke: {
                width: 10,
                color: '#f00'
            }
        }, defaultVal)).toEqual({
            width: 100,
            height: 200,
            borderRadius: 10,
            background: '#ffcc00',
            boxShadow: 'inset 0 0 0 1em #ff0000'
        });
    });
});
