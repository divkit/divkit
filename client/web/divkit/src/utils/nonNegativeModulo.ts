export function nonNegativeModulo(value: number, mod: number): number {
    let res = value % mod;
    if (res < 0) {
        res += mod;
    }
    return res;
}
