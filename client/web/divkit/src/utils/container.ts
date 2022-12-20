import type { BooleanInt } from '../../typings/common';
import type { DrawableStyle } from './correctDrawableStyles';
import type { EdgeInsets } from '../types/edgeInserts';
import type { ContainerOrientation } from '../types/container';
import type { MaybeMissing } from '../expressions/json';
import type { Size } from '../types/sizes';
import { pxToEm } from './pxToEm';

export interface SeparatorStyle {
    show_at_start?: BooleanInt;
    show_at_end?: BooleanInt;
    show_between?: BooleanInt;
    style: DrawableStyle;
}

function setAdditionalPadding(
    insets: EdgeInsets,
    separator: SeparatorStyle | null,
    crossAxis: boolean
): void {
    if (separator?.show_at_start) {
        if (crossAxis) {
            insets.top = separator.style.height;
        } else {
            insets.left = separator.style.width;
        }
    }

    if (separator?.show_at_end) {
        if (crossAxis) {
            insets.bottom = separator.style.height;
        } else {
            insets.right = separator.style.width;
        }
    }
}

export function calcAdditionalPaddings(
    orientation: ContainerOrientation,
    separator: SeparatorStyle | null,
    lineSeparator: SeparatorStyle | null
) {
    const res: EdgeInsets = {};

    setAdditionalPadding(res, separator, orientation === 'vertical');
    setAdditionalPadding(res, lineSeparator, orientation === 'horizontal');

    return res;
}

export function calcItemsGap(
    orientation: ContainerOrientation,
    separator: SeparatorStyle | null,
    lineSeparator: SeparatorStyle | null
): string {
    let vals: number[];

    if (orientation === 'horizontal') {
        vals = [
            lineSeparator?.show_between ? lineSeparator.style.height : 0,
            separator?.show_between ? separator.style.width : 0
        ];
    } else {
        vals = [
            separator?.show_between ? separator.style.height : 0,
            lineSeparator?.show_between ? lineSeparator.style.width : 0
        ];
    }

    return vals.map(pxToEm).join(' ');
}

export interface ContainerChildInfo {
    width: MaybeMissing<Size> | undefined;
    height: MaybeMissing<Size> | undefined;
}

export function hasKnownWidthCheck(item: ContainerChildInfo) {
    const type = item.width?.type;

    return type === 'wrap_content' || type === 'fixed';
}

export function hasKnownHeightCheck(item: ContainerChildInfo) {
    const type = item.height?.type;

    return type !== 'match_parent';
}
