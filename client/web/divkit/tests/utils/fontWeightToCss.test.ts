import {
    describe,
    expect,
    test
} from 'vitest';

import { fontWeightToCss } from '../../src/utils/fontWeightToCss';

describe('fontWeightToCss', () => {
    test('simple', () => {
        expect(fontWeightToCss(undefined)).toBe(undefined);

        // @ts-expect-error Incorrect value
        expect(fontWeightToCss('abc')).toBe(undefined);

        expect(fontWeightToCss('light')).toBe(300);
        expect(fontWeightToCss('regular')).toBe(400);
        expect(fontWeightToCss('medium')).toBe(500);
        expect(fontWeightToCss('bold')).toBe(700);
    });
});
