import { correctColor } from '../../src/utils/correctColor';

describe('correctColor', () => {
    test('simple', () => {
        expect(correctColor('#fc0', 1, 'default')).toBe('#ffcc00');
        expect(correctColor('#fco', 1, 'default')).toBe('default');
    });
});
