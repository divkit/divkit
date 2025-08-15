const path = require('path');
const {fork} = require('child_process');

let retryCounter = 0;
let child = initChild();
function initChild() {
    let child = fork(path.resolve(__dirname, 'runTsWorker.js'), {
        silent: true
    });

    child.on('message', onMessage);

    child.stdout.on('data', data => {
        console.log(String(data));
    });

    child.stderr.on('data', data => {
        console.log(String(data));
    });

    child.on('exit', (code, signal) => {
        console.log(`Typescript friend exited with ${code} / ${signal}`);

        setTimeout(() => {
            child = initChild();
        }, ++retryCounter * 1000);
    });

    return child;
}

let counter = 0;
let tasks = new Map();

function onMessage(msg) {
    const task = tasks.get(msg.id);
    if (task && task.resolve) {
        if (msg.ok) {
            task.resolve(msg.result);
        } else {
            task.reject(new Error(msg.error));
        }
        tasks.delete(msg.id);
    }
}

exports.runTs = function runTs(code) {
    const id = ++counter;

    return new Promise((resolve, reject) => {
        tasks.set(id, {
            resolve,
            reject
        });

        setTimeout(() => reject(new Error('Timeout')), 5000);

        if (!child.send({
            id,
            code
        })) {
            reject(new Error('Something went wrong'));
        }
    });
}
