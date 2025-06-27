const path = require('path');
const fs = require('fs');

function read(dir, createTestCase, skipTests = [], skipFirefox = []) {
    function append(dir, prefix) {
        const fulldir = path.join(__dirname, dir);
        const items = fs.readdirSync(fulldir);

        for (const item of items) {
            if (skipTests.some(test => item.includes(test))) {
                continue;
            }

            if (item.endsWith('.json')) {
                if (item === 'templates.json') {
                    continue;
                }

                const testCase = item.replace('.json', '');
                const testPath = '/' + path.relative(path.resolve(__dirname, '../../..'), path.resolve(__dirname, prefix + item));

                const platforms = require(path.join(fulldir, item)).platforms;
                if (platforms && !platforms.includes('web')) {
                    console.log('skip', path.join(fulldir, item));
                    continue;
                }

                let only;
                if (skipFirefox.some(test => item.includes(test))) {
                    only = 'chromeMobile';
                }
                createTestCase(testCase, testPath, only);
            } else {
                describe(item, function() {
                    append(path.join(dir, item), prefix + item + '/');
                });
            }
        }
    }

    append(dir, dir);
}

function createSimpleTestCase(testCase, testPath, only) {
    if (only) {
        hermione.only.in(only, 'Unstable test');
    }
    it(`${testCase}`, async function() {
        await this.browser.yaOpenCrossplatformJson(testPath);
        await this.browser.assertView(testCase, '#root');
    });
}

function createInteractiveTestCase(testCase, testPath) {
    const { steps } = JSON.parse(fs.readFileSync(path.join(path.resolve(__dirname, '../../..'), testPath), 'utf8'));
    it(`${testCase}`, async function() {
        await this.browser.yaOpenCrossplatformJson(testPath);

        for (let i = 0; i < steps.length; i++) {
            const { delay, div_actions } = steps[i];

            if (div_actions) {
                for (const action of div_actions) {
                    await this.browser.execute(action => {
                        window.divkitRoot.execAction(action);
                    }, action);
                }
            }

            if (delay) {
                await this.browser.pause(delay);
            }

            await this.browser.pause(300);
            await this.browser.assertView(`step${i}`, '#root');
        }
    });
}

function createIntegrationTestCase(testCase, testPath) {
    const { description, div_data, cases } = JSON.parse(fs.readFileSync(path.join(path.resolve(__dirname, '../../..'), testPath), 'utf8'));

    for (let i = 0; i < cases.length; ++i) {
        const item = cases[i];
        const resultType = item.expected.find(it => it.type === 'variable' && it.variable_name === 'result')?.value?.type;

        if (item.platforms && !item.platforms.includes('web')) {
            continue;
        }

        describe(description, () => {
            it(`Case [${i}]`, async function() {
                await this.browser.yaOpenCrossplatformJson(testPath, {
                    result_type: resultType
                });

                if (item.div_actions) {
                    for (let j = 0; j < item.div_actions.length; j++) {
                        const action = item.div_actions[j];

                        await this.browser.execute(action => {
                            window.divkitRoot.execAction(action);
                        }, action);
                    }
                }

                const expectErrors = item.expected.filter(it => it.type === 'error');
                const errors = await this.browser.execute(() => {
                    if (!window.errors) {
                        return [];
                    }

                    return window.errors.map(it => {
                        return {
                            message: it.message,
                            additionalMessage: it.additional ? it.additional.message : undefined,
                            expression: it.additional ? it.additional.expression : undefined
                        };
                    });
                });
                errors.length.should.equal(expectErrors.length);

                for (let j = 0; j < expectErrors.length; ++j) {
                    let expected = expectErrors[j];
                    let str = errors[j].additionalMessage || errors[j].message;

                    if (errors[j].expression) {
                        str += ' Expression: ' + errors[j].expression.replace(/\\/g, '\\\\');
                    }

                    str.should.equal(expected.value);
                }

                for (let j = 0; j < item.expected.length; j++) {
                    const expected = item.expected[j];

                    if (expected.type === 'variable') {
                        const result = await this.browser.execute(variableName => {
                            const inst = window.divkitRoot.getDebugAllVariables().get(variableName);
                            const type = inst.getType();
                            let value = inst.getValue();

                            if (typeof value === 'bigint') {
                                value = Number(value);
                            } else if (type === 'boolean') {
                                value = Boolean(value);
                            }

                            return {
                                type,
                                value
                            };
                        }, expected.variable_name);

                        result.type.should.equal(expected.value.type);
                        result.value.should.deep.equal(expected.value.value);
                    } else if (expected.type === 'error') {
                        continue;
                    } else {
                        throw new Error('Unsupported expected type ' + expected.type);
                    }
                }
            });
        });
    }
}

const crossplatformPath = '../../../../../../test_data';
describe('crossplatform', () => {
    describe('samples', () => {
        const skipTests = [];
        read(`${crossplatformPath}/samples/`, createSimpleTestCase, skipTests);
    });

    describe('components', () => {
        const skipTests = [];
        const skipFirefox = [
            'radial-positions'
        ];
        read(`${crossplatformPath}/snapshot_test_data/`, createSimpleTestCase, skipTests, skipFirefox);
    });

    describe('interactions', () => {
        const skipTests = [];
        read(`${crossplatformPath}/interactive_snapshot_test_data/`, createInteractiveTestCase, skipTests);
    });

    describe('integration', () => {
        const skipTests = [];
        read(`${crossplatformPath}/integration_test_data/`, createIntegrationTestCase, skipTests);
    });

    describe('unit', () => {
        const skipTests = [
            'patches',
            // animated images
            'div-gif-image',
            'new_state_incompatible'
        ];
        read(`${crossplatformPath}/unit_test_data/`, createSimpleTestCase, skipTests);
    });
});
