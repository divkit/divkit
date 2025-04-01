import type { Overflow } from '../../typings/common';

export interface SwitchElements {
    setCurrentItem(item: number, animated: boolean): void;
    setPreviousItem(step: number, overflow: Overflow, animated: boolean): void;
    setNextItem(step: number, overflow: Overflow, animated: boolean): void;
    scrollToStart?: (animated: boolean) => void;
    scrollToEnd?: (animated: boolean) => void;
    scrollToPosition?: (step: number, animated: boolean) => void;
    scrollCombined?: (opts: {
        step?: number;
        offset?: number;
        overflow?: Overflow;
        animated?: boolean;
    }) => void;
}
