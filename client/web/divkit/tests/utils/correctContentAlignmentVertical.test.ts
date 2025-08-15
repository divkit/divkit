import {
    describe,
    expect,
    test
} from 'vitest';

import { correctContentAlignmentVertical } from '../../src/utils/correctContentAlignmentVertical';

describe('correctContentAlignmentVertical', () => {
    test('simple', () => {
        expect(correctContentAlignmentVertical(undefined, 'start')).toBe('start');
        expect(correctContentAlignmentVertical('top', 'center')).toBe('start');
        expect(correctContentAlignmentVertical('center', 'start')).toBe('center');
        expect(correctContentAlignmentVertical('bottom', 'start')).toBe('end');
        expect(correctContentAlignmentVertical('baseline', 'start')).toBe('baseline');
        expect(correctContentAlignmentVertical('space-between', 'start')).toBe('space-between');
        expect(correctContentAlignmentVertical('space-around', 'start')).toBe('space-around');
        expect(correctContentAlignmentVertical('space-evenly', 'start')).toBe('space-evenly');
        expect(correctContentAlignmentVertical('smth', 'start')).toBe('start');
    });
});
