import { type Func, funcs } from '../../src/expressions/funcs/funcs';
import { register } from '../../src/expressions/funcs';

const path = require('path');
const fs = require('fs');

const dir = path.resolve(__filename, '../../../../../../test_data/expression_test_data');

const tests = fs.readdirSync(dir);

register();

function runCase(item: any) {
    const expectedArgs: any[] = item.arguments || [];

    let func: Func | null = null;
    const list = funcs.get(item.function_name);
    if (list) {
        for (let i = 0; i < list.length; ++i) {
            const candidate = list[i];

            if (candidate.args.length === expectedArgs.length) {
                if (candidate.args.every((candidateArg, index) => {
                    const expectedArg = expectedArgs[index];

                    const candidateType = typeof candidateArg === 'object' ? candidateArg.type : candidateArg;
                    const candidateVararg = typeof candidateArg === 'object' ? Boolean(candidateArg.isVararg) : false;

                    return candidateType === expectedArg.type && candidateVararg === Boolean(expectedArg.vararg);
                })) {
                    func = candidate;
                    break;
                }
            }
        }
    }

    expect(func).not.toBeNull();
}

describe('signatures', () => {
    for (const file of tests) {
        const name = file.replace('.json', '');
        const contents = require(path.resolve(dir, file));

        if (contents.signatures) {
            describe(name, () => {
                for (const item of contents.signatures) {
                    if (item.platforms.includes('web')) {
                        it(item.name, () => {
                            runCase(item);
                        });
                    } else {
                        // eslint-disable-next-line no-console
                        console.log('skip', file, name, item.name);
                    }
                }
            });
        }
    }
});
