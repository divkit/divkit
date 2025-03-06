export type Overflow = 'clamp' | 'ring';

export interface SwitchElements {
    setCurrentItem(item: number, animated: boolean): void;
    setPreviousItem(step: number, overflow: Overflow, animated: boolean): void;
    setNextItem(step: number, overflow: Overflow, animated: boolean): void;
    scrollToStart?: (animated: boolean) => void;
    scrollToEnd?: (animated: boolean) => void;
    scrollBackward?: (step: number, overflow: Overflow, animated: boolean) => void;
    scrollForward?: (step: number, overflow: Overflow, animated: boolean) => void;
    scrollToPosition?: (step: number, animated: boolean) => void;
}
