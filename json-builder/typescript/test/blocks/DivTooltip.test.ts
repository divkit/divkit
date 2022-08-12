import { DivImage, DivText, fixed } from '../../src';

describe('DivTooltip tests', (): void => {
    it('should create block with tooltip', (): void => {
        const block = new DivImage({
            image_url: 'https://image.url',
            width: fixed(44),
            height: fixed(44),
            tooltips: [
                {
                    id: 'example_tooltip',
                    position: 'bottom-left',
                    div: new DivText({
                        text: 'Tooltip text',
                    }),
                },
            ],
        });

        expect(block).toMatchSnapshot();
    });
});
