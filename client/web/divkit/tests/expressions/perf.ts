/* eslint-disable no-console */

import { resolve } from 'node:path';
import { readdirSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { parse } from '../../src/expressions/expressions';

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
//@ts-ignore
const __filename = fileURLToPath(import.meta.url);

const dir = resolve(__filename, '../../../../../../test_data/expression_test_data');

const tests = readdirSync(dir);

const expressions: string[] = [];

for (const file of tests) {
    const contents = require(resolve(dir, file));

    if (contents.cases) {
        for (const item of contents.cases) {
            if (item.platforms.includes('web')) {
                expressions.push(item.expression);
            }
        }
    }
}

console.time('parse');
for (let i = 0; i < 100; ++i) {
    expressions.forEach(expr => {
        try {
            parse(expr, {
                startRule: 'JsonStringContents'
            });
        } catch (err) {}
    });
}
console.timeEnd('parse');
