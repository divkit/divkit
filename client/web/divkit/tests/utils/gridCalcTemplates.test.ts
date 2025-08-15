import {
    describe,
    expect,
    test
} from 'vitest';

import { gridCalcTemplates } from '../../src/utils/gridCalcTemplates';

describe('fontWeightToCss', () => {
    test('simple', () => {
        expect(gridCalcTemplates([1, 1, 1], [0, 0, 0], 3)).toBe('1fr 1fr 1fr');

        expect(gridCalcTemplates([.5, .5, .5], [0, 0, 0], 3)).toBe('1fr 1fr 1fr');
        expect(gridCalcTemplates([.5, .25, .5], [0, 0, 0], 3)).toBe('2fr 1fr 2fr');
        expect(gridCalcTemplates([.5, .3, .5], [0, 0, 0], 3)).toBe('1.667fr 1fr 1.667fr');

        expect(gridCalcTemplates([1, 1, 1], [50, 0, 0], 3)).toBe('minmax(5em,1fr) minmax(5em,1fr) minmax(5em,1fr)');

        expect(gridCalcTemplates([0, 0, 0], [0, 0, 0], 3)).toBe('auto auto auto');
    });
});
