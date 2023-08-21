describe('regression', () => {
    describe('Pager default item states', () => {
        beforeEach(async function() {
            await this.browser.yaOpenRegressionJson('pager/pager_default_item_states');
        });
        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Pager item change state', async function() {
            // Wait for visibility action logging
            //First item
            await this.browser.pause(1000);
            await this.browser.$('span=EXPAND').then(elem => elem.click());
    
            await this.browser.assertView('pager_item_state_collapse_1', '#root');

            await this.browser.$('span=COLLAPSE').then(elem => elem.click());

            await this.browser.assertView('pager_item_state_expand_1', '#root');
            //Scroll
            await this.browser.yaSwipe({
                selector: ('[data-test-id = "blocks_2"]'),
                direction: 'left',
                duration: 500,
                offset: 300
            });

            await this.browser.assertView('pager_scroll_states_righting', '#root');
            //Second item
            await this.browser.$('[data-test-id="expand_1"]').then(elem => elem.click());
    
            await this.browser.assertView('pager_item_state_collapse_2', '#root');

            await this.browser.$('span=COLLAPSE').then(elem => elem.click());

            await this.browser.assertView('pager_item_state_expand_2', '#root');

        });
    });
});
