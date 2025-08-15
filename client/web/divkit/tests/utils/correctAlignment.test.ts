import {
    describe,
    expect,
    test
} from 'vitest';

import { correctAlignment } from '../../src/utils/correctAlignment';

describe('correctAlignment', () => {
    test('simple', () => {
        expect(correctAlignment(undefined, 'start')).toBe('start');
        expect(correctAlignment('start', 'center')).toBe('start');
        expect(correctAlignment('center', 'start')).toBe('center');
        expect(correctAlignment('end', 'start')).toBe('end');
        expect(correctAlignment('smth', 'start')).toBe('start');
    });
});
