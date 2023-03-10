describe('regression', () => {
    describe('States default state', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('states_default_state');
        });

        it('Tap on text', async function() {
            await this.browser.assertView('default', '#root');
            const elem = await this.browser.$('span=Default state');
            elem.click();
            await this.browser.assertView('nondefault_state_on', '#root');
            elem.click();
            await this.browser.assertView('default_state_on', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
        })
    });
});