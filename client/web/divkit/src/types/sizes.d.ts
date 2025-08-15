import type { BooleanInt } from '../../typings/common';

export interface FixedSize {
    type: 'fixed';
    value: number;
}

export interface MatchParentSize {
    type: 'match_parent';
    weight?: number;
}

export interface ConstraintSize {
    value: number;
    // unit
}

export interface WrapContentSize {
    type: 'wrap_content';
    constrained?: BooleanInt;
    min_size?: ConstraintSize;
    max_size?: ConstraintSize;
}

export type Size = FixedSize | MatchParentSize | WrapContentSize;

export interface Dimension {
    value: number;
    // unit
}

export interface PercentageSize {
    type: 'percentage';
    value: number;
}
