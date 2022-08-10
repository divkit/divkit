import { Visibility } from '../types/base';

export function correctVisibility(visibility: string | undefined, defaultVal: Visibility): Visibility {
    if (
        visibility === 'visible' ||
        visibility === 'invisible' ||
        visibility === 'gone'
    ) {
        return visibility;
    }

    return defaultVal;
}
