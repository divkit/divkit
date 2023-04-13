describe('regression', () => {
    describe('Gallery default item states', () => {
        beforeEach(async function() {
            await this.browser.execute(() => {
                window.divkitLogs = [];
            });
            await this.browser.yaOpenRegressionJson('gallery/gallery_default_item_states');
        });

        it('Gallery item change state', async function() {
            // Wait for visibility action logging
            await this.browser.pause(1000);
            await this.browser.$('span=EXPAND').then(elem => elem.click());
    
            await this.browser.assertView('item_state_collapse', '#root');

            await this.browser.$('span=COLLAPSE').then(elem => elem.click());

            await this.browser.assertView('item_state_expand', '#root');

            const logs = await this.browser.execute(() => window.divkitLogs);
            logs.length.should.equal(4);
        });
    });
});