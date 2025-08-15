import {
    describe,
    expect,
    test
} from 'vitest';

import { correctGeneralOrientation } from '../../src/utils/correctGeneralOrientation';

describe('correctGeneralOrientation', () => {
    test('simple', () => {
        expect(correctGeneralOrientation(undefined, 'vertical')).toBe('vertical');
        expect(correctGeneralOrientation('vertical', 'horizontal')).toBe('vertical');
        expect(correctGeneralOrientation('horizontal', 'vertical')).toBe('horizontal');
        expect(correctGeneralOrientation('smth', 'vertical')).toBe('vertical');
    });
});
