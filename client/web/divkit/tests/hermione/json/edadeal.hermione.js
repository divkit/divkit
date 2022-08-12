const path = require('path');
const fs = require('fs');

describe('edadeal', () => {
    function read(dir) {
        function append(dir, prefix) {
            const items = fs.readdirSync(path.join(__dirname, dir));

            for (const item of items) {
                if (item.endsWith('.json')) {
                    if (item === 'templates.json') {
                        continue;
                    }

                    let testCase = item.replace('.json', '');
                    let testData = '/' + path.relative(path.resolve(__dirname, '../../../../../..'), path.resolve(__dirname, prefix + item));

                    it(`${testCase}`, async function() {
                        await this.browser.yaOpenJson(testData);
                        await this.browser.assertView(testCase, '#root');
                    });
                } else {
                    describe(item, function() {
                        append(path.join(dir, item), prefix + item + '/');
                    });
                }
            }
        }

        append(dir, dir);
    }

    read('./edadeal/');
});
