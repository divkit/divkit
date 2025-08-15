describe('regression', () => {
    describe('Tab swipe restricted', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('tabs_disabled_switch_tabs_by_swipe');
        });

        it('Horizontal swipe', async function() {
            await this.browser.$('button=Tab 2').then(elem => elem.click());

            await this.browser.assertView('swipe_right_tab_2', '#root');

            await this.browser.$('button=Tab 0').then(elem => elem.click());

            await this.browser.assertView('swipe_left_tab_0', '#root');
        });
    });
});
