const path = require('path');
const fs = require('fs');
const os = require('os');

const registryHostname = 'registry.npmjs.org';
const username = 'robot-divkit';
const email = 'robot-divkit@yandex-team.ru';
const token = process.env.ROBOT_NPM_TOKEN;

if (!token) {
    throw new Error('No token');
}

const filename = path.join(os.homedir(), '.npmrc');
const content = [
    `//${registryHostname}/:_authToken="${token}"`,
    `//${registryHostname}/:always-auth=false`,
].join('\n');

// eslint-disable-next-line no-console
console.log(`Writing npm auth token for ${username} (${email}) at ${registryHostname} to ${filename}`);

fs.writeFileSync(filename, content);
