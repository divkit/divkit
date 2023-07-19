describe('regression', () => {
    describe('Visibility actions for default item', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('visibility_actions/default_item');
        });

        it('Check logs visibility action fot default item', async function() {
            // Wait for visibility action logging and container log
            await this.browser.pause(4000);
            await this.browser.assertView('default_item', '#root');
            let logs = await this.browser.execute(() => window.divkitLogs);
            logs = logs.map(log => log.action.log_id);
            logs[0].should.equal('6');
            logs[1].should.equal('content_item_show:0');
            logs[2].should.equal('container');
        });
    });
});
