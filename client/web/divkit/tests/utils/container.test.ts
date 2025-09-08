import {
    describe,
    expect,
    test
} from 'vitest';

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

    test('calcAdditionalPaddings', () => {
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

    test('calcAdditionalPaddings with margins', () => {
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

    test('calcItemsGap', () => {
        expect(calcItemsGap({
            orientation: 'horizontal',
            separator: null,
            lineSeparator: null,
            itemSpacing: 0,
            lineSpacing: 0
        })).toBe('0 0');

        expect(calcItemsGap({
            orientation: 'horizontal',
            separator: null,
            lineSeparator: null,
            itemSpacing: 10,
            lineSpacing: 0
        })).toBe('0 1em');

        expect(calcItemsGap({
            orientation: 'horizontal',
            separator: null,
            lineSeparator: null,
            itemSpacing: 0,
            lineSpacing: 10
        })).toBe('1em 0');

        expect(calcItemsGap({
            orientation: 'vertical',
            separator: null,
            lineSeparator: null,
            itemSpacing: 10,
            lineSpacing: 0
        })).toBe('1em 0');

        expect(calcItemsGap({
            orientation: 'vertical',
            separator: null,
            lineSeparator: null,
            itemSpacing: 0,
            lineSpacing: 10
        })).toBe('0 1em');

        expect(calcItemsGap({
            orientation: 'horizontal',
            separator: {
                style: separatorStyle,
                margins: emptyMargins
            },
            lineSeparator: null,
            itemSpacing: 0,
            lineSpacing: 0
        })).toBe('0 0');

        expect(calcItemsGap({
            orientation: 'horizontal',
            separator: {
                style: separatorStyle,
                show_between: true,
                margins: emptyMargins
            },
            lineSeparator: null,
            itemSpacing: 0,
            lineSpacing: 0
        })).toBe('0 1em');

        expect(calcItemsGap({
            orientation: 'horizontal',
            separator: {
                style: separatorStyle,
                show_between: true,
                margins: emptyMargins
            },
            lineSeparator: null,
            itemSpacing: 20,
            lineSpacing: 0
        })).toBe('0 1em');

        expect(calcItemsGap({
            orientation: 'vertical',
            separator: {
                style: {
                    width: 10,
                    height: 20,
                    borderRadius: 0,
                    background: '#000'
                },
                show_between: true,
                margins: emptyMargins
            },
            lineSeparator: null,
            itemSpacing: 0,
            lineSpacing: 0
        })).toBe('2em 0');

        expect(calcItemsGap({
            orientation: 'horizontal',
            separator: {
                style: separatorStyle,
                show_between: true,
                margins: emptyMargins
            },
            lineSeparator: {
                style: separatorStyle20,
                show_between: true,
                margins: emptyMargins
            },
            itemSpacing: 0,
            lineSpacing: 0
        })).toBe('2em 1em');

        expect(calcItemsGap({
            orientation: 'horizontal',
            separator: null,
            lineSeparator: {
                style: separatorStyle20,
                show_between: true,
                margins: emptyMargins
            },
            itemSpacing: 0,
            lineSpacing: 0
        })).toBe('2em 0');
    });

    test('calcItemsGap with margins', () => {
        expect(calcItemsGap({
            orientation: 'horizontal',
            separator: {
                style: separatorStyle,
                show_between: true,
                margins: {
                    ...emptyMargins,
                    left: 10
                }
            },
            lineSeparator: null,
            itemSpacing: 0,
            lineSpacing: 0
        })).toBe('0 2em');

        expect(calcItemsGap({
            orientation: 'vertical',
            separator: {
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
            },
            lineSeparator: null,
            itemSpacing: 0,
            lineSpacing: 0
        })).toBe('3em 0');

        expect(calcItemsGap({
            orientation: 'horizontal',
            separator: {
                style: separatorStyle,
                show_between: true,
                margins: {
                    ...emptyMargins,
                    left: 20
                }
            },
            lineSeparator: {
                style: separatorStyle20,
                show_between: true,
                margins: {
                    ...emptyMargins,
                    top: 10
                }
            },
            itemSpacing: 0,
            lineSpacing: 0
        })).toBe('3em 3em');

        expect(calcItemsGap({
            orientation: 'horizontal',
            separator: null,
            lineSeparator: {
                style: separatorStyle20,
                show_between: true,
                margins: {
                    ...emptyMargins,
                    left: 10
                }
            },
            itemSpacing: 0,
            lineSpacing: 0
        })).toBe('2em 0');

        expect(calcItemsGap({
            orientation: 'horizontal',
            separator: null,
            lineSeparator: {
                style: separatorStyle20,
                show_between: true,
                margins: {
                    ...emptyMargins,
                    top: 10
                }
            },
            itemSpacing: 0,
            lineSpacing: 0
        })).toBe('3em 0');
    });
});
