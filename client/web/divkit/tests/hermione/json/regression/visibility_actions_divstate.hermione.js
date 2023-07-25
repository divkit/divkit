describe('regression', () => {
    describe('Visibility actions in DivState', () => {
        beforeEach(async function() {
            await this.browser.yaOpenRegressionJson('visibility_actions/states');
        });

        it('Select actions in DivState', async function() {
            await this.browser.assertView('screen_divstates_1', '#root');
            await this.browser.$('span=State 2').then(elem => elem.click());
            await this.browser.assertView('screen_divstates_2', '#root');
            // Wait 2 seconds
            await this.browser.pause(2000);
            await this.browser.assertView('screen_divstates_3', '#root');
        });
    });
});
