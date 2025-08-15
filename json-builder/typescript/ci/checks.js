const {
    Graph
} = require('@yandex-int/tkit');

const useSelectivity = !process.argv.includes('--skipSelectivity') && !process.env.SKIP_SELECTIVITY;
const projectPrefix = 'public/json-builder/typescript';

function prepareSelectivity(list) {
    if (useSelectivity) {
        return list;
    }

    return undefined;
}

const graph = new Graph({
    repo: {},
    failOnStepFailure: false,
    selective: true,
    hideIntermediate: true,
    stopOnUpdate: false
});

const treeConfig = {
    'ts-jsonbuilder:check:ts': null,
    'ts-jsonbuilder:check:eslint': null,
    'ts-jsonbuilder:check:prettier': null,
    'ts-jsonbuilder:test:unit': null
};

const steps = {
    'ts-jsonbuilder:check:ts': [
        'npx tsc --noEmit',
        {
            files: prepareSelectivity([
                `${projectPrefix}/tsconfig.json`,
                `${projectPrefix}/**/*.ts`,
                `${projectPrefix}/package.json`,
                'public/schema/**'
            ])
        }
    ],
    'ts-jsonbuilder:check:eslint': [
        'npx eslint "src/**/*.ts" "test/**/*.ts"',
        {
            files: prepareSelectivity([
                `${projectPrefix}/**/*.js`,
                `${projectPrefix}/**/*.ts`,
                `${projectPrefix}/.eslintrc.json`
            ])
        }
    ],
    'ts-jsonbuilder:check:prettier': [
        'npx prettier --check "src/**/*.ts" "test/**/*.ts"',
        {
            files: prepareSelectivity([
                `${projectPrefix}/**/*.js`,
                `${projectPrefix}/**/*.ts`
            ])
        }
    ],
    'ts-jsonbuilder:test:unit': [
        'npx jest',
        {
            files: prepareSelectivity([
                `${projectPrefix}/**/*.ts`,
                `${projectPrefix}/**/*.snap`,
                'public/schema/**'
            ])
        }
    ]
};

graph.run(treeConfig, steps)
    .catch(err => {
        console.error(err);
        process.exit(1);
    });
