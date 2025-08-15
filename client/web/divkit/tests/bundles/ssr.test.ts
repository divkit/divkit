/* eslint-disable import/no-extraneous-dependencies */
import {
    describe,
    expect,
    test
} from 'vitest';

import { render } from '@divkitframework/divkit';

const path = require('path');
const fs = require('fs');

const dir = path.resolve(__filename, '../../../../../../test_data/snapshot_test_data');

const tests = fs.readdirSync(dir);

function scanDir(dir: string, list: string[]): void {
    for (const file of list) {
        if (fs.lstatSync(path.resolve(dir, file)).isDirectory()) {
            const newDir = path.resolve(dir, file);
            const newList = fs.readdirSync(newDir);

            describe(file, () => {
                scanDir(newDir, newList);
            });
        } else if (file.endsWith('.json')) {
            const contents = require(path.resolve(dir, file));
            if (!contents.platforms || contents.platforms.includes('web')) {
                test(file.replace('.json', ''), () => {
                    expect(typeof render({
                        id: 'test',
                        json: contents
                    })).toEqual('string');
                });
            }
        }
    }
}

describe('ssr', () => {
    scanDir(dir, tests);
});
