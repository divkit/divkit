import type { DivBaseData } from './base';
import type { DivActionableData } from './actionable';
import type { AlignmentHorizontal, AlignmentVertical } from './alignment';

export type ContainerOrientation = 'vertical' | 'horizontal' | 'overlap';

export interface DivContainerData extends DivBaseData, DivActionableData {
    type: 'container';
    content_alignment_horizontal?: AlignmentHorizontal;
    content_alignment_vertical?: AlignmentVertical;
    orientation?: ContainerOrientation;
    items: DivBaseData[];
    // auto_animations_enabled
}
