describe('regression', () => {
    describe('Actions on tap', () => {
        beforeEach(async function() {
            await this.browser.yaOpenRegressionJson('button_actions');
        });

        it('Tap on Button 1', async function() {
            await this.browser.$('span=Button 1 (tap)').then(elem => elem.click());

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'button1_tap').should.equal(true);
        });

        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Long tap on Button 2', async function() {
            await this.browser.yaLongTap('span=Button 2 (long tap)', 500);

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'button2_longtap').should.equal(true);
        });

        it('Double tap on Button 3', async function() {
            await this.browser.$('span=Button 3 (double tap)').then(elem => elem.doubleClick());

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'button3_doubletap').should.equal(true);
        });

        it('Tap on Button 4', async function() {
            await this.browser.$('span=Button 4 (tap + long tap + double tap)').then(elem => elem.click());

            // wait click delay
            await this.browser.pause(1000);

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'button4_tap').should.equal(true);
        });

        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Long tap on Button 4', async function() {
            await this.browser.yaLongTap('span=Button 4 (tap + long tap + double tap)', 500);

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'button4_longtap').should.equal(true);
        });

        it('Double tap on Button 4', async function() {
            await this.browser.$('span=Button 4 (tap + long tap + double tap)').then(elem => elem.doubleClick());

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'button4_doubletap').should.equal(true);
        });

        it ('Tap on Button 5', async function() {
            await this.browser.$('span=Button 5 (tap + long tap)').then(elem => elem.click());

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'button5_tap').should.equal(true);
        });

        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Long tap on Button 5', async function() {
            await this.browser.yaLongTap('span=Button 5 (tap + long tap)', 500);

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'button5_longtap').should.equal(true);
        });

        it('Double tap on Button 5', async function() {
            await this.browser.$('span=Button 5 (tap + long tap)').then(elem => elem.doubleClick());

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
            logs.filter(it => it.action.log_id === 'button5_tap').length.should.equal(2);
        });

        it ('Tap on Button 6', async function() {
            await this.browser.$('span=Button 6 (tap + double tap)').then(elem => elem.click());

            // wait click delay
            await this.browser.pause(1000);

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'button6_tap').should.equal(true);
        });

        it('Double tap on Button 6', async function() {
            await this.browser.$('span=Button 6 (tap + double tap)').then(elem => elem.doubleClick());

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(1);
            logs.some(it => it.action.log_id === 'button6_doubletap').should.equal(true);
        });
    });
});
