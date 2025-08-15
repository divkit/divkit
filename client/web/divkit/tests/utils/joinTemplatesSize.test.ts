import {
    describe,
    expect,
    test
} from 'vitest';

import { joinTemplateSizes } from '../../src/utils/joinTemplateSizes';

describe('joinTemplateSizes', () => {
    test('simple', () => {
        expect(joinTemplateSizes(['100%', 'max-content', 'max-content', 'max-content', '100%']))
            .toBe('100% repeat(3, max-content) 100%');
        expect(joinTemplateSizes(['100%', '100%', 'max-content', '100%', '100%']))
            .toBe('repeat(2, 100%) max-content repeat(2, 100%)');
        expect(joinTemplateSizes(['100%', 'max-content', 'auto'])).toBe('100% max-content auto');
        expect(joinTemplateSizes(['100%', '100%'])).toBe('repeat(2, 100%)');
        expect(joinTemplateSizes(['100%'])).toBe('100%');
        expect(joinTemplateSizes([])).toBe('');
    });
});
