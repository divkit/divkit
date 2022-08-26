'use strict';

const set = {
    noClientErrors: require('./expectations/no-client-errors'),
    noStrangeKeywords: require('./expectations/no-strange-keywords')
};

const unit = [
    set.noClientErrors,
    set.noStrangeKeywords
];

const waitExpectations = function(browser, opts, list) {
    return Promise.all(list.map(func => func.call(browser, opts)));
};

exports.unit = function(browser, opts) {
    return waitExpectations(browser, opts, unit);
};
