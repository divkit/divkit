const {Graph} = require('@yandex-int/tkit');

(async function () {
    const graph = new Graph({
        repo: {},
        subscribers: [],
        failOnStepFailure: false,
        selective: true,
        hideIntermediate: true,
        stopOnUpdate: false
    });

    const treeConfig = {
        'client modules': null,
        'server modules': null,
        'lint server': ['server modules'],
        'lint client': ['client modules'],
        'svelte check': ['client modules']
    };
    const steps = {
        'client modules': [
            'cd client && npm ci',
            {
                intermediate: true
            }
        ],
        'server modules': [
            'cd server && npm ci',
            {
                intermediate: true
            }
        ],
        'lint server': [
            'npm run lint-server',
            {
                files: [
                    '.eslintrc.js',
                    'server/**'
                ]
            }
        ],
        'lint client': [
            'npm run lint-client',
            {
                files: [
                    '.eslintrc.js',
                    'client/**'
                ]
            }
        ],
        'svelte check': [
            'cd client && npm run check',
            {
                files: [
                    'client/**'
                ]
            }
        ]
    };

    return graph.run(treeConfig, steps);
})()
    .catch((err) => {
        console.error(err);
        process.exit(1);
    });
