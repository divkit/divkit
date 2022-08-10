'use strict';

function isNotSupportedError(err) {
    return err.type === 'ProtocolError' &&
            err.message.match(/this log type .* is not available for this browser\/device/) ||
        err.type === 'RuntimeError' &&
            err.message.includes('Command not found');
}

module.exports = function(opts = {}) {
    if (this.capabilities.browserName !== 'chrome') {
        return;
    }

    return this
        .getLogs('browser')
        .then(list => {
            if (opts.ignoreErrorsSource) {
                list = list.filter(err => !opts.ignoreErrorsSource.test(err.source));
            }

            if (opts.ignoreErrorsMessage) {
                list = list.filter(err => !opts.ignoreErrorsMessage.test(err.message));
            }

            list = list.filter(err => err.level !== 'WARNING');
            list = list.filter(err => err.source !== 'recommendation');
            list = list.filter(err => err.source !== 'deprecation');
            list = list.filter(err => err.source !== 'network');

            if (list.length !== 0) {
                throw new Error(list.map(err => {
                    return `Browser ${err.source} error found: ${err.message} (${JSON.stringify(err)})`;
                }).join('\n'));
            }
        }, err => {
            if (isNotSupportedError(err)) {
                return;
            }
            throw err;
        });
};
