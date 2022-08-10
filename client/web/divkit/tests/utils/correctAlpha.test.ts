import { correctAlpha } from '../../src/utils/correctAlpha';

describe('correctAlpha', () => {
    test('simple', () => {
        expect(correctAlpha(1, .5)).toBe(1);
        expect(correctAlpha(1.1, .5)).toBe(.5);
        expect(correctAlpha(0, .5)).toBe(0);
        expect(correctAlpha(-.5, .5)).toBe(.5);
        expect(correctAlpha(undefined, .5)).toBe(.5);
    });
});
