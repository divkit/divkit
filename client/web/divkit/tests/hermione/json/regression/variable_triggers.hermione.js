describe('regression', () => {
    describe('Variable triggers', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('variables/div_variable_triggers');
        });

        it('Tap on button left', async function() {
            const elem = await this.browser.$('span=Left');
            elem.click();
            await this.browser.assertView('switch_left_to_on', '#root');
            elem.click();
            await this.browser.assertView('switch_left_to_off', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(4);
        })

        it('Tap on button right', async function() {
            const elem = await this.browser.$('span=Right');
            elem.click();
            await this.browser.assertView('switch_right_to_on', '#root');
            elem.click();
            await this.browser.assertView('switch_right_to_off', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(4);
        })

        it('Tap on both button', async function() {
            await this.browser.$('span=Left').then(elem => elem.click());
            await this.browser.$('span=Right').then(elem => elem.click());

            await this.browser.assertView('both', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(4);
        })
    });
});