describe('regression', () => {
    describe('Send visiblity_actions for first page in Pager', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('visibility_actions/pager');
        });

        it('Check logs first page visiblity actions ', async function() {
            // Wait for visibility action logging
            await this.browser.pause(1000);
            let logs = await this.browser.execute(() => window.divkitLogs);
            logs = logs.map(log => log.action.log_id);
            const expectedSortedLogs = ['content_item_show:0', 'goose_shown'];
            const sortedLogs = logs.slice().sort();
            sortedLogs.should.deep.equal(expectedSortedLogs);
        });
    });
});
