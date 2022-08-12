import {
    divCard,
    DivNeighbourPageSize,
    DivPager,
    DivPageSize,
    DivPercentageSize,
    DivText,
    IDivData,
    fixed,
    reference,
    rewriteRefs,
    templateHelper,
} from '../../src';

describe('DivPager tests', (): void => {
    it('should create simple block', (): void => {
        const block = new DivPager({
            orientation: 'horizontal',
            item_spacing: fixed(10),
            items: [
                new DivText({
                    text: 'Page 1',
                }),
                new DivText({
                    text: 'Page 2',
                }),
            ],
            layout_mode: new DivPageSize({
                page_width: new DivPercentageSize({
                    value: 90,
                }),
            }),
        });

        expect(block).toMatchSnapshot();
    });

    it('should create card with templates', (): void => {
        const templates = {
            div: new DivPager({
                items: [
                    new DivText({
                        text: reference('page1_text'),
                    }),
                    new DivText({
                        text: reference('page2_text'),
                    }),
                ],
                layout_mode: reference('mode'),
            }),
        };

        const divData: IDivData = {
            log_id: 'id',
            states: [
                {
                    state_id: 1,
                    div: templateHelper(templates).div({
                        page1_text: 'Page 1',
                        page2_text: 'Page 2',
                        mode: new DivNeighbourPageSize({
                            neighbour_page_width: fixed(10),
                        }),
                    }),
                },
            ],
        };

        expect(divCard(rewriteRefs(templates), divData)).toMatchSnapshot();
    });
});
