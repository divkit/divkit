const path = require('path');
const fs = require('fs');

function read(dir, createTestCase, skipTests = []) {
    function append(dir, prefix) {
        const items = fs.readdirSync(path.join(__dirname, dir));

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

                createTestCase(testCase, testPath);
            } else {
                describe(item, function() {
                    append(path.join(dir, item), prefix + item + '/');
                });
            }
        }
    }

    append(dir, dir);
}

function createSimpleTestCase(testCase, testPath) {
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
            const { div_actions } = steps[i];

            if (div_actions) {
                for (const action of div_actions) {
                    await this.browser.execute(action => {
                        window.divkitRoot.execAction(action);
                    }, action);
                }
            }

            await this.browser.pause(300);
            await this.browser.assertView(`step${i}`, '#root');
        }
    });
}

const crossplatformPath = '../../../../../../test_data';
describe('crossplatform', () => {
    describe('samples', () => {
        const skipTests = [];
        read(`${crossplatformPath}/samples/`, createSimpleTestCase, skipTests);
    });

    describe('components', () => {
        const skipTests = [];
        read(`${crossplatformPath}/snapshot_test_data/`, createSimpleTestCase, skipTests);
    });

    describe('interactions', () => {
        const skipTests = [];
        read(`${crossplatformPath}/interactive_snapshot_test_data/`, createInteractiveTestCase, skipTests);
    });
});
