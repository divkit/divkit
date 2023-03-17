describe('regression', () => {
    describe('Paged gallery scroll', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('gallery/gallery_swipe_paged');
        });

        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Horizontal swipe', async function() {
            // Wait for visibility action logging
            await this.browser.pause(1000);
            await this.browser.yaSwipe({
                selector: 'div[role=tabpanel]',
                direction: 'left',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('swipe_left', '#root');

            await this.browser.yaSwipe({
                selector: 'div[role=tabpanel]',
                direction: 'right',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('swipe_right', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);
            logs.length.should.equal(3);
        });
    });
});
