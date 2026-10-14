async function setTruncatePolicy(browser, policy) {
    await browser.execute(value => window.divkitRoot.execAction({
        log_id: 'set-policy',
        typed: {
            type: 'set_variable',
            variable_name: 'policy',
            value: { type: 'string', value }
        }
    }), policy);
}

describe('Single-line truncation whitespace', () => {
    beforeEach(async function() {
        await this.browser.yaOpenJson('truncate-policy.json');
    });

    it('Collapses spaces and newlines when word truncation is enabled', async function() {
        const text = await this.browser.execute(() => document.querySelector('[data-test-id="price"]').innerText);
        text.should.equal('Price: 100 old');
    });

    it('Keeps whitespace collapsed after switching back to word truncation', async function() {
        // Arrange
        await setTruncatePolicy(this.browser, 'grapheme');
        await this.browser.waitUntil(() => this.browser.execute(() =>
            document.querySelector('[data-test-id="price"]').innerText === 'Price: 100 old'));
        // Act
        await setTruncatePolicy(this.browser, 'word');
        // Assert
        const text = await this.browser.execute(() => document.querySelector('[data-test-id="price"]').innerText);
        text.should.equal('Price: 100 old');
    });
});
