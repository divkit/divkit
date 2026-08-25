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

        let snapshotIndex = 0;
        for (const step of steps) {
            if (step.type === 'div_action') {
                await this.browser.execute(action => {
                    window.divkitRoot.execAction(action);
                }, step.action);
            } else if (step.type === 'wait') {
                await this.browser.pause(step.duration_ms);
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
                    result_type: resultType,
                    mock_video_playback: item.mock_video_playback
                });
                await this.browser.execute(() => {
                    localStorage.clear();
                });

                for (const action of actions) {
                    await this.browser.execute(action => {
                        window.divkitRoot.execAction(action);
                    }, action);
                }

                if (item.mock_video_playback) {
                    await this.browser.waitUntil(async () => {
                        return this.browser.execute(() => {
                            const getPlaybackState = id => {
                                const component = document.querySelector(`[data-test-id="${id}"]`);
                                const video = component && component.querySelector('video');
                                return video && video.getAttribute('data-mock-playback-state');
                            };

                            return getPlaybackState('paused_video') === 'paused' &&
                                getPlaybackState('autostart_video') === 'playing';
                        });
                    }, {
                        timeout: 3000,
                        interval: 100,
                        timeoutMsg: 'expected mocked videos to reach their requested playback states'
                    });
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
                    if (item.mock_video_playback) {
                        continue;
                    }

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
