describe('regression', () => {
    describe('Pager item actions', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('pager/pager-item-actions');
        });
        
        it('Pager item actions scroll', async function() {
            // Wait for visibility action logging
            await this.browser.pause(1000);
            //First click
            await this.browser.$('[data-test-id = "scroll_click_4"]').then(elem => elem.click());
            //Because don't have time scrolling
            await this.browser.pause(1000);

            await this.browser.assertView('scroll_block_6', '#root');
            //Second click
            await this.browser.$('[data-test-id = "scroll_click_1"]').then(elem => elem.click());
            //Because don't have time scrolling
            await this.browser.pause(1000);

            await this.browser.assertView('scroll_block_1', '#root');
            //Third click
            await this.browser.$('[data-test-id = "scroll_click_3"]').then(elem => elem.click());
            //Because don't have time scrolling
            await this.browser.pause(1000);

            await this.browser.assertView('scroll_block_2', '#root');
            //Fourth click
            await this.browser.$('[data-test-id = "scroll_click_2"]').then(elem => elem.click());
            //Because don't have time scrolling
            await this.browser.pause(1000);

            await this.browser.assertView('scroll_block_1_dbl', '#root');

        });
    });
});
