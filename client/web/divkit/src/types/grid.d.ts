import type { DivBaseData } from './base';
import type { DivActionableData } from './actionable';
import type { AlignmentHorizontal, AlignmentVertical } from './alignment';

export interface DivGridData extends DivBaseData, DivActionableData {
    type: 'grid';
    content_alignment_horizontal?: AlignmentHorizontal;
    content_alignment_vertical?: AlignmentVertical;
    items: DivBaseData[];
    column_count: number;
}
