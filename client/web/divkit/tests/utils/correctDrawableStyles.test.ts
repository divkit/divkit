import { correctDrawableStyle } from '../../src/utils/correctDrawableStyles';

describe('correctDrawableStyle', () => {
    test('simple', () => {
        const defaultVal = {
            width: 10,
            height: 10,
            borderRadius: 5,
            background: '#000'
        };

        const types = ['rounded_rectangle', 'circle'];

        expect(correctDrawableStyle(undefined, types, defaultVal)).toEqual(defaultVal);

        // @ts-expect-error Incorrect data
        expect(correctDrawableStyle({}, types, defaultVal)).toEqual(defaultVal);

        expect(correctDrawableStyle({
            // @ts-expect-error Incorrect data
            type: 'abcde'
        }, types, defaultVal)).toEqual(defaultVal);

        expect(correctDrawableStyle({
            type: 'shape_drawable',
            // @ts-expect-error Incorrect data
            color: 123
        }, types, defaultVal)).toEqual(defaultVal);

        // @ts-expect-error Incorrect data
        expect(correctDrawableStyle({
            type: 'shape_drawable',
            color: '#fc0',
        }, types, defaultVal)).toEqual(defaultVal);

        expect(correctDrawableStyle({
            type: 'shape_drawable',
            color: '#fc0',
            shape: {
                type: 'rounded_rectangle'
            }
        }, types, defaultVal)).toEqual({
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
        }, types, defaultVal)).toEqual({
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
        }, types, defaultVal)).toEqual({
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
        }, types, defaultVal)).toEqual({
            width: 100,
            height: 200,
            borderRadius: 10,
            background: '#ffcc00',
            boxShadow: 'inset 0 0 0 1em #ff0000'
        });

        expect(correctDrawableStyle({
            type: 'shape_drawable',
            color: '#fc0',
            shape: {
                type: 'circle',
                radius: {
                    type: 'fixed',
                    value: 100
                }
            }
        }, types, defaultVal)).toEqual({
            width: 100,
            height: 100,
            borderRadius: 100,
            background: '#ffcc00'
        });

        expect(correctDrawableStyle({
            type: 'shape_drawable',
            color: '#fc0',
            shape: {
                type: 'circle',
                radius: {
                    type: 'fixed',
                    value: 100
                }
            }
        }, [], defaultVal)).toEqual(defaultVal);

        expect(correctDrawableStyle({
            type: 'shape_drawable',
            color: '#fc0',
            shape: {
                type: 'rounded_rectangle'
            }
        }, [], defaultVal)).toEqual(defaultVal);
    });
});
