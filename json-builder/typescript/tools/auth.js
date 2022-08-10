const path = require('path');
const fs = require('fs');
const os = require('os');

const registryHostname = 'npm.yandex-team.ru';
const username = 'robot-divkit';
const email = 'robot-divkit@yandex-team.ru';
const password = process.env.ROBOT_NPM_OAUTH;

if (!password) {
    throw new Error('No password');
}

const filename = path.join(os.homedir(), '.npmrc');
const content = [
    `//${registryHostname}/:_password="${Buffer.from(password).toString('base64')}"`,
    `//${registryHostname}/:username=${username}`,
    `//${registryHostname}/:email=${email}`,
    `//${registryHostname}/:always-auth=false`,
].join('\n');

// eslint-disable-next-line no-console
console.log(`Writing npm auth credentials for ${username} (${email}) at ${registryHostname} to ${filename}`);

fs.writeFileSync(filename, content);
