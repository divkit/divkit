import { DivText } from '../../src';

describe('DivExtension tests', (): void => {
    it('should create block with extension', (): void => {
        const block = new DivText({
            text: 'Some text',
            extensions: [
                {
                    id: 'text_extension',
                    params: {
                        string_param: 'param1',
                        integer_param: 100,
                    },
                },
            ],
        });

        expect(block).toMatchSnapshot();
    });
});
