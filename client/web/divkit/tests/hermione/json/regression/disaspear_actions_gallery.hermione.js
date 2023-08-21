describe('regression', () => {
    describe('Disappear actions in gallery', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('gallery/gallery_swipe_disappear_action');
        });

        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Horizontal swipe', async function() {
            // Wait for visibility action logging
            await this.browser.assertView('screen_disaspear_actions', '#root');
            await this.browser.yaSwipe({
                selector: ('[data-test-id = "blocks_2"]'),
                direction: 'left',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('left_swipe_disaspear', '#root');
            await this.browser.pause(1000)
            // Wait for visibility action logging
            let logs = await this.browser.execute(() => window.divkitLogs);
            logs = logs.map(log => log.action.log_id);
            logs.should.contain('item/01');
        });
    });
});
