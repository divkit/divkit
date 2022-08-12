import {
    divCard,
    DivIndicator,
    DivRoundedRectangleShape,
    fixed,
    IDivData,
    reference,
    rewriteRefs,
    templateHelper,
} from '../../src';

describe('DivIndicator tests', (): void => {
    it('should create simple block', (): void => {
        const block = new DivIndicator({
            pager_id: 'pager1',
            active_item_color: '#fff',
            inactive_item_color: '#000',
            space_between_centers: fixed(1),
            shape: new DivRoundedRectangleShape({
                item_height: fixed(20, 'sp'),
                item_width: fixed(100),
                corner_radius: fixed(10, 'dp'),
            }),
            animation: 'worm',
        });

        expect(block).toMatchSnapshot();
    });

    it('should create card with templates', (): void => {
        const templates = {
            div: new DivIndicator({
                pager_id: reference('pager_id'),
                shape: new DivRoundedRectangleShape({
                    item_height: reference('shape_height'),
                }),
            }),
        };

        const divData: IDivData = {
            log_id: 'id',
            states: [
                {
                    state_id: 1,
                    div: templateHelper(templates).div({
                        pager_id: 'pager1',
                        shape_height: fixed(20),
                        animation: 'slider',
                    }),
                },
            ],
        };

        expect(divCard(rewriteRefs(templates), divData)).toMatchSnapshot();
    });
});
