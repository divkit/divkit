import type { VideoScale } from '../../typings/common';

export function videoSize(scale?: VideoScale): string {
    if (scale === 'fill') {
        return 'cover';
    /* } else if (scale === 'stretch') {
        return 'fill'; */
    } else if (scale === 'no_scale') {
        return 'none';
    }

    // 'fit' and default
    return 'contain';
}
