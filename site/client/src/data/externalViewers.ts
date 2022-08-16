import { get, writable } from 'svelte/store';
/*import { session } from './session';
import { addListener, wsPromise } from './ws';*/

export interface Device {
    /** @deprecated -> os_name */
    os?: string;
    os_name?: string;
    width: number;
    height: number;
    density?: number;
    device: string;
    /** @deprecated -> app_name */
    app?: string;
    app_name?: string;
    /** @deprecated -> app_version */
    version?: string;
    app_version?: string;
    /** @deprecated -> os_version */
    sdk_name?: string;
    os_version?: string;
}

export interface ViewerState {
    screenshot?: string;
    errors?: ViewerError[];
}

export interface ViewerBase {
    id: string;
    connected: boolean;
    device?: Device;
    lastState?: ViewerState;
}

export interface ExternalViewer extends ViewerBase {
    device: Device;
}

export interface WebViewer extends ViewerBase {
    id: 'web';
}

export interface ViewerError {
    message: string;
    stack: string[];
    level?: 'error' | 'warn';
}

let currentViewers: ExternalViewer[] = [];
export const externalViewers = writable(currentViewers);

/*
let prevListen: string | null = null;

function updateListen(ws: WebSocket): void {
    const sessionInfo = get(session);

    if (!sessionInfo || !sessionInfo.uuid || sessionInfo.uuid === prevListen) {
        return;
    }

    prevListen = sessionInfo.uuid;

    ws.send(JSON.stringify({
        type: 'listen_room',
        message: {
            uuid: sessionInfo.uuid
        }
    }));
}

export function listenToDevices(): void {
    wsPromise.then(ws => {
        addListener(json => {
            if (json.message?.uuid && json.message.uuid !== prevListen) {
                return;
            }

            if (json.type === 'disconnect_room') {
                const index = currentViewers.findIndex(it => it.id === json.message.id);

                if (index > -1) {
                    currentViewers.splice(index, 1);
                    externalViewers.set([...currentViewers]);
                }
            } else if (json.type === 'update_state') {
                const item = currentViewers.find(it => it.id === json.message.id);

                if (item) {
                    item.lastState = json.message.lastState;
                } else {
                    currentViewers.push({
                        id: json.message.id,
                        device: json.message.device,
                        lastState: json.message.lastState || null
                    });
                }

                externalViewers.set([...currentViewers]);
            }
        });

        updateListen(ws);

        let prevUuid: string | null = null;
        session.subscribe(val => {
            if (!prevUuid || !val || prevUuid !== val.uuid) {
                prevUuid = val.uuid;
                currentViewers = [];
                externalViewers.set(currentViewers);
                updateListen(ws);
            }
        });
    });
}*/
