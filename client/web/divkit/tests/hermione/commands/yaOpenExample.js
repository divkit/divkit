'use strict';

/**
 * Opens specific example
 *
 * @alias browser.yaOpenExample
 *
 * @param {string} example
 * @param {YaOpenExampleParams} params
 *
 * @returns {Promise}
 */
module.exports = async function(example, params = {}) {
    const url = require('url');
    const expectations = require('./page-expectations').unit;

    const [hostname, port] = await Promise.all([
        this.getMeta('static:hostname'),
        this.getMeta('static:port')
    ]);

    const urlObj = {
        protocol: 'https:',
        hostname,
        port,
        pathname: example,
        query: params.query
    };

    await this.url(url.format(urlObj));

    await this.waitUntil(() => {
        return this.$('#root > *').then(elem => elem.isExisting());
    }, {
        timeout: 5000,
        timeoutMsg: 'expected json to render 5s'
    });

    await this.executeAsync(done => {
        if (document.readyState === 'complete') {
            done();
        } else {
            document.addEventListener('load', done);
        }
    });

    await expectations(this, params.expectations);
};

/**
 * @typedef YaOpenExampleParams
 * @property {String} [subpage] - имя файла (без .html), если необходимо загрузить страницу отличную от index
 */
