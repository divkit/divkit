import { correctTabDelimiterStyle } from '../../src/utils/correctTabDelimiterStyle';

describe('correctTabDelimiterStyle', () => {
    test('simple', () => {
        expect(correctTabDelimiterStyle(undefined, {
            url: 'a'
        })).toEqual({
            url: 'a'
        });
        //@ts-expect-error Incorrect type
        expect(correctTabDelimiterStyle(123, {
            url: 'a'
        })).toEqual({
            url: 'a'
        });
        expect(correctTabDelimiterStyle({}, {
            url: 'a'
        })).toEqual({
            url: 'a'
        });

        expect(correctTabDelimiterStyle({
            image_url: 'b'
        }, {
            url: 'a'
        })).toEqual({
            url: 'b'
        });

        expect(correctTabDelimiterStyle({
            image_url: 'b',
            width: {
                type: 'fixed',
                value: 10
            }
        }, {
            url: 'a'
        })).toEqual({
            url: 'b',
            width: 10
        });

        expect(correctTabDelimiterStyle({
            image_url: 'b',
            width: {
                type: 'fixed',
                value: 10
            },
            height: {
                type: 'fixed',
                value: 20
            }
        }, {
            url: 'a'
        })).toEqual({
            url: 'b',
            width: 10,
            height: 20
        });
    });
});
