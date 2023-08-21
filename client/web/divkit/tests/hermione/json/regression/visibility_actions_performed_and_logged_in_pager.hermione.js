describe('regression', () => {
    describe('Visibility actions performed and logged in pager', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('visibility_actions/swipe_div_pager');
        });

        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Horizontal swipe_div_pager_right', async function() {
            // Wait for visibility action logging
            await this.browser.pause(1000);
            await this.browser.yaSwipe({
                selector: 'div[role=tabpanel]',
                direction: 'left',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('div_pager_right', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);
            logs.length.should.equal(4);
        });

            hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Horizontal swipe_div_pager_left', async function() {
            // Wait for visibility action logging
            await this.browser.pause(1000);
            await this.browser.yaSwipe({
                selector: 'div[role=tabpanel]',
                direction: 'right',
                duration: 400,
                offset: 200
            });
            await this.browser.assertView('div_pager_left', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);
            logs.length.should.equal(2);
        });
    });
});