import type { Tooltip } from './base';
import type { Orientation } from './orientation';

export type Align = 'start' | 'center' | 'end';
export type AlignPlusBaseline = Align | 'baseline';
export type ScrollSnap = 'start' | 'center' | 'end';

export interface LayoutParams {
    parentHAlign?: Align;
    parentVAlign?: AlignPlusBaseline;
    parentContainerOrientation?: Orientation;
    parentContainerWrap?: boolean;
    parentLayoutOrientation?: Orientation;
    parentHorizontalWrapContent?: boolean;
    parentVerticalWrapContent?: boolean;
    overlapParent?: boolean;
    gridArea?: {
        x: number;
        y: number;
        colSpan: number;
        rowSpan: number;
    };
    scrollSnap?: ScrollSnap;
    fakeElement?: boolean;
    tooltips?: {
        internalId: number;
        ownerNode: HTMLElement;
        desc: Tooltip;
        timeoutId: number | null;
    }[];
}
