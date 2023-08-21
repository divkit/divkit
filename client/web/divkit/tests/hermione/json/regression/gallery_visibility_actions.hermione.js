describe('regression', () => {
    describe('Visibility_actions in gallery', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('gallery/gallery_swipe');
        });

        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Horizontal swipe visibility action', async function() {
            // Wait for visibility action logging
            await this.browser.pause(1000);
            await this.browser.yaSwipe({
                selector: 'div[role=tabpanel]',
                direction: 'left',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('swipe_left__action', '#root');

            await this.browser.yaSwipe({
                selector: 'div[role=tabpanel]',
                direction: 'right',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('swipe_right__action', '#root');

            let logs = await this.browser.execute(() => window.divkitLogs);
            logs = logs.map(log => log.action.log_id);
            logs[0].should.equal('item/01');
            logs[1].should.equal('item/02');
            logs[2].should.equal('item/01');
        });
    });
});
