'use strict';

const KEYWORDS = [
    'undefined',
    '\\[object Object\\]',
    '\\bNaN\\b'
];

const WRAP_LEN = 64;

module.exports = async function() {
    const result = await this.execute(function(len, keywords) {
        var doc = document.cloneNode(true);
        var scripts = doc.querySelectorAll('script');

        for (var i = 0; i < scripts.length; ++i) {
            scripts[i].parentNode.removeChild(scripts[i]);
        }

        var regexp = new RegExp('.{0,' + len + '}(?=' + keywords.join('|') + ').{0,' + len + '}', 'g');

        return doc.documentElement.outerHTML.match(regexp);
    }, WRAP_LEN, KEYWORDS);

    if (result) {
        throw new Error(`Found bad keywords in html: ${result.join('\n')}`);
    }
};
