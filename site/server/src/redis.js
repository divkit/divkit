const Redis = require("ioredis");

module.exports = {
    publishJsonUpdate,
    subscribeJsonUpdate,
    unsubscribeJsonUpdate,
};

const port = process.env.DIVVIEW_REDIS_PORT || 6379;
const hosts = (process.env.DIVVIEW_REDIS_HOSTS || '').split(',');
const password = process.env.DIVVIEW_REDIS_PASSWORD;

const updateJsonChannel = process.env.DIVVIEW_REDIS_UPDATE_JSON_CHANNEL || 'update-json-channel';
// const updateStateChannel = process.env.DIVVIEW_REDIS_UPDATE_STATE_CHANNEL || 'update-state-channel';

let connectOptions;
const hostsDesc = hosts.map(host => ({host, port}));

if (process.env.NODE_ENV === 'production') {
    connectOptions = [hostsDesc, {
        redisOptions: {
            password
        }
    }];
} else {
    connectOptions = [];
}

if (!hosts.length) {
    throw new Error('Missing DIVVIEW_REDIS_HOSTS');
}

const subscribers = {};
const connectSubscribe = new Promise(resolve => {
    const clusterForSubscribe = connectOptions.length ? new Redis.Cluster(...connectOptions) : new Redis();

    clusterForSubscribe.on('ready', () => {
        console.log('redis subscribe channel connected');
        resolve(clusterForSubscribe);

        clusterForSubscribe.on('message', (channel, message) => {
            const uuid = channel.split(':')[1];
            if (!uuid) {
                return;
            }
            // console.log('redis received', uuid, message.slice(0, 100));
            if (subscribers[uuid]) {
                subscribers[uuid](message);
            }
        });
    });
    clusterForSubscribe.on('error', err => {
        console.error('redis subscribe connect error', err);
    });
});

const connectPublish = new Promise(resolve => {
    const clusterForPublish = connectOptions.length ? new Redis.Cluster(...connectOptions) : new Redis();

    clusterForPublish.on('ready', () => {
        console.log('redis publish channel connected');
        resolve(clusterForPublish);
    });
    clusterForPublish.on('error', err => {
        console.error('redis publish connect error', err);
    });
});

async function publishJsonUpdate(uuid, message) {
    const clusterForPublish = await connectPublish;

    // console.log('redis publish', message.slice(0, 100));
    clusterForPublish.publish(updateJsonChannel + ':' + uuid, message);
}

async function subscribeJsonUpdate(uuid, fn) {
    if (subscribers[uuid]) {
        return;
    }

    const clusterForSubscribe = await connectSubscribe;

    subscribers[uuid] = fn;
    clusterForSubscribe.subscribe(updateJsonChannel + ':' + uuid, (err, count) => {
        if (err) {
            console.error("Failed to subscribe: %s", err.message);
        } else {
            console.log(
                `Redis subscribed to ${uuid} successfully! This client is currently subscribed to ${count} channels.`
            );
        }
    });
}

async function unsubscribeJsonUpdate(uuid) {
    const clusterForSubscribe = await connectSubscribe;

    if (!subscribers[uuid]) {
        return
    }
    delete subscribers[uuid];

    clusterForSubscribe.unsubscribe(updateJsonChannel + ':' + uuid, (err, count) => {
        if (err) {
            console.error('Failed to unsubscribe: %s', err.message);
        } else {
            console.log('Redis successfully unsubscribed from %s and now subscribed to %s', uuid, count);
        }
    });
}
