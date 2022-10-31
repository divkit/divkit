import { getCssFilter } from '../../src/utils/filters';

describe('filters', () => {
    it('getCssFilter with incorrect object', () => {
        const logError = jest.fn();

        expect(getCssFilter([{
            type: 'blur',
            radius: undefined
        }], logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    it('getCssFilter with incorrect object 2', () => {
        const logError = jest.fn();

        expect(getCssFilter([{
            type: undefined,
            radius: undefined
        }], logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    it('getCssFilter with incorrect object 3', () => {
        const logError = jest.fn();

        expect(getCssFilter([{
            // @ts-expect-error Incorrect data
            type: 'double-rainbow'
        }], logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    it('getCssFilter with incorrect object 4', () => {
        const logError = jest.fn();

        expect(getCssFilter([{
            type: 'blur',
            radius: -4
        }], logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    it('getCssFilter with correct blur', () => {
        const logError = jest.fn();

        expect(getCssFilter([{
            type: 'blur',
            radius: 4
        }], logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });
});
