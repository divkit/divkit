import { Orientation } from './orientation';

export type Align = 'start' | 'center' | 'end';
export type ScrollSnap = 'start' | 'center' | 'end';

export interface LayoutParams {
    parentHAlign?: Align;
    parentVAlign?: Align;
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
}
