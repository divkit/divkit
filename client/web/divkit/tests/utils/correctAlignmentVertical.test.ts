import {
    describe,
    expect,
    test
} from 'vitest';

import { correctAlignmentVertical } from '../../src/utils/correctAlignmentVertical';

describe('correctAlignmentVertical', () => {
    test('simple', () => {
        expect(correctAlignmentVertical(undefined, 'start')).toBe('start');
        expect(correctAlignmentVertical('top', 'center')).toBe('start');
        expect(correctAlignmentVertical('center', 'start')).toBe('center');
        expect(correctAlignmentVertical('bottom', 'start')).toBe('end');
        expect(correctAlignmentVertical('baseline', 'start')).toBe('baseline');
        expect(correctAlignmentVertical('smth', 'start')).toBe('start');
    });
});
