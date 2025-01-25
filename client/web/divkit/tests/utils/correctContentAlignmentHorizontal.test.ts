import {
    describe,
    expect,
    test
} from 'vitest';

import { correctContentAlignmentHorizontal } from '../../src/utils/correctContentAlignmentHorizontal';

describe('correctContentAlignmentHorizontal', () => {
    test('simple', () => {
        expect(correctContentAlignmentHorizontal(undefined, 'ltr', 'start')).toBe('start');
        expect(correctContentAlignmentHorizontal('left', 'ltr', 'center')).toBe('start');
        expect(correctContentAlignmentHorizontal('center', 'ltr', 'start')).toBe('center');
        expect(correctContentAlignmentHorizontal('right', 'ltr', 'start')).toBe('end');
        expect(correctContentAlignmentHorizontal('space-between', 'ltr', 'start')).toBe('space-between');
        expect(correctContentAlignmentHorizontal('space-around', 'ltr', 'start')).toBe('space-around');
        expect(correctContentAlignmentHorizontal('space-evenly', 'ltr', 'start')).toBe('space-evenly');
        expect(correctContentAlignmentHorizontal('smth', 'ltr', 'start')).toBe('start');

        expect(correctContentAlignmentHorizontal(undefined, 'rtl', 'start')).toBe('start');
        expect(correctContentAlignmentHorizontal('left', 'rtl', 'center')).toBe('end');
        expect(correctContentAlignmentHorizontal('center', 'rtl', 'start')).toBe('center');
        expect(correctContentAlignmentHorizontal('right', 'rtl', 'start')).toBe('start');
        expect(correctContentAlignmentHorizontal('space-between', 'rtl', 'start')).toBe('space-between');
        expect(correctContentAlignmentHorizontal('space-around', 'rtl', 'start')).toBe('space-around');
        expect(correctContentAlignmentHorizontal('space-evenly', 'rtl', 'start')).toBe('space-evenly');
        expect(correctContentAlignmentHorizontal('smth', 'rtl', 'start')).toBe('start');
    });
});
