import { correctContentAlignmentHorizontal } from '../../src/utils/correctContentAlignmentHorizontal';

describe('correctContentAlignmentHorizontal', () => {
    test('simple', () => {
        expect(correctContentAlignmentHorizontal(undefined, 'left')).toBe('left');
        expect(correctContentAlignmentHorizontal('left', 'center')).toBe('left');
        expect(correctContentAlignmentHorizontal('center', 'left')).toBe('center');
        expect(correctContentAlignmentHorizontal('right', 'left')).toBe('right');
        expect(correctContentAlignmentHorizontal('space-between', 'left')).toBe('space-between');
        expect(correctContentAlignmentHorizontal('space-around', 'left')).toBe('space-around');
        expect(correctContentAlignmentHorizontal('space-evenly', 'left')).toBe('space-evenly');
        expect(correctContentAlignmentHorizontal('smth', 'left')).toBe('left');
    });
});
