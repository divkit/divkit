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
    margins: PreparedMargins;
}

export interface PreparedMargins {
    top: number;
    right: number;
    bottom: number;
    left: number;
}

export function prepareMargins(margins?: EdgeInsets): PreparedMargins {
    return {
        top: Number(margins?.top) || 0,
        right: Number(margins?.right) || 0,
        bottom: Number(margins?.bottom) || 0,
        left: Number(margins?.left) || 0
    };
}

function setAdditionalPadding(
    insets: EdgeInsets,
    separator: SeparatorStyle | null,
    crossAxis: boolean
): void {
    const hMargin = (separator?.margins.left || 0) + (separator?.margins.right || 0);
    const vMargin = (separator?.margins.top || 0) + (separator?.margins.bottom || 0);

    if (separator?.show_at_start) {
        if (crossAxis) {
            insets.top = separator.style.height + vMargin;
        } else {
            insets.left = separator.style.width + hMargin;
        }
    }

    if (separator?.show_at_end) {
        if (crossAxis) {
            insets.bottom = separator.style.height + vMargin;
        } else {
            insets.right = separator.style.width + hMargin;
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
    const separatorHMargins = (separator?.margins.left || 0) + (separator?.margins.right || 0);
    const separatorVMargins = (separator?.margins.top || 0) + (separator?.margins.bottom || 0);
    const lineSeparatorHMargins = (lineSeparator?.margins.left || 0) + (lineSeparator?.margins.right || 0);
    const lineSeparatorVMargins = (lineSeparator?.margins.top || 0) + (lineSeparator?.margins.bottom || 0);

    if (orientation === 'horizontal') {
        vals = [
            lineSeparator?.show_between ? (lineSeparator.style.height + lineSeparatorVMargins) : 0,
            separator?.show_between ? (separator.style.width + separatorHMargins) : 0
        ];
    } else {
        vals = [
            separator?.show_between ? (separator.style.height + separatorVMargins) : 0,
            lineSeparator?.show_between ? (lineSeparator.style.width + lineSeparatorHMargins) : 0
        ];
    }

    return vals.map(pxToEm).join(' ');
}

export interface ContainerChildInfo {
    width: MaybeMissing<Size> | undefined;
    height: MaybeMissing<Size> | undefined;
}

export function isWidthMatchParent(item: ContainerChildInfo) {
    const type = item.width?.type;

    return type !== 'wrap_content' && type !== 'fixed';
}

export function isHeightMatchParent(item: ContainerChildInfo) {
    const type = item.height?.type;

    return type === 'match_parent';
}
