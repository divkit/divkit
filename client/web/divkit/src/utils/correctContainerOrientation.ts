import type { ContainerOrientation } from '../types/container';

export function correctContainerOrientation(
    orientation: string | undefined,
    defaultVal: ContainerOrientation
): ContainerOrientation {
    if (
        orientation === 'vertical' ||
        orientation === 'horizontal' ||
        orientation === 'overlap'
    ) {
        return orientation;
    }

    return defaultVal;
}
