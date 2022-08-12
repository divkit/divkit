import { divCard, DivGifImage, fixed, IDivData, reference, rewriteRefs, templateHelper } from '../../src';

describe('DivGifImage tests', (): void => {
    it('should create simple block', (): void => {
        const block = new DivGifImage({
            gif_url: 'https://image.ru',
            width: fixed(100),
            height: fixed(200),
            scale: 'fit',
            border: {
                corner_radius: 1,
                stroke: {
                    width: 1,
                    color: '#cc0',
                },
            },
            preview: 'BASE64PREVIEWDATA',
        });

        expect(block).toMatchSnapshot();
    });

    it('should create card with templates', (): void => {
        const templates = {
            gif: new DivGifImage({
                gif_url: reference('url'),
                width: fixed(100),
                aspect: {
                    ratio: reference('aspect_ratio'),
                },
            }),
        };

        const divData: IDivData = {
            log_id: 'id',
            states: [
                {
                    state_id: 1,
                    div: templateHelper(templates).gif({
                        url: 'https://image.url',
                        aspect_ratio: 2,
                    }),
                },
            ],
        };

        expect(divCard(rewriteRefs(templates), divData)).toMatchSnapshot();
    });
});
