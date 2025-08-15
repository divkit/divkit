import {
    describe,
    expect,
    test
} from 'vitest';

import { correctAlignmentHorizontal } from '../../src/utils/correctAlignmentHorizontal';

describe('correctAlignmentHorizontal', () => {
    test('simple', () => {
        expect(correctAlignmentHorizontal(undefined, 'ltr', 'start')).toBe('start');
        expect(correctAlignmentHorizontal('left', 'ltr', 'center')).toBe('start');
        expect(correctAlignmentHorizontal('center', 'ltr', 'start')).toBe('center');
        expect(correctAlignmentHorizontal('right', 'ltr', 'start')).toBe('end');
        expect(correctAlignmentHorizontal('smth', 'ltr', 'start')).toBe('start');

        expect(correctAlignmentHorizontal(undefined, 'rtl', 'start')).toBe('start');
        expect(correctAlignmentHorizontal('left', 'rtl', 'center')).toBe('end');
        expect(correctAlignmentHorizontal('center', 'rtl', 'start')).toBe('center');
        expect(correctAlignmentHorizontal('right', 'rtl', 'start')).toBe('start');
        expect(correctAlignmentHorizontal('smth', 'rtl', 'start')).toBe('start');
    });
});
