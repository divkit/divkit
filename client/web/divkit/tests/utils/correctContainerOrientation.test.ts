import {
    describe,
    expect,
    test
} from 'vitest';

import { correctContainerOrientation } from '../../src/utils/correctContainerOrientation';

describe('correctContainerOrientation', () => {
    test('simple', () => {
        expect(correctContainerOrientation(undefined, 'vertical')).toBe('vertical');
        expect(correctContainerOrientation('vertical', 'horizontal')).toBe('vertical');
        expect(correctContainerOrientation('horizontal', 'vertical')).toBe('horizontal');
        expect(correctContainerOrientation('overlap', 'vertical')).toBe('overlap');
        expect(correctContainerOrientation('smth', 'vertical')).toBe('vertical');
    });
});
