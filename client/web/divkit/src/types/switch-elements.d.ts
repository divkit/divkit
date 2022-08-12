export type Overflow = 'clamp' | 'ring';

export interface SwitchElements {
    setCurrentItem(item: number): void;
    setPreviousItem(overflow: Overflow): void;
    setNextItem(overflow: Overflow): void;
}
