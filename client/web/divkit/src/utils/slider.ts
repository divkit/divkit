const MAX_TICKS = 1000;

function round(x: number, align: number, start: number): number {
    return start + Math.ceil((x - start) / align) * align;
}

export function fillTicks(from: number, to: number, minValue: number, maxValue: number, inside: boolean): number[] {
    const res: number[] = [];

    const scale = maxValue - minValue < MAX_TICKS ? 1 : Math.ceil((maxValue - minValue) / MAX_TICKS);
    const startOffset = round(minValue, scale, minValue) - minValue;

    if (inside) {
        for (let i = round(from, scale, minValue) - startOffset; i < to + scale; i += scale) {
            res.push((Math.min(i, to) - minValue) / (maxValue - minValue));
        }
    } else {
        for (let i = minValue; i < from; i += scale) {
            res.push((i - minValue) / (maxValue - minValue));
        }
        for (let i = round(to + 1, scale, minValue); i < maxValue + scale; i += scale) {
            res.push((Math.min(i, maxValue) - minValue) / (maxValue - minValue));
        }
    }

    return res;
}
