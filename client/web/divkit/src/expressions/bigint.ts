export const hasBigInt = typeof BigInt === 'function';

export function toBigIntNoCheck(val: number | bigint | string): number | bigint {
    if (hasBigInt) {
        return BigInt(val);
    }
    return Number(val);
}

export const MAX_INT = toBigIntNoCheck('9223372036854775807');
export const MIN_INT = toBigIntNoCheck('-9223372036854775808');

export function toBigInt(val: number | bigint | string): number | bigint {
    const res = toBigIntNoCheck(val);
    if (res > MAX_INT || res < MIN_INT) {
        throw new Error('Integer overflow.');
    }
    return res;
}

export const bigIntZero = toBigInt(0);

export function absBigInt(val: number | bigint): number | bigint {
    let res = val;
    if (res < 0) {
        res = -res;
    }
    return res;
}

export function signBigInt(val: number | bigint): number | bigint {
    let res = 0;

    if (val > 0) {
        res = 1;
    } else if (val < 0) {
        res = -1;
    }

    return toBigInt(res);
}
