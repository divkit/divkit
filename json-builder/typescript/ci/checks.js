const {
    Graph
} = require('@yandex-int/tkit');

const graph = new Graph({
    repo: {},
    failOnStepFailure: false,
    selective: true,
    hideIntermediate: true,
    stopOnUpdate: false
});
const treeConfig = {
    'check:ts': null,
    'check:eslint': null,
    'check:prettier': null,
    'test:unit': null
};

const steps = {
    'check:ts': [
        'npx tsc --noEmit',
        {
            files: [
                'tsconfig.json',
                '**.ts',
                'package.json'
            ]
        }
    ],
    'check:eslint': [
        'npx eslint "src/**/*.ts" "test/**/*.ts"',
        {
            files: [
                '**.js',
                '**.ts',
                '.eslintrc.json'
            ]
        }
    ],
    'check:prettier': [
        'npx prettier --check "src/**/*.ts" "test/**/*.ts"',
        {
            files: [
                '**.js',
                '**.ts'
            ]
        }
    ],
    'test:unit': [
        'npx jest',
        {
            files: [
                '**.ts',
                '**.snap'
            ]
        }
    ]
};

graph.run(treeConfig, steps)
    .catch(err => {
        console.error(err);
        process.exit(1);
    });
