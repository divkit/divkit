const METRIKA_ID = 90054154;
let promise: Promise<Metrika> | undefined;

interface Metrika {
    reachGoal(goal: string): void;
    params(params: Record<string, unknown>): void;
}

declare global {
    // eslint-disable-next-line @typescript-eslint/no-namespace
    namespace Ya {
        const Metrika: {
            new(params: Record<string, unknown>): Metrika;
        };
    }
}

export function initCounter(): Promise<Metrika> {
    if (promise) {
        return promise;
    }

    promise = new Promise(resolve => {
        const script = document.createElement('script');
        script.onload = () => {
            try {
                resolve(new window.Ya.Metrika({
                    id: METRIKA_ID,
                    accurateTrackBounce: 10000,
                    trackLinks: true
                }));
            } catch (_err) {
                // do nothing
            }
        };
        script.src = 'https://mc.yandex.ru/metrika/watch.js';
        document.head.appendChild(script);
    });

    return promise;
}
