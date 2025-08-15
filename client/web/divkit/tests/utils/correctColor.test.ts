import {
    describe,
    expect,
    test
} from 'vitest';

import { correctColor } from '../../src/utils/correctColor';

describe('correctColor', () => {
    test('simple', () => {
        expect(correctColor('#fc0', 1, 'default')).toBe('#ffcc00');
        expect(correctColor('#fco', 1, 'default')).toBe('default');
    });

    test('incorrect', () => {
        // @ts-expect-error Wrong type
        expect(correctColor({}, 1, 'default')).toBe('default');
        // @ts-expect-error Wrong type
        expect(correctColor(111, 1, 'default')).toBe('default');
        // @ts-expect-error Wrong type
        expect(correctColor(null, 1, 'default')).toBe('default');
        expect(correctColor(undefined, 1, 'default')).toBe('default');
    });
});
