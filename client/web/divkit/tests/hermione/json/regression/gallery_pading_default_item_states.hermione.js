describe('regression', () => {
    describe('Gallery paging default item states', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('gallery/gallery_paging_default_item_states');
        });
        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Gallery item change state', async function() {
            //First item
            await this.browser.$('span=EXPAND').then(elem => elem.click());

            await this.browser.assertView('item_pading_state_collapse_1', '#root');

            await this.browser.$('span=COLLAPSE').then(elem => elem.click());

            await this.browser.assertView('item_pading_state_expand_1', '#root');
            //First Scroll
            await this.browser.yaSwipe({
                selector: ('[data-test-id = "blocks_2"]'),
                direction: 'left',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('pading_scroll_states_right', '#root');
            //Second item
            await this.browser.$('[data-test-id="expand_2"]').then(elem => elem.click());

            await this.browser.assertView('pager_item_state_collapse_2', '#root');

            await this.browser.$('span=COLLAPSE').then(elem => elem.click());
            
            await this.browser.assertView('pager_item_state_expand_2', '#root');
            //Second Scroll
            await this.browser.yaSwipe({
                selector: ('[data-test-id = "blocks_2"]'),
                direction: 'right',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('pading_scroll_states_left', '#root');
        });
    });
});
