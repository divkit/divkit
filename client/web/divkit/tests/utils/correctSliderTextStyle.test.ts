import { correctSliderTextStyle } from '../../src/utils/correctSliderTextStyle';

describe('correctSliderTextStyle', () => {
    test('simple', () => {
        const defaultVal = {
            fontSize: '1em',
            fontWeight: 400,
            textColor: '#f00'
        };

        expect(correctSliderTextStyle(undefined, defaultVal)).toEqual(defaultVal);

        // @ts-expect-error Incorrect data
        expect(correctSliderTextStyle({}, defaultVal)).toEqual(defaultVal);

        expect(correctSliderTextStyle({
            font_size: 15
        }, defaultVal)).toEqual({
            fontSize: '1.5em',
            fontWeight: undefined,
            textColor: '#000'
        });

        expect(correctSliderTextStyle({
            font_size: 15,
            font_weight: 'light',
            text_color: '#fc0'
        }, defaultVal)).toEqual({
            fontSize: '1.5em',
            fontWeight: 300,
            textColor: '#ffcc00'
        });

        expect(correctSliderTextStyle({
            font_size: 15,
            font_weight: 'light',
            text_color: '#fc0',
            offset: {
                x: {
                    value: 10
                },
                y: {
                    value: 20
                }
            }
        }, defaultVal)).toEqual({
            fontSize: '1.5em',
            fontWeight: 300,
            textColor: '#ffcc00',
            offset: {
                x: 10,
                y: 20
            }
        });
    });
});
