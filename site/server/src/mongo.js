const util = require('util');
const path = require('path');
const {MongoClient} = require('mongodb');

module.exports = {
    connectMongo,
    getMongo,
    setMongo
};

const cert = path.resolve(__dirname, '..', 'artifacts', 'YandexInternalRootCA.crt');

const isProd = process.env.NODE_ENV === 'production';

const DB_NAME = process.env.DIVVIEW_MONGO_DB || 'divview';
const DB_COLLECTION = 'json';

const TIME_TO_CLEANUP = 60 * 60 * 24; // 24 hours in seconds

let url;
if (isProd) {
    url = util.format(
        'mongodb://%s:%s@%s/?replicaSet=%s&authSource=%s&ssl=true',
        process.env.DIVVIEW_MONGO_USER || 'divviewuser',
        process.env.DIVVIEW_MONGO_PASSWORD,
        process.env.DIVVIEW_MONGO_CONNECT,
        'rs01',
        DB_NAME
    );
} else {
    url = 'mongodb://127.0.0.1:27017';
}

let promise;
/** @type MongoClient */
let client;
function connectMongo() {
    if (promise) {
        return promise;
    }

    promise = Promise.resolve().then(() => {
        client = new MongoClient(url, {
            sslCA: cert,
            readPreference: 'primaryPreferred'
        });

        return client.connect();
    }).then(() => {
        const db = client.db(DB_NAME);
        const collection = db.collection(DB_COLLECTION);
        return collection.createIndex('uuid', {
            unique: true
        }).then(() => {
            return collection.createIndex('ts', {
                expireAfterSeconds: TIME_TO_CLEANUP
            })
        }).then(() => {
            return db;
        });
    });

    return promise;
}

async function setMongo(uuid, vals) {
    await connectMongo();

    const db = client.db(DB_NAME);
    const collection = db.collection(DB_COLLECTION);

    return collection.updateOne({
        uuid
    }, {
        $set: {
            ...vals,
            ts: new Date()
        }
    }, {
        upsert: true
    });
}

async function getMongo(uuid, projection = {}) {
    await connectMongo();

    const db = client.db(DB_NAME);
    const collection = db.collection(DB_COLLECTION);

    return collection.findOne({
        uuid
    }, {projection});
}
