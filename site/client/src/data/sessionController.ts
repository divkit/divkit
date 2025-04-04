import { get } from 'svelte/store';
import { initialValueStore, valueStore } from './valueStore';
import { isDesign, isFeatures, isInitialLoading, isLoadError, isSamples, session } from './session';
import { debounce } from '../utils/debounce';
import { clientHostPath, serverHostPath } from '../utils/const';
import { savedStore } from './savedStore';
import { editorMode } from './editorMode';
import { jsonStore } from './jsonStore';
// import { viewModeStore } from './viewModeStore';
// import { addListener, wsPromise } from './ws';
// import { listenToDevices } from './externalViewers';
import { getLs, setLs } from '../utils/localStorage';
import { DEFAULT_EDITOR_VALUE } from './defaultEditorValue';

/* function listenJsonForPreview(uuid: string): void {
    wsPromise.then(ws => {
        ws.send(JSON.stringify({
            type: 'listen',
            message: {
                uuid
            }
        }));
    });
    addListener(msg => {
        if (msg.type === 'json') {
            jsonStore.set(msg.message.json);
        }
    });
} */

// let mode = '';

async function init() {
    const params = new URLSearchParams(location.search);

    const uuid = params.get('uuid');
    // mode = params.get('mode') || '';

    const design = params.get('design') === '1';
    const samples = params.get('samples') === '1';
    const features = params.get('features') === '1' || location.pathname.startsWith('/features');

    if (features) {
        isFeatures.set(true);
    } else if (design) {
        isDesign.set(true);
        initialValueStore.set(DEFAULT_EDITOR_VALUE);
        valueStore.set(DEFAULT_EDITOR_VALUE);
    } else if (samples) {
        isSamples.set(true);
    }

    /*if (mode) {
        viewModeStore.set(mode);
        if (mode === 'preview' && uuid) {
            listenJsonForPreview(uuid);
        }
    }*/
    /*if (mode !== 'preview') {
        listenToDevices();
    }*/

    if (uuid && !features) {
        isInitialLoading.set(true);
        const writeKey = getLs(`session_write_key_${uuid}`) || '';

        return fetch(`//${serverHostPath}api/source?uuid=${uuid}`)
            .then(res => {
                if (!res.ok) {
                    throw new Error('Not ok');
                }
                return res.json();
            })
            .then(res => {
                session.set({
                    uuid,
                    writeKey
                });

                initialValueStore.set(res.source);
                valueStore.set(res.source);
                if (res.json) {
                    try {
                        jsonStore.set(JSON.parse(res.json));
                    } catch (err) {
                        //
                    }
                }
                editorMode.set(res.language);

                isInitialLoading.set(false);
            }).catch(() => {
                isLoadError.set(true);
            });
    }
}

const storeVal = debounce((val: string) => {
    const sessionInfo = get(session);

    fetch(`//${serverHostPath}api/updateSource`, {
        body: JSON.stringify({
            value: val,
            language: get(editorMode),
            uuid: sessionInfo.uuid,
            writeKey: sessionInfo.writeKey
        }),
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(() => {
        savedStore.set(true);
    });
}, 500);

valueStore.subscribe(val => {
    const sessionInfo = get(session);
    const initialValue = get(initialValueStore);

    // if (get(viewModeStore) === 'preview') {
    //     return;
    // }

    savedStore.set(val === initialValue);

    if (sessionInfo.uuid && sessionInfo.writeKey) {
        storeVal(val);
    }
});

savedStore.subscribe(saved => {
    if (saved) {
        window.removeEventListener('beforeunload', unsavedPrompt, {capture: true});
    } else {
        window.addEventListener('beforeunload', unsavedPrompt, {capture: true});
    }
});

function unsavedPrompt(event: BeforeUnloadEvent) {
    event.preventDefault();
    return event.returnValue = 'Changes is not saved, confirm to close?';
}

async function genLinks(uuid: string) {
    const isEditor = get(isDesign);

    return {
        linkToEdit: `${location.protocol}//${clientHostPath}?uuid=${uuid}${isEditor ? '&design=1' : ''}`,
        linkToPreview: `${location.protocol}//${clientHostPath}?uuid=${uuid}&mode=preview`,
        linkToJSON: `${location.protocol}//${serverHostPath}api/json?uuid=${uuid}`
    };
}

export class CustomError extends Error {
    constructor(message?: string) {
        super(message);
    }
}

export async function save() {
    const curSession = get(session);
    if (curSession.uuid && curSession.writeKey) {
        return Promise.resolve({
            ...(await genLinks(curSession.uuid)),
            uuid: curSession.uuid
        });
    } else {
        return fetch(`//${serverHostPath}api/share`, {
            body: JSON.stringify({
                value: get(valueStore),
                json: JSON.stringify(get(jsonStore)),
                language: get(editorMode)
            }),
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(
            res => {
                if (!res.ok) {
                    return res.json().then(json => {
                        if (json && json.error) {
                            throw new CustomError(json.error);
                        } else {
                            throw new Error('Not ok');
                        }
                    });
                }
                return res.json();
            }
        ).then(async res => {
            const uuid = res.uuid;
            const writeKey = res.writeKey;

            session.set({
                uuid,
                writeKey
            });
            savedStore.set(true);

            setLs(`session_write_key_${uuid}`, writeKey);

            const links = await genLinks(uuid);

            history.replaceState(null, document.title, links.linkToEdit);

            return {
                ...links,
                uuid
            };
        });
    }
}

export const initPromise = init();
