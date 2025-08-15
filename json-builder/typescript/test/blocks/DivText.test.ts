import { DivText, expression, fixed } from '../../src';

describe('DivText tests', (): void => {
    it('should create simple block', (): void => {
        const block = new DivText({
            text: 'Some text',
            font_size: 18,
            font_size_unit: 'sp',
            font_weight: 'medium',
        });

        expect(block).toMatchSnapshot();
    });

    it('should create block with expressions', (): void => {
        const block = new DivText({
            text: expression('Text: @{text_var}'),
            font_size: expression('@{font_size_var}'),
            width: fixed(expression('@{width_var}')),
        });

        expect(block).toMatchSnapshot();
    });

    it('should create block with ranges', (): void => {
        const block = new DivText({
            text: 'Underlined text',
            ranges: [
                {
                    start: 0,
                    end: 10,
                    underline: 'single',
                },
            ],
        });

        expect(block).toMatchSnapshot();
    });

    it('should create block with ellipsis', (): void => {
        const block = new DivText({
            text: 'Some text',
            width: fixed(10),
            ellipsis: {
                text: '...',
                actions: [
                    {
                        log_id: 'id',
                        url: 'https://some.url',
                    },
                ],
            },
        });

        expect(block).toMatchSnapshot();
    });
});
