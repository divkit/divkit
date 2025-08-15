import type { TintMode } from '../types/image';

export function correctTintMode(tintMode: TintMode | undefined, defaultVal: TintMode): TintMode {
    if (
        tintMode === 'source_in' ||
        tintMode === 'source_atop' ||
        tintMode === 'darken' ||
        tintMode === 'lighten' ||
        tintMode === 'multiply' ||
        tintMode === 'screen'
    ) {
        return tintMode;
    }

    return defaultVal;
}
