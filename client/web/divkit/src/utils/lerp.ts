export function lerp(a: number, b: number, t: number): number {
    return a * (1 - t) + b * t;
}
