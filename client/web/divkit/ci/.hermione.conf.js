const path = require('path');
const fs = require('fs');
const chai = require('chai');

const tunnelerLocalPort = process.env.LOCAL_PORT || 8081;
const localGrid = !!process.env.HERMIONE_LOCAL_GRID;
const isGui = process.argv.includes('gui');

const retry = process.env.HERMIONE_RETRY !== undefined ?
    Number(process.env.HERMIONE_RETRY) :
    (isGui ? 0 : 10);

// RESULT_RESOURCES_PATH определяется в кубике common/misc/run_command
// https://a.yandex-team.ru/arc_vcs/ci/registry/common/misc
const HTML_REPORT_DIR = `hermione-gui-report`;

chai.config.includeStack = true;
chai.Should();
// chai.use(chaiAsPromised);
global.assert = chai.assert;

const plugins = {
    'static-server': {
        // path: path.resolve(__dirname, 'tests/hermione/static'),
        path: path.resolve(__dirname, '../../../..'),
        port: tunnelerLocalPort
    },
    'html-reporter/hermione': {
        path: HTML_REPORT_DIR
    },
    // '@yandex-int/tunneler/hermione': {
    //     enabled: true,
    //     tunnelerOpts: {
    //         localport: tunnelerLocalPort,
    //         user: process.env.tunneler_user || process.env.USER,
    //         sshRetries: 5,
    //         ports: {
    //             min: 1001,
    //             max: 65535,
    //         }
    //     }
    // },
    '@yandex-int/hermione-surfwax-router': {
        enabled: Boolean(process.env.TEAMCITY_VERSION || process.env.CI)
    }
};

module.exports = {
    sets: {
        all: {
            files: '../tests/hermione/**/*.hermione.js'
        }
    },

    browsers: {
        chromeMobile: {
            screenshotMode: 'viewport',
            // windowSize: '360x640',
            desiredCapabilities: {
                browserName: 'chrome',
                browserVersion: '90.0',
                acceptInsecureCerts: true,
                'goog:chromeOptions': {
                    mobileEmulation: {
                        // deviceName: 'Google Nexus 5',
                        deviceMetrics: {
                            width: 360,
                            height: 640,
                            pixelRatio: 2.0
                        },
                        userAgent: 'Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.3497.81 Mobile Safari/537.36 Hermione/1'
                    },
                    args: [
                        'headless',
                        'hide-scrollbars',
                        'disable-gpu'
                    ]
                }
            },
            meta: {platform: 'touch'}
        },
        firefoxMobile: {
            windowSize: '360x640',
            resetCursor: false,
            desiredCapabilities: {
                browserName: 'firefox',
                browserVersion: '69.0',
                acceptInsecureCerts: true,
                'moz:firefoxOptions': {
                    prefs: {
                        'general.useragent.override': 'Mozilla/5.0 (X11; Linux x86_64; rv:69.0) Gecko/20100101 Firefox/69.0 Hermione/1'
                    }
                }
            },
            meta: {platform: 'touch'}
        }
    },

    plugins,

    retry,

    sessionsPerBrowser: localGrid ? 1 : 5,
    testsPerSession: 10,

    gridUrl: localGrid ?
        'http://localhost:4444/wd/hub' :
        `http://morda@sw.yandex-team.ru:80/v0`,

    httpTimeout: 30000,
    pageLoadTimeout: 20000,
    sessionRequestTimeout: 150000,
    sessionQuitTimeout: 5000,
    waitTimeout: 10000,

    compositeImage: true,
    antialiasingTolerance: 4,

    system: {
        debug: false,

        // workers: 4,

        // Recommendation: use ctx in your tests in favor of global variables.
        ctx: {
        }
    },

    screenshotDelay: 400,
    screenshotsDir: (test) => {
        let basePath = '../tests/hermione/screens/';
        let testPath = '';
        let parent = test.parent;

        while(parent) {
            testPath = path.join(parent.title, testPath);
            parent = parent.parent;
        }

        return path.join(__dirname, basePath, testPath, test.title, test.browserId);
    },

    prepareBrowser: function (browser) {
        // chaiAsPromised.transferPromiseness = browser.transferPromiseness;

        const commandsDir = path.resolve(__dirname, '../tests/hermione/commands');

        fs.readdirSync(commandsDir)
            .filter(function(name) {
                return path.extname(name) === '.js' && fs.statSync(path.resolve(commandsDir, name)).isFile();
            })
            .forEach(function(filename) {
                browser.addCommand(path.basename(filename, '.js'), require(path.resolve(commandsDir, filename)));
            });
    }
};
