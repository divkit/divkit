describe('regression', () => {
    describe('Nested change bounds transitions', () => {
        beforeEach(async function() {
            await this.browser.yaOpenRegressionJson('action_animation/nested_change_bounds');
        });

        it('Nested change bounds transitions', async function() {
            await this.browser.assertView('press_first_screen', '#root');
            await this.browser.$('[data-test-id = "container_02"]').then(elem => elem.click());
            //because we are waiting for animation
            await this.browser.pause(1000);
            await this.browser.assertView('press_here_screen', '#root');
            await this.browser.$('[data-test-id = "container_02"]').then(elem => elem.click());
            //because we are waiting for animation
            await this.browser.pause(1000);
            await this.browser.assertView('press_again_screen', '#root');
        });
    });
});
