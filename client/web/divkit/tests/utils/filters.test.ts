import {
    describe,
    expect,
    test,
    vi
} from 'vitest';

import { getCssFilter } from '../../src/utils/filters';

describe('filters', () => {
    test('getCssFilter with incorrect object', () => {
        const logError = vi.fn();

        expect(getCssFilter([{
            type: 'blur',
            radius: undefined
        }], logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('getCssFilter with incorrect object 2', () => {
        const logError = vi.fn();

        expect(getCssFilter([{
            type: undefined,
            radius: undefined
        }], logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('getCssFilter with incorrect object 3', () => {
        const logError = vi.fn();

        expect(getCssFilter([{
            // @ts-expect-error Incorrect data
            type: 'double-rainbow'
        }], logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('getCssFilter with incorrect object 4', () => {
        const logError = vi.fn();

        expect(getCssFilter([{
            type: 'blur',
            radius: -4
        }], logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('getCssFilter with correct blur', () => {
        const logError = vi.fn();

        expect(getCssFilter([{
            type: 'blur',
            radius: 4
        }], logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });
});
