import { DivCustom } from '../../src';

describe('DivCustom tests', (): void => {
    it('should create simple block', (): void => {
        const block = new DivCustom({
            custom_type: 'test_type',
            custom_props: {
                string_prop: 'Custom string',
                integer_prop: 100,
            },
        });

        expect(block).toMatchSnapshot();
    });
});
