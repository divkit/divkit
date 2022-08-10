import type { AlignmentHorizontal, AlignmentVertical } from '../types/alignment';
import { imagePos } from './background';

export function correctImagePosition(obj: {
    content_alignment_horizontal?: AlignmentHorizontal;
    content_alignment_vertical?: AlignmentVertical;
}, defaultVal: string): string {
    const halign = obj.content_alignment_horizontal;
    const valign = obj.content_alignment_vertical;

    if (
        halign && halign !== 'left' && halign !== 'center' && halign !== 'right' ||
        valign && valign !== 'top' && valign !== 'center' && valign !== 'bottom'
    ) {
        return defaultVal;
    }

    return imagePos(obj);
}
