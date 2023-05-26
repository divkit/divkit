import { correctContentAlignmentVertical } from '../../src/utils/correctContentAlignmentVertical';

describe('correctContentAlignmentVertical', () => {
    test('simple', () => {
        expect(correctContentAlignmentVertical(undefined, 'top')).toBe('top');
        expect(correctContentAlignmentVertical('top', 'center')).toBe('top');
        expect(correctContentAlignmentVertical('center', 'top')).toBe('center');
        expect(correctContentAlignmentVertical('bottom', 'top')).toBe('bottom');
        expect(correctContentAlignmentVertical('baseline', 'top')).toBe('baseline');
        expect(correctContentAlignmentVertical('space-between', 'top')).toBe('space-between');
        expect(correctContentAlignmentVertical('space-around', 'top')).toBe('space-around');
        expect(correctContentAlignmentVertical('space-evenly', 'top')).toBe('space-evenly');
        expect(correctContentAlignmentVertical('smth', 'top')).toBe('top');
    });
});
