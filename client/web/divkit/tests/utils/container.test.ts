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

    const emptyMargins = {
        top: 0,
        right: 0,
        bottom: 0,
        left: 0
    };

    it('calcAdditionalPaddings', () => {
        expect(calcAdditionalPaddings('horizontal', null, null)).toEqual({});

        expect(calcAdditionalPaddings('horizontal', {
            style: separatorStyle,
            margins: emptyMargins
        }, null)).toEqual({});

        expect(calcAdditionalPaddings('horizontal', {
            style: separatorStyle,
            show_at_start: true,
            margins: emptyMargins
        }, null)).toEqual({
            left: 10
        });

        expect(calcAdditionalPaddings('horizontal', {
            style: separatorStyle,
            show_at_start: true,
            show_at_end: true,
            margins: emptyMargins
        }, null)).toEqual({
            left: 10,
            right: 10
        });

        expect(calcAdditionalPaddings('vertical', {
            style: separatorStyle,
            show_at_start: true,
            show_at_end: true,
            margins: emptyMargins
        }, null)).toEqual({
            top: 10,
            bottom: 10
        });

        expect(calcAdditionalPaddings('vertical', {
            style: separatorStyle,
            show_at_end: true,
            margins: emptyMargins
        }, null)).toEqual({
            bottom: 10
        });
    });

    it('calcAdditionalPaddings with margins', () => {
        expect(calcAdditionalPaddings('horizontal', {
            style: separatorStyle,
            show_at_start: true,
            margins: {
                ...emptyMargins,
                left: 10
            }
        }, null)).toEqual({
            left: 20
        });

        expect(calcAdditionalPaddings('horizontal', {
            style: separatorStyle,
            show_at_start: true,
            show_at_end: true,
            margins: {
                ...emptyMargins,
                right: 10
            }
        }, null)).toEqual({
            left: 20,
            right: 20
        });

        expect(calcAdditionalPaddings('vertical', {
            style: separatorStyle,
            show_at_start: true,
            show_at_end: true,
            margins: {
                ...emptyMargins,
                top: 10
            }
        }, null)).toEqual({
            top: 20,
            bottom: 20
        });

        expect(calcAdditionalPaddings('vertical', {
            style: separatorStyle,
            show_at_end: true,
            margins: {
                ...emptyMargins,
                bottom: 10
            }
        }, null)).toEqual({
            bottom: 20
        });
    });

    it('calcItemsGap', () => {
        expect(calcItemsGap('horizontal', null, null)).toBe('0 0');

        expect(calcItemsGap('horizontal', {
            style: separatorStyle,
            margins: emptyMargins
        }, null)).toBe('0 0');

        expect(calcItemsGap('horizontal', {
            style: separatorStyle,
            show_between: true,
            margins: emptyMargins
        }, null)).toBe('0 1em');

        expect(calcItemsGap('vertical', {
            style: {
                width: 10,
                height: 20,
                borderRadius: 0,
                background: '#000'
            },
            show_between: true,
            margins: emptyMargins
        }, null)).toBe('2em 0');

        expect(calcItemsGap('horizontal', {
            style: separatorStyle,
            show_between: true,
            margins: emptyMargins
        }, {
            style: separatorStyle20,
            show_between: true,
            margins: emptyMargins
        })).toBe('2em 1em');

        expect(calcItemsGap('horizontal', null, {
            style: separatorStyle20,
            show_between: true,
            margins: emptyMargins
        })).toBe('2em 0');
    });

    it('calcItemsGap with margins', () => {
        expect(calcItemsGap('horizontal', {
            style: separatorStyle,
            show_between: true,
            margins: {
                ...emptyMargins,
                left: 10
            }
        }, null)).toBe('0 2em');

        expect(calcItemsGap('vertical', {
            style: {
                width: 10,
                height: 20,
                borderRadius: 0,
                background: '#000'
            },
            show_between: true,
            margins: {
                ...emptyMargins,
                top: 10
            }
        }, null)).toBe('3em 0');

        expect(calcItemsGap('horizontal', {
            style: separatorStyle,
            show_between: true,
            margins: {
                ...emptyMargins,
                left: 20
            }
        }, {
            style: separatorStyle20,
            show_between: true,
            margins: {
                ...emptyMargins,
                top: 10
            }
        })).toBe('3em 3em');

        expect(calcItemsGap('horizontal', null, {
            style: separatorStyle20,
            show_between: true,
            margins: {
                ...emptyMargins,
                left: 10
            }
        })).toBe('2em 0');

        expect(calcItemsGap('horizontal', null, {
            style: separatorStyle20,
            show_between: true,
            margins: {
                ...emptyMargins,
                top: 10
            }
        })).toBe('3em 0');
    });
});
