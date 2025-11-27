import type { Orientation } from './orientation';

export type Align = 'start' | 'center' | 'end';
export type AlignPlusBaseline = Align | 'baseline';
export type ScrollSnap = 'start' | 'center' | 'end';

export interface LayoutParams {
    parentHAlign?: Align;
    parentVAlign?: AlignPlusBaseline;
    parentContainerOrientation?: Orientation;
    parentContainerKnownWidth?: boolean;
    parentContainerKnownHeight?: boolean;
    parentContainerWrap?: boolean;
    parentLayoutOrientation?: Orientation;
    parentHorizontalWrapContent?: boolean;
    parentVerticalWrapContent?: boolean;
    stretchWidth?: boolean;
    stretchHeight?: boolean;
    treatMatchParentAs100?: boolean;
    overlapParent?: boolean;
    gridArea?: {
        x: number;
        y: number;
        colSpan: number;
        rowSpan: number;
    };
    scrollSnap?: ScrollSnap;
}
