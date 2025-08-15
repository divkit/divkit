describe('regression', () => {
    describe('Pager default item states', () => {
        beforeEach(async function() {
            await this.browser.yaOpenRegressionJson('pager/pager_default_item_states');
        });
        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Pager item change state', async function() {
            await this.browser.$('[role=tabpanel]:nth-child(3) button').then(elem => elem.click());

            await this.browser.assertView('pager_item_state_collapse_1', '#root');

            await this.browser.$('[role=tabpanel]:nth-child(3) button').then(elem => elem.click());

            await this.browser.assertView('pager_item_state_expand_1', '#root');
        });
    });
});
