import { divCard, DivText, IDivData, reference, rewriteRefs, templateHelper } from '../../src';

describe('DivAnimation tests', (): void => {
    it('should create card with templates', (): void => {
        const templates = {
            div: new DivText({
                text: 'Some text',
                action_animation: {
                    name: 'set',
                    items: [
                        {
                            name: 'fade',
                            start_value: reference('fade_start_value'),
                            end_value: 1,
                        },
                        {
                            name: 'scale',
                            start_delay: reference('scale_start_delay'),
                            start_value: reference('scale_start_value'),
                            end_value: 1,
                        },
                    ],
                },
            }),
        };

        const divData: IDivData = {
            log_id: 'id',
            states: [
                {
                    state_id: 1,
                    div: templateHelper(templates).div({
                        fade_start_value: 0.5,
                        scale_start_delay: 250,
                        scale_start_value: 0.0,
                    }),
                },
            ],
        };

        expect(divCard(rewriteRefs(templates), divData)).toMatchSnapshot();
    });
});
