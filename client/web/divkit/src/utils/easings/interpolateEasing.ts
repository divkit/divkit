export function interpolateEasing(table: number[]) {
    return (t: number) => {
        if (t <= 0) {
            return 0;
        } else if (t >= 1) {
            return 1;
        }

        const x = t * table.length;
        const i = Math.floor(x);
        const p0 = table[i];
        const p1 = table[i + 1];
        const t2 = x - i;

        return p0 * t2 + p1 * (1 - t2);
    };
}
