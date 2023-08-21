describe('regression', () => {
    describe('Gallery items posing', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('gallery/gallery-items-posing');
        });

        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Horizontal swipe', async function() {
            await this.browser.assertView('gallery_items_posing_1', '#root');
            await this.browser.yaSwipe({
                selector: ('[data-test-id = "block_1"]'),
                direction: 'left',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('gallery_items_posing_2', '#root');
            await this.browser.yaSwipe({
                selector: ('[data-test-id = "block_2"]'),
                direction: 'left',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('gallery_items_posing_3', '#root');
            await this.browser.yaSwipe({
                selector: ('[data-test-id = "block_3"]'),
                direction: 'left',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('gallery_items_posing_4', '#root');
        });
    });
});
