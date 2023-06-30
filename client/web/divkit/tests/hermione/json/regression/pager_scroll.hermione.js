describe('regression', () => {
    describe('Paged scroll', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('pager/pager');
        });

        hermione.only.in('chromeMobile', 'pointerType="touch" is not supported on firefox');
        it('Horizontal scroll page', async function() {
            // Wait for visibility action logging
            await this.browser.pause(1000);
            await this.browser.yaSwipe({
                selector: ('[data-test-id = "block_1"]'),
                direction: 'left',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('scroll_lefting', '#root');

            await this.browser.yaSwipe({
                selector: ('[data-test-id = "block_2"]'),
                direction: 'right',
                duration: 500,
                offset: 300
            });
            await this.browser.assertView('scroll_righting', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);
            logs.length.should.equal(3);
        });
    });
});
