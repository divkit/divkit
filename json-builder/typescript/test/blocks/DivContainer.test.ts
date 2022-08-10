import {
    divCard,
    DivContainer,
    DivImage,
    DivSeparator,
    fixed,
    IDivData,
    reference,
    rewriteRefs,
    templateHelper,
} from '../../src';

describe('DivContainer tests', (): void => {
    it('should create card with templates', (): void => {
        const templates = {
            product: new DivContainer({
                items: [
                    new DivImage({
                        width: reference('top_image_width'),
                        height: fixed(40),
                        image_url: reference('top_image_url'),
                    }),
                    new DivSeparator({
                        height: reference('gap_height'),
                        delimiter_style: {
                            orientation: 'horizontal',
                            color: reference('gap_color'),
                        },
                    }),
                    new DivImage({
                        width: reference('bottom_image_width'),
                        height: fixed(40),
                        image_url: reference('bottom_image_url'),
                    }),
                ],
            }),
        };

        const divData: IDivData = {
            log_id: 'id',
            states: [
                {
                    state_id: 1,
                    div: templateHelper(templates).product({
                        top_image_url: 'https://image.url/top',
                        top_image_width: fixed(40),
                        bottom_image_url: 'https://image.url/bottom',
                        bottom_image_width: fixed(40),
                        gap_height: fixed(10),
                        gap_color: '#000',
                        paddings: {
                            left: 10,
                            right: 10,
                        },
                    }),
                },
            ],
        };

        expect(divCard(rewriteRefs(templates), divData)).toMatchSnapshot();
    });
});
