export type Overflow = 'clamp' | 'ring';

export interface SwitchElements {
    setCurrentItem(item: number): void;
    setPreviousItem(step: number, overflow: Overflow): void;
    setNextItem(step: number, overflow: Overflow): void;
    scrollToStart?: () => void;
    scrollToEnd?: () => void;
    scrollBackward?: (step: number, overflow: Overflow) => void;
    scrollForward?: (step: number, overflow: Overflow) => void;
    scrollToPosition?: (step: number) => void;
}
