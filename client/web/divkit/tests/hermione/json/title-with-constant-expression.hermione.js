describe('custom', () => {
    it('title-with-constant-expression', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/title-with-constant-expression.json');

        await this.browser.assertView('title-with-constant-expression', '#root');
    });
});
