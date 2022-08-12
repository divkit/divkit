'use strict';

/**
 * Открывает указанную пример
 *
 * @alias browser.yaOpenCrossplatformJson
 *
 * @param {string} project
 * @param {string} platform
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
