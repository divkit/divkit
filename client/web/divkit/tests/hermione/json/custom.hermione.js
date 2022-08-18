describe('custom', () => {
    it('wrap_content img inside gallery', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/wrapContentImage.json');

        await this.browser.assertView('recursive', '#root');
    });

    it('text with icon', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/textWithIcon.json');

        await this.browser.assertView('recursive', '#root');
    });

    it('overlap with match_parent', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/overlapWithMatchParent.json');

        await this.browser.assertView('recursive', '#root');
    });
});
