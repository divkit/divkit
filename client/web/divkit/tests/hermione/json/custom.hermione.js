describe('custom', () => {
    it('wrap_content img inside gallery', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/wrapContentImage.json');

        await this.browser.assertView('recursive', '#root');
    });
});
