describe('set_stored_value', () => {
    const cases = [
        { title: 'integer zero', type: 'integer', value: 0 },
        { title: 'number zero', type: 'number', value: 0 },
        { title: 'empty string', type: 'string', value: '' },
        { title: 'boolean false', type: 'boolean', value: false },
        { title: 'boolean true', type: 'boolean', value: true },
        { title: 'zero lifetime', type: 'integer', value: 1, lifetime: 0 },
        { title: 'URL empty string', type: 'string', value: '', url: true },
        { title: 'URL boolean zero', type: 'boolean', value: '0', expected: false, url: true },
        { title: 'URL boolean one', type: 'boolean', value: '1', expected: true, url: true },
        { title: 'URL boolean false', type: 'boolean', value: 'false', expected: false, url: true },
        { title: 'URL boolean true', type: 'boolean', value: 'true', expected: true, url: true },
        { title: 'missing value', type: 'integer', error: true },
        { title: 'null value', type: 'integer', value: null, error: true },
        { title: 'missing lifetime', type: 'integer', value: 1, omitLifetime: true, error: true }
    ];

    for (const storeMethod of ['set', 'setValue']) {
        for (const item of cases) {
            it(`${item.error ? 'rejects' : 'stores'} ${item.title} with ${storeMethod} from a visibility action`, async function() {
                await this.browser.yaOpenJson('../../../tests/hermione/json/custom/title-with-constant-expression.json');
                await this.browser.execute((item, storeMethod) => {
                    window.divkitRoot.$destroy();
                    const target = document.querySelector('#root');
                    target.innerHTML = '';
                    window.storedValueCalls = [];
                    window.storedValueErrors = [];
                    const lifetime = item.lifetime === undefined ? 3600 : item.lifetime;
                    const action = { log_id: 'reset_to_zero' };
                    if (item.url) {
                        action.url = 'div-action://set_stored_value?' + new URLSearchParams({
                            name: 'tooltip_shown', type: item.type, value: item.value, lifetime
                        });
                    } else {
                        action.typed = {
                            type: 'set_stored_value', name: 'tooltip_shown', lifetime,
                            value: { type: item.type, value: item.value }
                        };
                    }
                    if (item.omitLifetime) {
                        delete action.typed.lifetime;
                    }
                    window.divkitRoot = window.Ya.DivKit.render({
                        id: 'stored-value', target,
                        json: { card: {
                            log_id: 'stored_zero',
                            states: [{ state_id: 0, div: {
                                type: 'container',
                                visibility_actions: [action],
                                items: [{ type: 'text', text: 'stored zero' }]
                            } }]
                        } },
                        store: {
                            get: () => undefined,
                            [storeMethod](...args) {
                                window.storedValueCalls.push(args.map(value =>
                                    typeof value === 'bigint' ? Number(value) : value));
                            }
                        },
                        onError(event) {
                            window.storedValueErrors.push(event.error.message);
                        }
                    });
                }, item, storeMethod);
                await this.browser.waitUntil(() => this.browser.execute(() =>
                    window.storedValueCalls.length > 0 || window.storedValueErrors.length > 0));
                const result = await this.browser.execute(() => ({
                    calls: window.storedValueCalls,
                    errors: window.storedValueErrors
                }));
                if (item.error) {
                    result.errors.should.deep.equal(['Missing required params for set_stored_value']);
                    result.calls.should.deep.equal([]);
                    return;
                }
                result.errors.should.deep.equal([]);
                result.calls.should.deep.equal([[
                    'tooltip_shown', item.type,
                    item.expected === undefined ? item.value : item.expected,
                    item.lifetime === undefined ? 3600 : item.lifetime,
                    ...(storeMethod === 'set' ? [null] : [])
                ]]);
            });
        }
    }
});
