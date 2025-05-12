describe('regression', () => {
    describe('Visibility actions performed and logged in pager', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('visibility_actions/swipe_div_pager');
        });

        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Horizontal swipe grouped bulkAction', async function() {
            // Wait for visibility action logging
            await this.browser.pause(4000);
            await this.browser.yaSwipe({
                selector: 'div[role=tabpanel]',
                direction: 'left',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('swipe_left_bulkAction', '#root');

            let logs = await this.browser.execute(() => window.divkitLogs);
            logs = logs.map(log => log.action.log_id);
            logs.should.contain('6','content_item_show:0','106','content_item_show:1');
        });
    });
});
