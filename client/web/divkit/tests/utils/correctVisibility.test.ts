import {
    describe,
    expect,
    test
} from 'vitest';

import { correctVisibility } from '../../src/utils/correctVisibility';

describe('correctVisibility', () => {
    test('simple', () => {
        expect(correctVisibility(undefined, 'visible')).toBe('visible');
        expect(correctVisibility('visible', 'gone')).toBe('visible');
        expect(correctVisibility('invisible', 'visible')).toBe('invisible');
        expect(correctVisibility('gone', 'visible')).toBe('gone');
        expect(correctVisibility('smth', 'visible')).toBe('visible');
    });
});
