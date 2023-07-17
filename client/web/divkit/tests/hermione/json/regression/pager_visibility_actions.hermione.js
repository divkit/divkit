describe('regression', () => {
    describe('Visibility_actions in pager', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('visibility_actions/pager');
        });
        //
        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Horizontal scroll visibility action', async function() {
            // Wait for visibility action logging
            await this.browser.pause(1000);
            //Visible goose_visible
            await this.browser.$('[data-test-id = "goose_visible"]', { visible: true });
            await this.browser.yaSwipe({
                selector: ('[data-test-id = "block_1"]'),
                direction: 'left',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('scroll_left_action', '#root');

            await this.browser.yaSwipe({
                selector: ('[data-test-id = "block_2"]'),
                direction: 'right',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('scroll_right_action', '#root');
            
            let logs = await this.browser.execute(() => window.divkitLogs);
            logs = logs.map(log => log.action.log_id);
            const expectedSortedLogs = ['content_item_show:0', 'content_item_show:1', 'goose_shown'];
            const sortedLogs = logs.slice().sort();
            sortedLogs.should.deep.equal(expectedSortedLogs);
        });
    });
});
