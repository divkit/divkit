describe('encode', () => {
    it('encode', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/encode.json');

        await this.browser.assertView('encode', '#root');
    });
});
