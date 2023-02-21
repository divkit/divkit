describe('regression', () => {
    describe('Root state switching', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('root_state_switching');
        });

        it('Tap on button', async function() {
            const elem = await this.browser.$('span=Change state (temporary)');
            elem.click();
            await this.browser.assertView('Square', '#root');
            elem.click();
            await this.browser.assertView('Circle', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
        });

        it('Click on button', async function() {
            const elem = await this.browser.$('span=Change state');
            elem.click();
            await this.browser.assertView('Square', '#root');
            elem.click();
            await this.browser.assertView('Circle', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
        });
    });
});
