import { correctBooleanInt } from '../../src/utils/correctBooleanInt';

describe('correctBooleanInt', () => {
    test('simple', () => {
        const logError = jest.fn();

        expect(correctBooleanInt(2, false, logError)).toBe(false);
        expect(correctBooleanInt(2, true, logError)).toBe(true);
        expect(correctBooleanInt(1, false, logError)).toBe(true);
        expect(correctBooleanInt(1, true, logError)).toBe(true);
        expect(correctBooleanInt(0, false, logError)).toBe(false);
        expect(correctBooleanInt(0, true, logError)).toBe(false);
        expect(correctBooleanInt(true, false, logError)).toBe(true);
        expect(correctBooleanInt(true, true, logError)).toBe(true);
        expect(correctBooleanInt(false, false, logError)).toBe(false);
        expect(correctBooleanInt(false, true, logError)).toBe(false);
        expect(correctBooleanInt(undefined, false, logError)).toBe(false);
        expect(correctBooleanInt(undefined, true, logError)).toBe(true);

        expect(logError.mock.calls).toMatchSnapshot();
    });
});
