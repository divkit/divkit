import { divCard, DivText, IDivData, reference, rewriteRefs, templateHelper } from '../../src';

describe('DivAction tests', (): void => {
    it('should create block with action', (): void => {
        const block = new DivText({
            text: 'Some text',
            action: {
                log_id: 'id',
                url: 'http://some.url',
            },
        });

        expect(block).toMatchSnapshot();
    });

    it('should create block with actions', (): void => {
        const block = new DivText({
            text: 'Some text',
            actions: [
                {
                    log_id: 'id_1',
                    url: 'http://some.url/?path_1',
                },
                {
                    log_id: 'id_2',
                    url: 'http://some.url/?path_2',
                },
            ],
        });

        expect(block).toMatchSnapshot();
    });

    it('should create block with action with payload', (): void => {
        const block = new DivText({
            text: 'Some text',
            action: {
                log_id: 'id',
                payload: {
                    custom_string_value: 'payload',
                    custom_integer_value: 20,
                },
            },
        });

        expect(block).toMatchSnapshot();
    });

    it('should create block with action with menu items', (): void => {
        const block = new DivText({
            text: 'Some text',
            action: {
                log_id: 'id',
                menu_items: [
                    {
                        text: 'Menu Item',
                        actions: [
                            {
                                log_id: 'id',
                                url: 'http://some.url',
                            },
                        ],
                    },
                ],
            },
        });

        expect(block).toMatchSnapshot();
    });

    it('should create card with templatesd action properties', (): void => {
        const templates = {
            card: new DivText({
                text: 'Some text',
                action: {
                    log_id: 'id',
                    url: reference('action_url'),
                },
            }),
        };

        const divData: IDivData = {
            log_id: 'id',
            states: [
                {
                    state_id: 1,
                    div: templateHelper(templates).card({
                        action_url: 'https://some.url',
                    }),
                },
            ],
        };

        expect(divCard(rewriteRefs(templates), divData)).toMatchSnapshot();
    });
});
