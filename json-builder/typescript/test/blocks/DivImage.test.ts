import { divCard, DivImage, fixed, IDivData, reference, rewriteRefs, templateHelper } from '../../src';

describe('DivImage tests', (): void => {
    it('should create simple block', (): void => {
        const block = new DivImage({
            image_url: 'https://image.url',
            width: fixed(44),
            height: fixed(44),
            aspect: {
                ratio: 0.75,
            },
            preload_required: 1,
        });

        expect(block).toMatchSnapshot();
    });

    it('should create card with templates', (): void => {
        const templates = {
            div: new DivImage({
                width: fixed(44),
                image_url: 'https://image.url',
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
                    div: templateHelper(templates).div({
                        aspect_ratio: 1,
                    }),
                },
            ],
        };

        expect(divCard(rewriteRefs(templates), divData)).toMatchSnapshot();
    });
});
