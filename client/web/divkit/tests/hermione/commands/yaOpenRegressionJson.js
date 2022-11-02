'use strict';

/**
 * Opens example from regression tests
 *
 * @alias browser.yaOpenRegressionJson
 *
 * @param {string} fileName
 *
 * @returns {Promise}
 */
module.exports = async function(fileName) {
    await this.yaOpenExample('client/web/divkit/tests/hermione/static/index.html', {
        query: {
            crossplatform_json: `../../../../../../test_data/regression_test_data/${fileName}.json`
        }
    });
};
