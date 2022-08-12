import { expression } from '../../src';

describe('div expression', () => {
    it('should be represented by string in json', () => {
        const expr = expression('@{test}');
        expect(JSON.parse(JSON.stringify(expr))).toBe('@{test}');
    });

    it('should be converted to string', () => {
        const expr = expression('@{test}');
        expect(`${expr}`).toBe('@{test}');
    });
});
