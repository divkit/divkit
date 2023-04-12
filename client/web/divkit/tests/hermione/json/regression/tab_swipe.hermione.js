describe('regression', () => {
    describe('Tab swipe', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('tabs_max_height');
        });

        it('Horizontal swipe', async function() {
            await this.browser.$('span=Tab 1').then(elem => elem.click());
    
            await this.browser.assertView('swipe_right_tab', '#root');

            await this.browser.$('span=Tab 0').then(elem => elem.click());

            await this.browser.assertView('swipe_left_tab', '#root');
        });
    });
});
