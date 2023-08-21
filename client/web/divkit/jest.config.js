module.exports = {
    transform: {
        '^.+\\.(js|jsx|ts|tsx)$': '<rootDir>/tools/test-transform.js'
    },
    transformIgnorePatterns: [],
    testMatch: [
        '<rootDir>/tests/**/*.test.ts'
    ],
    cacheDirectory: '<rootDir>/node_modules/.cache/jest',
    moduleNameMapper: {
        '^.+\\.svelte$': '<rootDir>/tools/svelte-mock.js'
    },
    coverageReporters: ['html-spa'],
    collectCoverageFrom: [
        '**/*.ts'
    ],
    coveragePathIgnorePatterns: [
        '/node_modules/'
    ],
    coverageDirectory: '<rootDir>/reports/coverage',
    // coverageThreshold: {
    //     global: {
    //         "branches": 100,
    //         "functions": 100,
    //         "lines": 100,
    //         "statements": 100,
    //     }
    // },
    reporters: [
        'default',
        [
            'jest-html-reporter',
            {
                outputPath: '<rootDir>/ci/jest-report/units/index.html',
                pageTitle: 'DivKit report',
                includeFailureMsg: true,
                includeConsoleLog: true,
                includeSuiteFailure: true,
                includeObsoleteSnapshots: true
            }
        ]
    ]
};
