import { correctImagePosition } from '../../src/utils/correctImagePosition';

describe('correctImagePosition', () => {
    test('simple', () => {
        expect(correctImagePosition({}, 'default')).toBe('50% 50%');
        expect(correctImagePosition({
            content_alignment_horizontal: 'left'
        }, 'default')).toBe('0% 50%');
        expect(correctImagePosition({
            content_alignment_horizontal: 'left',
            content_alignment_vertical: 'bottom'
        }, 'default')).toBe('0% 100%');
        expect(correctImagePosition({
            content_alignment_horizontal: 'left',
            // @ts-expect-error wrong type
            content_alignment_vertical: 'topmost'
        }, 'default')).toBe('default');
    });
});
