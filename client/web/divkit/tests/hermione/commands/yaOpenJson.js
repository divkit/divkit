'use strict';

/**
 * Opens specific json
 *
 * @alias browser.yaOpenJson
 *
 * @param {string} jsonPath
 * @param {YaOpenExampleParams} params
 *
 * @returns {Promise}
 */
module.exports = async function(jsonPath, params = {}) {
    await this.yaOpenExample('client/web/divkit/tests/hermione/static/index.html', {
        query: {
            json: jsonPath
        }
    });
};
