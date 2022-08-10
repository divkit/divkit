import { correctAlignmentHorizontal } from '../../src/utils/correctAlignmentHorizontal';

describe('correctAlignmentHorizontal', () => {
    test('simple', () => {
        expect(correctAlignmentHorizontal(undefined, 'left')).toBe('left');
        expect(correctAlignmentHorizontal('left', 'center')).toBe('left');
        expect(correctAlignmentHorizontal('center', 'left')).toBe('center');
        expect(correctAlignmentHorizontal('right', 'left')).toBe('right');
        expect(correctAlignmentHorizontal('smth', 'left')).toBe('left');
    });
});
