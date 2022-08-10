import { pxToEm } from './pxToEm';

/**
 * Filter weights, that would result in < 1fr
 * @param weight
 */
function isSpannedWeight(weight: number): boolean {
    return weight > 0 && weight < 1;
}

function formatWeight(weight: number): string {
    return String(Math.ceil(weight * 1000) / 1000);
}

export function gridCalcTemplates(weights: number[], minSizes: number[], length: number): string {
    // If result weight is lesser than 1, multiply all weights so all of them would exceed 1
    if (weights.some(isSpannedWeight)) {
        const multiply = Math.max(...weights.filter(isSpannedWeight).map(weight => 1 / weight));
        weights = weights.map(weight => weight * multiply);
    }

    const allTracksHasWeight = weights.every(Boolean);
    let minSize = 0;
    let totalWeight = 0;
    const template: string[] = [];

    if (allTracksHasWeight) {
        totalWeight = weights.reduce((acc, item) => {
            return acc + item;
        }, 0);

        for (let i = 0; i < length; ++i) {
            if (!minSizes[i]) {
                continue;
            }

            const minTrackSize = (minSizes[i] / weights[i]) * totalWeight;

            if (minTrackSize > minSize) {
                minSize = minTrackSize;
            }
        }
    }

    for (let i = 0; i < length; ++i) {
        if (minSize) {
            template[i] =
                `minmax(${pxToEm((minSize * weights[i]) / totalWeight)},${formatWeight(weights[i])}fr)`;
        } else if (weights[i]) {
            template[i] = `${formatWeight(weights[i])}fr`;
        } else {
            template[i] = 'auto';
        }
    }

    return template.join(' ');
}
