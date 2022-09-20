describe('custom', () => {
    it('text with icon', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/textWithIcon.json');

        await this.browser.assertView('recursive', '#root');
    });

    it('overlap with match_parent', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/overlapWithMatchParent.json');

        await this.browser.assertView('recursive', '#root');
    });

    it('container and margins', async function() {
        await this.browser.yaOpenJson('../../../tests/hermione/json/custom/containerAndMargins.json');

        await this.browser.assertView('margins', '#root');
    });
});
