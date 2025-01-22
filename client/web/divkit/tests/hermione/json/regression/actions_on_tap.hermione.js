describe('regression', () => {
    describe('Actions on tap', () => {
        beforeEach(async function() {
            await this.browser.yaOpenRegressionJson('button_actions');
        });

        it('Tap on top button', async function() {
            await this.browser.$('span=With double and long taps').then(elem => elem.click());

            // wait menu animation
            await this.browser.pause(500);

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'single_tap').should.equal(true);

            await this.browser.assertView('menu', '#root');
        });

        it('Double tap on top button', async function() {
            await this.browser.$('span=With double and long taps').then(elem => elem.doubleClick());

            // wait menu animation and click delay
            await this.browser.pause(1000);

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'doubletap_actions').should.equal(true);

            await this.browser.assertView('menu', '#root');
        });

        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Long click on top button', async function() {
            await this.browser.yaLongTap('span=With double and long taps', 500);

            // wait menu animation and click delay
            await this.browser.pause(1000);

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'longtap_actions').should.equal(true);

            await this.browser.assertView('menu', '#root');
        });

        it ('Tap on button 2', async function() {
            await this.browser.$('span=Without double tap').then(elem => elem.click());
            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'single_tap').should.equal(true);

            // wait menu animation
            await this.browser.pause(500);

            await this.browser.assertView('menu', '#root');
        });

        it('Double tap on button 2', async function() {
            await this.browser.$('span=Without double tap').then(elem => elem.doubleClick());
            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
            logs.filter(it => it.action.log_id === 'single_tap').length.should.equal(2);

            // wait menu animation
            await this.browser.pause(500);

            await this.browser.assertView('menu', '#root');
        });

        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Long click on button 2', async function() {
            await this.browser.yaLongTap('span=Without double tap', 500);

            // wait menu animation and long tap selection
            await this.browser.pause(1000);

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'longtap_actions').should.equal(true);

            await this.browser.assertView('menu', '#root');
        });
    });
});
