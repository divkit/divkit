export function clamp(val: number | bigint, min: number, max: number): number {
    return Math.max(min, Math.min(max, Number(val)));
}
