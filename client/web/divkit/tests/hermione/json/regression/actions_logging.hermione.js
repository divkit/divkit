describe('regression', () => {
    describe('Actions logging', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('logging');
        });

        it('Click on Test button', async function() {
            await this.browser.$('span=Test button').then(elem => elem.click());
            // wait for click, because of doubletap_actions delay
            await this.browser.pause(500);

            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
            logs.some(it => it.action.log_id === 'first_tap').should.equal(true);
            logs.some(it => it.action.log_id === 'second_tap').should.equal(true);
        });

        it('Double click Test button', async function() {
            await this.browser.$('span=Test button').then(elem => elem.doubleClick());
            const logs = await this.browser.execute(() => window.divkitLogs);

            logs.length.should.equal(2);
            logs.some(it => it.action.log_id === 'first_doubletap').should.equal(true);
            logs.some(it => it.action.log_id === 'second_doubletap').should.equal(true);
        });

        // todo: Добавить тест на лонгтап, на данный момент лонгтап не логируется и тест не может пройти, проблема, вероятно, в самом тесте. Пока было принято решение не делать в данном тесте эту проверку.
    });
});
