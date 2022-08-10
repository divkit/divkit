import { correctAlignmentVertical } from '../../src/utils/correctAlignmentVertical';

describe('correctAlignmentVertical', () => {
    test('simple', () => {
        expect(correctAlignmentVertical(undefined, 'top')).toBe('top');
        expect(correctAlignmentVertical('top', 'center')).toBe('top');
        expect(correctAlignmentVertical('center', 'top')).toBe('center');
        expect(correctAlignmentVertical('bottom', 'top')).toBe('bottom');
        expect(correctAlignmentVertical('smth', 'top')).toBe('top');
    });
});
