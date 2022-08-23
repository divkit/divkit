import { serverHostPath, isProd } from '../utils/const';

// todo reconnect

type Listener = (msg: any) => void;
const listeners: Listener[] = [];

export const wsPromise = new Promise<WebSocket>(resolve => {
    const ws = new WebSocket((isProd ? 'wss://' : 'ws://') + serverHostPath);
    let connected = false;

    ws.onopen = () => {
        connected = true;
        resolve(ws);
    };

    ws.onmessage = msg => {
        let json: any;
        try {
            json = JSON.parse(msg.data);
        } catch (err) {
            return;
        }

        listeners.forEach(listener => listener(json));
    };
});

export function addListener(listener: Listener): void {
    listeners.push(listener);
}
