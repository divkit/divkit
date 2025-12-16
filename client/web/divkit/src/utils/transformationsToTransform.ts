import type { MaybeMissing } from '../expressions/json';
import type { PivotValue, Transformation, Translation } from '../types/base';
import { pxToEm } from './pxToEm';

function translationToCss(
    translation: MaybeMissing<Translation> | undefined,
    multiplier = 1
): string | undefined {
    if (!translation || typeof translation.value !== 'number') {
        return;
    }

    if (translation.type === 'translation-fixed'
    ) {
        return pxToEm(translation.value * multiplier);
    } else if (translation.type === 'translation-percentage'
    ) {
        return `${translation.value * multiplier}%`;
    }
}

function pivotToCss(
    pivot: MaybeMissing<PivotValue> | undefined,
    multiplier = 1
): string | undefined {
    if (!pivot || typeof pivot.value !== 'number') {
        return;
    }

    if (pivot.type === 'pivot-fixed') {
        return pxToEm(pivot.value * multiplier);
    } else if (pivot.type === 'pivot-percentage') {
        return `${pivot.value * multiplier}%`;
    }
}

export function transformationsToTransform(transformations: MaybeMissing<Transformation[]>): string {
    return transformations.map(it => {
        if (it.type === 'rotation') {
            if (typeof it.angle === 'number') {
                const pivotX = pivotToCss(it.pivot_x) || '50%';
                const pivotY = pivotToCss(it.pivot_y) || '50%';
                const reversePivotX = pivotToCss(it.pivot_x, -1) || '-50%';
                const reversePivotY = pivotToCss(it.pivot_y, -1) || '-50%';

                return `translate(${pivotX}, ${pivotY}) rotate(${it.angle}deg) translate(${reversePivotX}, ${reversePivotY})`;
            }
        } else if (it.type === 'translation') {
            const x = translationToCss(it.x) || 0;
            const y = translationToCss(it.y) || 0;

            return `translate(${x}, ${y})`;
        }
    }).filter(Boolean).join(' ');
}
