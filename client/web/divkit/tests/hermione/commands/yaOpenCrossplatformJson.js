'use strict';

/**
 * Opens example from multiplatform tests
 *
 * @alias browser.yaOpenCrossplatformJson
 *
 * @param {string} jsonPath
 * @param {YaOpenExampleParams} params
 *
 * @returns {Promise}
 */
module.exports = async function(jsonPath, params = {}) {
    await this.yaOpenExample('client/web/divkit/tests/hermione/static/index.html', {
        query: {
            crossplatform_json: jsonPath
        }
    });
};
