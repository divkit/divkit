export interface Snap {
    val: number;
    type: 'start' | 'center' | 'end';
    special?: string;
}

export function bestSnap(possible: Snap[], current: Snap, maxDistance: number): Snap | undefined {
    let best: Snap | undefined;
    let bestDist: number | undefined;

    possible.forEach(item => {
        if (item.type === current.type) {
            const d = Math.abs(item.val - current.val);
            if (d <= maxDistance && (!bestDist || bestDist > d)) {
                bestDist = d;
                best = item;
            }
        }
    });

    return best;
}
