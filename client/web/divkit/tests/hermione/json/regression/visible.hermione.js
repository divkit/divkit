describe('regression', () => {
    describe('Visible_in_div-base', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('set_visibility');
        });

        it('Click on visible', async function() {
            await this.browser.$('span=visible').then(elem => elem.click());
            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
            logs.some(it => it.action.log_id === 'update_visibility_visible').should.equal(true);

            await this.browser.assertView('visible', '#root');
        });

        it('Click on invisible', async function() {
            await this.browser.$('span=invisible').then(elem => elem.click());
            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
            logs.some(it => it.action.log_id === 'update_visibility_invisible').should.equal(true);

            await this.browser.assertView('invisible', '#root');
        });

        it('Click on gone', async function() {
            await this.browser.$('span=gone').then(elem => elem.click());
            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
            logs.some(it => it.action.log_id === 'update_visibility_gone').should.equal(true);

            await this.browser.assertView('gone', '#root');
        });
        })
    });
