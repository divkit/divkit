describe('scope_id', () => {
    it('should change scoped local variable', async function() {
        await this.browser.yaOpenJson('../../../../../../test_data/ui_test_data/actions/scoped_actions.json');

        const title = await this.browser.$('[data-test-id="title"]');

        (await title.getText()).should.equal('Lorem ipsum');
        await this.browser.$('span=Scoped').then(elem => elem.click());
        (await title.getText()).should.equal('Title has been changed');
    });

    it('should change local variable from nested scope', async function() {
        await this.browser.yaOpenJson('../../../../../../test_data/ui_test_data/actions/scoped_actions.json');

        const title = await this.browser.$('[data-test-id="title"]');

        (await title.getText()).should.equal('Lorem ipsum');
        await this.browser.$('span=Not scoped').then(elem => elem.click());
        (await title.getText()).should.equal('Lorem ipsum');
    });

    it('should not change non-scoped local variable', async function() {
        await this.browser.yaOpenJson('../../../../../../test_data/ui_test_data/actions/scoped_actions_nested_scope.json');

        const title = await this.browser.$('[data-test-id="title"]');

        (await title.getText()).should.equal('Lorem ipsum');
        await this.browser.$('span=Content scope').then(elem => elem.click());
        (await title.getText()).should.equal('Title has been changed from \'content\' scope');
    });

    it('should not change on ambiguous action', async function() {
        await this.browser.yaOpenJson('../../../../../../test_data/ui_test_data/actions/scoped_actions_ambiguous_scope.json');

        const title = await this.browser.$('span=Lorem ipsum');

        (await title.getText()).should.equal('Lorem ipsum');
        await this.browser.$('span=Ambiguous scope').then(elem => elem.click());
        (await title.getText()).should.equal('Lorem ipsum');
    });
});
