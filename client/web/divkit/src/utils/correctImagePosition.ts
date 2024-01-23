import type { Direction } from '../../typings/common';
import type { AlignmentHorizontal, AlignmentVertical } from '../types/alignment';
import { imagePos } from './background';

export function correctImagePosition(obj: {
    content_alignment_horizontal?: AlignmentHorizontal;
    content_alignment_vertical?: AlignmentVertical;
}, direction: Direction, defaultVal: string): string {
    let halign = obj.content_alignment_horizontal;
    const valign = obj.content_alignment_vertical;

    if (halign === 'start') {
        halign = direction === 'ltr' ? 'left' : 'right';
    } else if (halign === 'end') {
        halign = direction === 'ltr' ? 'right' : 'left';
    }

    if (
        halign && halign !== 'left' && halign !== 'center' && halign !== 'right' ||
        valign && valign !== 'top' && valign !== 'center' && valign !== 'bottom'
    ) {
        return defaultVal;
    }

    return imagePos({
        content_alignment_horizontal: halign,
        content_alignment_vertical: valign
    });
}
