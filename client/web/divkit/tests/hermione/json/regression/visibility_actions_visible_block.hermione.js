describe('regression', () => {
    describe('Visibility action in visible block', () => {
        beforeEach(async function() {
            await this.browser.yaOpenRegressionJson('visibility_actions/visibility_action');
        });

        it('Actions in in visible block', async function() {
            await this.browser.assertView('screen_visible_block_1', '#root');
            // Wait 2 seconds
            await this.browser.pause(2000);
            await this.browser.assertView('screen_visible_block_2', '#root');
        });
    });
});
