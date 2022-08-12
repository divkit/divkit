import {
    DivAppearanceSetTransition,
    DivChangeBoundsTransition,
    DivFadeTransition,
    DivImage,
    DivScaleTransition,
    DivState,
    DivText,
    fixed,
} from '../../src';

describe('DivTransitions tests', (): void => {
    it('should create block with in/out transitions', (): void => {
        const block = new DivText({
            id: 'id',
            text: 'Text with transitions',
            transition_triggers: ['visibility_change'],
            transition_in: new DivAppearanceSetTransition({
                items: [
                    new DivFadeTransition({
                        alpha: 0,
                        duration: 250,
                        start_delay: 250,
                    }),
                    new DivScaleTransition({
                        scale: 2,
                        duration: 500,
                        interpolator: 'ease_in_out',
                    }),
                ],
            }),
            transition_out: new DivScaleTransition({
                scale: 0,
                duration: 500,
                interpolator: 'linear',
            }),
        });

        expect(block).toMatchSnapshot();
    });

    it('should create block with change transitions', (): void => {
        const block = new DivState({
            div_id: 'id',
            states: [
                {
                    state_id: 'state1',
                    div: new DivImage({
                        id: 'state1/image',
                        image_url: 'https://image.url',
                        width: fixed(100),
                        height: fixed(50),
                        transition_triggers: ['state_change'],
                        transition_change: new DivChangeBoundsTransition({
                            duration: 500,
                            interpolator: 'ease_in',
                        }),
                    }),
                },
                {
                    state_id: 'state2',
                    div: new DivImage({
                        id: 'state2/image',
                        image_url: 'https://image.url',
                        width: fixed(50),
                        height: fixed(100),
                        transition_triggers: ['state_change'],
                        transition_change: new DivChangeBoundsTransition({
                            duration: 500,
                            interpolator: 'ease_out',
                        }),
                    }),
                },
            ],
        });

        expect(block).toMatchSnapshot();
    });
});
