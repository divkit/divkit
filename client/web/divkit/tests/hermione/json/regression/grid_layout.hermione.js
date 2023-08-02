describe('regression', () => {
    describe('Grid layout', () => {
        beforeEach(async function() {
            await this.browser.yaOpenRegressionJson('grid_layout');
        });

        it('Grid layout', async function() {
            await this.browser.assertView('screen_layout_1', '#root');
            await this.browser.$('[data-test-id = "button"]').then(elem => elem.click());
            await this.browser.assertView('screen_layout_2', '#root');
            await this.browser.$('[data-test-id = "button_1"]').then(elem => elem.click());
            await this.browser.assertView('screen_layout_3', '#root');
        });
    });
});
