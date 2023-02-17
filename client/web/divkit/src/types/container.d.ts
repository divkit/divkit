import type { DivBaseData } from './base';
import type { DivActionableData } from './actionable';
import type { AlignmentHorizontal, AlignmentVertical } from './alignment';
import type { BooleanInt } from '../../typings/common';
import type { Drawable } from './drawable';
import type { DivAspect } from './image';

export type ContainerOrientation = 'vertical' | 'horizontal' | 'overlap';

export type ContainerLayoutMode = 'no_wrap' | 'wrap';

export interface ContainerSeparator {
    show_at_start?: BooleanInt;
    show_at_end?: BooleanInt;
    show_between?: BooleanInt;
    style: Drawable;
}

export interface DivContainerData extends DivBaseData, DivActionableData {
    type: 'container';
    content_alignment_horizontal?: AlignmentHorizontal;
    content_alignment_vertical?: AlignmentVertical;
    orientation?: ContainerOrientation;
    items: DivBaseData[];
    // auto_animations_enabled
    layout_mode?: ContainerLayoutMode;
    separator?: ContainerSeparator;
    line_separator?: ContainerSeparator;
    aspect?: DivAspect;
}
