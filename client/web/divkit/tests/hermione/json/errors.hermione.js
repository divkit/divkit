describe('errors', () => {
    it('should handle recursive templates', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/errors/recursive.json');

        await this.browser.assertView('recursive', '#root');
    });

    it('should handle recursive templates 2', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/errors/recursive2.json');

        await this.browser.assertView('recursive2', '#root');
    });

    it('should handle broken tab title', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/errors/broken-tab-title.json');

        await this.browser.assertView('broken-tab-title', '#root');
    });

    it('should handle incorrect background', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/errors/incorrect-background-color.json');

        await this.browser.assertView('incorrect-background-color', '#root');
    });
});
