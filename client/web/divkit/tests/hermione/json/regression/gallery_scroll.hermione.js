describe('regression', () => {
    describe('Gallery scroll', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('gallery/gallery_swipe');
        });

        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Horizontal swipe', async function() {
            // Wait for visibility action logging
            await this.browser.pause(1000);
            await this.browser.yaSwipe({
                selector: 'div[role=tabpanel]',
                direction: 'right',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('left_swipe', '#root');

            await this.browser.yaSwipe({
                selector: 'div[role=tabpanel]',
                direction: 'left',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('right_swipe', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);
            logs.length.should.equal(2);
        });
    });
});