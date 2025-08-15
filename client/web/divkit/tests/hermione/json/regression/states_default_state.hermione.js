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
            let elem = await this.browser.$('[role=button]');
            elem.click();
            await this.browser.assertView('nondefault_state_on', '#root');
            elem = await this.browser.$('[role=button]');
            elem.click();
            await this.browser.assertView('default_state_on', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
        })
    });
});
