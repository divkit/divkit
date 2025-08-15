describe('custom', () => {
    it('corners', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/corners.json');

        await this.browser.assertView('corners', '#root');
    });
});
