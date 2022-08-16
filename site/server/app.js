const fs = require('fs');

const WebSocket = require('ws');

const Koa = require('koa');
const Router = require('koa-router');
const bodyParser = require('koa-bodyparser');
const serve = require('koa-static');
const compress = require('koa-compress');
const cors = require('@koa/cors');

const { v4: uuidv4 } = require('uuid');

// const {connect, set, get} = require('./src/db');
const {runTs} = require('./src/runTs');

/*connect().then(() => {
    console.log('db connected');
}).catch(err => {
    console.error('failed to connect', err);
});*/

const app = new Koa();
const router = new Router();

/*router.post('/api/share', async ctx => {
    const uuid = uuidv4();
    const writeKey = uuidv4();

    ctx.set('Cache-Control', 'no-cache,no-store,max-age=0,must-revalidate');

    try {
        const lang = ctx.request.body.language;
        const source = ctx.request.body.value;
        const json = ctx.request.body.json;

        await set(uuid, {
            source: lang === 'json' ? null : source,
            json: lang === 'json' ? source : json,
            language: lang,
            writeKey,
        });

        ctx.body = {
            ok: 1,
            uuid,
            writeKey
        };
    } catch (err) {
        console.error(err);

        ctx.status = 500;
    }
});*/

/*router.post('/api/updateSource', async ctx => {
    const uuid = ctx.request.body.uuid;
    const source = ctx.request.body.value;
    const lang = ctx.request.body.language;
    const writeKey = ctx.request.body.writeKey;

    ctx.set('Cache-Control', 'no-cache,no-store,max-age=0,must-revalidate');

    const item = await get(uuid);

    if (item && item.writeKey === writeKey) {
        try {
            await set(uuid, {
                source: lang === 'json' ? null : source,
                json: lang === 'json' ? source : item.json,
                language: lang
            });

            ctx.body = {
                ok: 1,
                uuid,
                writeKey
            };

            console.log('updateSource', uuid);
            const data = toJson(source);

            if (data && lang === 'json') {
                const message = JSON.stringify({type: 'json', message: {json: data}});
                if (roomDevices.has(uuid)) {
                    for (const ws of roomDevices.get(uuid)) {
                        ws.send(message);
                    }
                }
            }
        } catch (err) {
            console.error(err);

            ctx.status = 500;
        }
    } else {
        ctx.status = 404;
    }
});*/

/*router.get('/api/json', async ctx => {
    const uuid = ctx.query.uuid;

    ctx.set('Cache-Control', 'no-cache,no-store,max-age=0,must-revalidate');

    try {
        const item = await get(uuid);

        if (item) {
            ctx.body = item.json;

            await set(uuid, item);
        } else {
            ctx.status = 404;
        }
    } catch (err) {
        console.error(err);

        ctx.status = 500;
    }
});*/

/*router.get('/api/source', async ctx => {
    const uuid = ctx.query.uuid;

    ctx.set('Cache-Control', 'no-cache,no-store,max-age=0,must-revalidate');

    try {
        const item = await get(uuid);

        if (item) {
            ctx.body = {
                source: (item.language === 'json' || !item.language) ? item.json : item.source,
                json: item.json,
                language: item.language || 'json'
            };

            await set(uuid, item);
        } else {
            ctx.status = 404;
        }
    } catch (err) {
        console.error(err);

        ctx.status = 500;
    }
});*/

router.post('/api/runTs', async ctx => {
    const code = ctx.request.body.code;
    const uuid = ctx.request.body.uuid;
    const writeKey = ctx.request.body.writeKey;

    ctx.set('Cache-Control', 'no-cache,no-store,max-age=0,must-revalidate');

    if (!code) {
        ctx.status = 400;
        return;
    }

    const item = (uuid && writeKey) ? await get(uuid) : null;

    if (uuid && writeKey && !(item && item.writeKey === writeKey)) {
        ctx.status = 400;
        return;
    }

    try {
        const res = await runTs(code);
        const strResult = JSON.stringify(res);

        if (strResult.length > 10000) {
            throw new Error('Too much data');
        }

        if (item) {
            await set(uuid, {
                json: strResult
            })
        }

        ctx.body = {
            ok: 1,
            result: strResult
        };

        /*const message = JSON.stringify({type: 'json', message: {json: res}});
        if (roomDevices.has(uuid)) {
            for (const ws of roomDevices.get(uuid)) {
                ws.send(message);
            }
        }*/
    } catch (err) {
        console.error(err);
        ctx.body = {
            error: String(err && err.message || err || 'Unknown error')
        };
    }
});

app.use(compress({
    filter () {
        return true;
    },
    // threshold: 2048,
    gzip: {
        flush: require('zlib').constants.Z_SYNC_FLUSH
    },
    deflate: {
        flush: require('zlib').constants.Z_SYNC_FLUSH,
    },
    br: false // disable brotli
}));

app.use(serve(__dirname + '/dist'));

if (process.env.NODE_ENV !== 'production') {
    app.use(cors());
}
app.use(bodyParser({
    jsonLimit: '10mb'
}));
app.use(router.routes());
app.use(router.allowedMethods());

const port = process.env.NODE_ENV === 'production' ? 80 : 3000;
const server = app.listen(port);

/*const wss = new WebSocket.Server({server});

/!** @type Map<string, Set<WebSocket>> *!/
const roomDevices = new Map();
/!** @type Map<WebSocket, string> *!/
const deviceToUuid = new Map();
/!** @type {Map<WebSocket, { device: object, lastState: object }>} *!/
const deviceState = new Map();
/!** @type {Map<string, Set<WebSocket>>} *!/
const roomListeners = new Map();
/!** @type {Map<WebSocket, string>} *!/
const roomListenerToUuid = new Map();

function toJson(str) {
    try {
        return JSON.parse(str);
    } catch (err) {
        return null;
    }
}

function deviceToId(device) {
    return device.client_id;
}

function disconnectDevice(ws) {
    let uuid = deviceToUuid.get(ws);
    if (uuid) {
        const prevSet = roomDevices.get(uuid);
        if (prevSet) {
            prevSet.delete(ws);
        }
        deviceToUuid.delete(ws);
    }

    const state = deviceState.get(ws);
    if (state) {
        deviceState.delete(ws);
    }

    if (uuid && roomListeners.has(uuid) && state) {
        for (const listenerWs of roomListeners.get(uuid)) {
            listenerWs.send(JSON.stringify({
                type: 'disconnect_room',
                message: {
                    uuid,
                    id: deviceToId(state.device)
                }
            }));
        }
    }
}

const wsCheckInterval = setInterval(function ping() {
    wss.clients.forEach(ws => {
        if (ws.isAlive === false) {
            return ws.terminate();
        }

        ws.isAlive = false;
        ws.ping();
    });
}, 30000);

wss.on('close', function close() {
    clearInterval(wsCheckInterval);
});

wss.on('connection', function connection(ws) {
    ws.isAlive = true;

    ws.on('pong', () => {
        ws.isAlive = true;
    });

    ws.on('message', async function incoming(message) {
        console.log('received', message);

        let json;
        try {
            json = JSON.parse(message);
        } catch (err) {
            return;
        }

        if (!json.type || !json.message) {
            return;
        }

        switch (json.type) {
            case 'listen': {
                const uuid = json.message.uuid;
                const item = await get(uuid);
                if (!item) {
                    console.log('listen - no data', uuid);
                    ws.send(JSON.stringify({type: 'error', message: {text: 'missing uuid'}}));
                    return;
                }

                disconnectDevice(ws);

                if (!roomDevices.has(uuid)) {
                    roomDevices.set(uuid, new Set());
                }
                roomDevices.get(uuid).add(ws);
                deviceToUuid.set(ws, uuid);

                const data = toJson(item.json);
                if (data) {
                    ws.send(JSON.stringify({type: 'json', message: {json: data}}));
                } else {
                    console.log('listen - not a json', uuid);
                }
                break;
            }
            case 'ui_state': {
                let item;
                if (deviceState.has(ws)) {
                    item = deviceState.get(ws);
                } else {
                    deviceState.set(ws, item = {
                        device: json.message.device,
                        lastState: null
                    });
                }

                item.lastState = {
                    screenshot: json.message.screenshot,
                    errors: json.message.errors
                };

                const uuid = deviceToUuid.get(ws);
                if (uuid) {
                    const listeners = roomListeners.get(uuid);
                    if (listeners) {
                        for (const listenerWs of listeners) {
                            listenerWs.send(JSON.stringify({
                                type: 'update_state',
                                message: {
                                    uuid,
                                    id: deviceToId(json.message.device),
                                    device: json.message.device,
                                    lastState: item.lastState
                                }
                            }));

                            delete awaitsDevice[uuid];
                        }
                    }
                }

                break;
            }
            case 'listen_room': {
                if (roomListenerToUuid.has(ws)) {
                    const prevUuid = roomListenerToUuid.get(ws);
                    if (roomListeners.has(prevUuid)) {
                        roomListeners.get(prevUuid).delete(ws);
                    }
                    roomListenerToUuid.delete(ws);
                }

                const uuid = json.message.uuid;
                roomListenerToUuid.set(ws, uuid);
                if (!roomListeners.has(uuid)) {
                    roomListeners.set(uuid, new Set());
                }

                roomListeners.get(uuid).add(ws);

                if (roomDevices.has(uuid)) {
                    for (const deviceWs of roomDevices.get(uuid)) {
                        const state = deviceState.get(deviceWs);

                        if (state) {
                            ws.send(JSON.stringify({
                                type: 'update_state',
                                message: {
                                    id: deviceToId(state.device),
                                    device: state.device,
                                    lastState: state.lastState
                                }
                            }));
                        }
                    }
                }

                break;
            }
        }
    });

    ws.on('close', () => {
        disconnectDevice(ws);
    });
});*/

console.log(`listening on port ${port}`);
