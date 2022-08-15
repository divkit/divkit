const {
    Graph
} = require('@yandex-int/tkit');

const useSelectivity = !process.argv.includes('--skipSelectivity');
const projectPrefix = 'public/client/web/divkit';

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
    'web:check:ts': null,
    'web:check:eslint': null,
    'web:check:svelte': null,
    'web:build:prod': null,
    'web:test:unit': ['web:build:prod'],
    'web:test:hermione': ['web:build:prod'],
};

const steps = {
    'web:check:ts': [
        'npm run check:ts',
        {
            /*files: prepareSelectivity([
                `${projectPrefix}/tsconfig.json`,
                `${projectPrefix}/!**!/!*.svelte`,
                `${projectPrefix}/!**!/!*.ts`,
                `${projectPrefix}/package.json`
            ])*/
        }
    ],
    'web:check:eslint': [
        'npm run check:eslint',
        {
            /*files: prepareSelectivity([
                `${projectPrefix}/!**!/!*.js`,
                `${projectPrefix}/!**!/!*.ts`,
                `${projectPrefix}/!**!/!*.svelte`,
                `${projectPrefix}/tsconfig.json`,
                `${projectPrefix}/package.json`,
                `${projectPrefix}/.eslintrc.js`
            ])*/
        }
    ],
    'web:check:svelte': [
        'npm run check:svelte',
        {
            /*files: prepareSelectivity([
                `${projectPrefix}/!**!/!*.svelte`,
                `${projectPrefix}/!**!/!*.ts`,
                `${projectPrefix}/package.json`
            ])*/
        }
    ],
    'web:build:prod': [
        'npm run build:prod',
        {
            intermediate: true,
            /*files: prepareSelectivity([
                `${projectPrefix}/src/!**`,
                `${projectPrefix}/webpack.config.js`,
                'public/test_data/!**'
            ])*/
        }
    ],
    'web:test:unit': [
        'npm run test:unit',
        {
            /*files: prepareSelectivity([
                `${projectPrefix}/!**!/!*.ts`,
                `${projectPrefix}/tests/templates`,
                `${projectPrefix}/tests/utils`,
                `${projectPrefix}/tools`,
                `${projectPrefix}/package.json`,
                `${projectPrefix}/jest.config.js`,
                `${projectPrefix}/webpack.config.js`,
                'public/test_data/!**'
            ])*/
        }
    ],
    'web:test:hermione': [
        'npm run test:hermione',
        {
            /*files: prepareSelectivity([
                `${projectPrefix}/src/!**`,
                `${projectPrefix}/tests/hermione`,
                `${projectPrefix}/.hermione.conf.js`,
                `${projectPrefix}/package.json`,
                `${projectPrefix}/webpack.config.js`,
                'public/test_data/!**'
            ]),*/
            report: 'hermione-gui-report',
            main: 'index.html',
            attributes: {
                project: 'home',
                tool: 'hermione',
                type: 'hermione-report',
                review_request: process.env.ARC_BRANCH
            },
            type: 'SANDBOX_CI_ARTIFACT'
        }
    ]
};

graph.run(treeConfig, steps)
    .catch(err => {
        console.error(err);
        process.exit(1);
    });
