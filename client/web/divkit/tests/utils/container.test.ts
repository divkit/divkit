import { calcAdditionalPaddings, calcItemsGap } from '../../src/utils/container';

describe('container', () => {
    const separatorStyle = {
        width: 10,
        height: 10,
        borderRadius: 0,
        background: '#000'
    };

    const separatorStyle20 = {
        width: 20,
        height: 20,
        borderRadius: 0,
        background: '#000'
    };

    it('calcAdditionalPaddings', () => {
        expect(calcAdditionalPaddings('horizontal', null, null)).toEqual({});

        expect(calcAdditionalPaddings('horizontal', {
            style: separatorStyle
        }, null)).toEqual({});

        expect(calcAdditionalPaddings('horizontal', {
            style: separatorStyle,
            show_at_start: true
        }, null)).toEqual({
            left: 10
        });

        expect(calcAdditionalPaddings('horizontal', {
            style: separatorStyle,
            show_at_start: true,
            show_at_end: true
        }, null)).toEqual({
            left: 10,
            right: 10
        });

        expect(calcAdditionalPaddings('vertical', {
            style: separatorStyle,
            show_at_start: true,
            show_at_end: true
        }, null)).toEqual({
            top: 10,
            bottom: 10
        });

        expect(calcAdditionalPaddings('vertical', {
            style: separatorStyle,
            show_at_end: true
        }, null)).toEqual({
            bottom: 10
        });
    });

    it('calcItemsGap', () => {
        expect(calcItemsGap('horizontal', null, null)).toBe('0 0');

        expect(calcItemsGap('horizontal', {
            style: separatorStyle
        }, null)).toBe('0 0');

        expect(calcItemsGap('horizontal', {
            style: separatorStyle,
            show_between: true
        }, null)).toBe('0 1em');

        expect(calcItemsGap('vertical', {
            style: {
                width: 10,
                height: 20,
                borderRadius: 0,
                background: '#000'
            },
            show_between: true
        }, null)).toBe('2em 0');

        expect(calcItemsGap('horizontal', {
            style: separatorStyle,
            show_between: true
        }, {
            style: separatorStyle20,
            show_between: true
        })).toBe('2em 1em');

        expect(calcItemsGap('horizontal', null, {
            style: separatorStyle20,
            show_between: true
        })).toBe('2em 0');
    });
});
