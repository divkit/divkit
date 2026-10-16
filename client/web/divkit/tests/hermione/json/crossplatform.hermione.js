const path = require('path');
const fs = require('fs');

const testPaths = new Set(hermione.ctx.screenshotTestPaths);
const filterTests = testPaths.size > 0;
// Explicit CI mode: no changed file matches a Web test (e.g. only JSONs of
// other platforms are touched), so no crossplatform test runs at all.
const skipAllTests = process.env.CROSSPLATFORM_SKIP === '1';
// Unmatched paths are only tolerated when the list was computed by CI
// (ci/web.sh). Manual runs, including direct screenshot updates, stay strict.
const lenientUnmatchedPaths = process.env.CROSSPLATFORM_SELECTIVITY_CI === '1';

function read(dir, createTestCase, skipTests = [], skipFirefox = []) {
    function append(dir, prefix) {
        if (skipAllTests) {
            return;
        }

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

                const relativePath = path.relative(path.resolve(__dirname, crossplatformPath), path.join(fulldir, item));
                if (filterTests && !testPaths.has(relativePath)) {
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
                testPaths.delete(relativePath);
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

        let snapshotIndex = 0;
        for (const step of steps) {
            if (step.type === 'div_action') {
                await this.browser.execute(action => {
                    window.divkitRoot.execAction(action);
                }, step.action);
            } else if (step.type === 'wait') {
                await this.browser.pause(step.duration_ms);
            } else if (step.type === 'verify_text') {
                if (!step.target || step.target.type !== 'div_id') {
                    throw new Error(`Unsupported verify_text target type: ${step.target?.type}`);
                }
                const actualText = await this.browser.execute(id => {
                    const element = [...document.querySelectorAll('[data-test-id]')]
                        .find(candidate => candidate.getAttribute('data-test-id') === id);
                    return element ? element.innerText : null;
                }, step.target.id);
                if (actualText === null) {
                    throw new Error(`No view with id '${step.target.id}'`);
                }
                actualText.should.equal(step.text, `Text mismatch for id '${step.target.id}'`);
            } else if (step.type === 'verify_snapshot') {
                await this.browser.pause(300);
                await this.browser.assertView(`step${snapshotIndex}`, '#root');
                snapshotIndex++;
            } else {
                throw new Error(`Unsupported interactive step type: ${step.type}`);
            }
        }
    });
}

function createIntegrationTestCase(testCase, testPath) {
    const testData = JSON.parse(fs.readFileSync(path.join(path.resolve(__dirname, '../../..'), testPath), 'utf8'));
    const cases = testData.steps ? [testData] : testData.cases;

    for (let i = 0; i < cases.length; ++i) {
        const item = cases[i];

        if (item.platforms && !item.platforms.includes('web')) {
            continue;
        }

        const actions = [];
        const expectedVariables = [];
        const expectedErrors = [];
        let verificationFound = false;

        for (const step of item.steps) {
            if (step.type === 'div_action') {
                if (verificationFound) {
                    throw new Error('div_action after verification is not supported by the Web integration runner');
                }
                actions.push(step.action);
            } else if (step.type === 'verify_variable') {
                verificationFound = true;
                expectedVariables.push(step);
            } else if (step.type === 'verify_errors') {
                verificationFound = true;
                expectedErrors.push(...step.errors);
            } else {
                throw new Error(`Unsupported integration step type: ${step.type}`);
            }
        }

        const resultType = expectedVariables.find(it => it.variable_name === 'result')?.value?.type;

        describe(testData.description, () => {
            it(`Case [${i}]`, async function() {
                await this.browser.yaOpenCrossplatformJson(testPath, {
                    result_type: resultType
                });
                await this.browser.execute(() => {
                    localStorage.clear();
                });

                for (const action of actions) {
                    await this.browser.execute(action => {
                        window.divkitRoot.execAction(action);
                    }, action);
                }

                const errors = await this.browser.execute(() => {
                    return (window.errors || []).filter(error => {
                        // Filter video errors
                        return error.message !== 'Video playing error';
                    }).map(error => {
                        const additionalMessage = error.additional ?
                            error.additional.message :
                            undefined;

                        return additionalMessage || error.message;
                    });
                });

                if (errors.length !== expectedErrors.length) {
                    console.error({ errors, expectedErrors });
                }

                errors.length.should.equal(expectedErrors.length);

                for (let j = 0; j < expectedErrors.length; ++j) {
                    if (errors[j] !== expectedErrors[j]) {
                        console.error({ actual: errors[j], expected: expectedErrors[j] });
                    }

                    errors[j].should.equal(expectedErrors[j]);
                }

                for (const expected of expectedVariables) {
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

if (testPaths.size > 0) {
    const message = `No Web tests found for paths relative to divkit/public/test_data: ${[...testPaths].join(', ')}`;
    if (!lenientUnmatchedPaths) {
        // Manual runs (including direct screenshot updates) must never
        // silently skip a requested test.
        throw new Error(message);
    }
    // A changed JSON may belong to another platform (filtered by `platforms`)
    // or be skipped on Web; do not fail the whole CI run for it.
    console.warn(`[WARN] ${message}`);
}
