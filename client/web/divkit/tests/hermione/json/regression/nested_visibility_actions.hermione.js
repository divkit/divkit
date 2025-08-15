describe('regression', () => {
    describe('Nested visibility actions', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('visibility_actions/article');
        });

        it('Nested visibility actions logs', async function() {
            // Wait for visibility action logging
            await this.browser.pause(1000);
            await this.browser.$('span=EXPAND').then(elem => elem.click());
    
            await this.browser.assertView('collapse_nested_visibility_actions', '#root');
            await this.browser.$('span=SHOW COMMENTS').then(elem => elem.click());
    
            await this.browser.assertView('comments_nested_visibility_actions', '#root');
            let logs = await this.browser.execute(() => window.divkitLogs);
            logs = logs.map(log => log.action.log_id);
            logs.should.contain('comment_01','comment_02','comment_03','comment_04');
        });
    });
});
