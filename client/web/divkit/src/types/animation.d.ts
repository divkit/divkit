import type { Interpolation } from './base';

export type AnimationType = 'fade' | 'scale' | 'native' | 'no_animation' | 'translate';

export interface AnyAnimation {
    name: AnimationType;
    duration?: number;
    start_delay?: number;
    start_value?: number;
    end_value?: number;
    interpolator?: Interpolation;
}

export interface AnimationSet {
    name: 'set';
    items: Animation[];
}

export type Animation = AnyAnimation | AnimationSet;
