describe('regression', () => {
    describe('Simple tap', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('simple_tap');
        });

        it('Tap on text', async function() {
            let elem = await this.browser.$('[role=button]');
            elem.click();
            await this.browser.assertView('first_tap', '#root');
            elem = await this.browser.$('[role=button]');
            elem.click();
            await this.browser.assertView('second_tap', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
        })
    });
});
