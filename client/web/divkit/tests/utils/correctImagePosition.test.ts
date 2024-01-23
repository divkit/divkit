import { correctImagePosition } from '../../src/utils/correctImagePosition';

describe('correctImagePosition', () => {
    test('simple', () => {
        expect(correctImagePosition({}, 'ltr', 'default')).toBe('50% 50%');
        expect(correctImagePosition({
            content_alignment_horizontal: 'left'
        }, 'ltr', 'default')).toBe('0% 50%');
        expect(correctImagePosition({
            content_alignment_horizontal: 'left',
            content_alignment_vertical: 'bottom'
        }, 'ltr', 'default')).toBe('0% 100%');
        expect(correctImagePosition({
            content_alignment_horizontal: 'left',
            // @ts-expect-error wrong type
            content_alignment_vertical: 'topmost'
        }, 'ltr', 'default')).toBe('default');
    });

    test('ltr', () => {
        expect(correctImagePosition({}, 'ltr', 'default')).toBe('50% 50%');
        expect(correctImagePosition({
            content_alignment_horizontal: 'start'
        }, 'ltr', 'default')).toBe('0% 50%');
        expect(correctImagePosition({
            content_alignment_horizontal: 'start',
            content_alignment_vertical: 'bottom'
        }, 'ltr', 'default')).toBe('0% 100%');
        expect(correctImagePosition({
            content_alignment_horizontal: 'start',
            // @ts-expect-error wrong type
            content_alignment_vertical: 'topmost'
        }, 'ltr', 'default')).toBe('default');
    });

    test('rtl', () => {
        expect(correctImagePosition({}, 'rtl', 'default')).toBe('50% 50%');
        expect(correctImagePosition({
            content_alignment_horizontal: 'start'
        }, 'rtl', 'default')).toBe('100% 50%');
        expect(correctImagePosition({
            content_alignment_horizontal: 'start',
            content_alignment_vertical: 'bottom'
        }, 'rtl', 'default')).toBe('100% 100%');
        expect(correctImagePosition({
            content_alignment_horizontal: 'start',
            // @ts-expect-error wrong type
            content_alignment_vertical: 'topmost'
        }, 'rtl', 'default')).toBe('default');
    });
});
