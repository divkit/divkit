import type { Interpolation } from '../../typings/common';

export function correctCSSInterpolator(
    interpolator: string | undefined,
    defaultVal: Interpolation
): Interpolation {
    if (
        interpolator === 'linear' ||
        interpolator === 'ease' ||
        interpolator === 'ease_in_out' ||
        interpolator === 'ease_in' ||
        interpolator === 'ease_out'
    ) {
        return interpolator;
    }

    return defaultVal;
}
