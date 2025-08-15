/* eslint-disable @typescript-eslint/no-explicit-any */

export function simpleThrottle<F extends(...args: any[]) => any>(fn: F, timeout: number): F {
    let runTs = 0;
    let prevRes: unknown;
    let awaits = false;

    return function(this: any) {
        const now = Date.now();

        if (!runTs || Math.abs(now - runTs) > timeout) {
            runTs = now;

            // eslint-disable-next-line prefer-rest-params
            return (prevRes = fn.apply(this, arguments as unknown as any[]));
        }

        if (!awaits) {
            awaits = true;
            setTimeout(() => {
                awaits = false;
                // eslint-disable-next-line prefer-rest-params
                prevRes = fn.apply(this, arguments as unknown as any[]);
            }, timeout);
        }

        return prevRes;
    } as unknown as F;
}
