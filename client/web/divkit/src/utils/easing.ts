import { linear, cubicIn, cubicOut, cubicInOut } from 'svelte/easing';
import type { Interpolation } from '../types/base';
import { ease } from './easings/ease';
import { spring } from './easings/spring';

export const EASING: Record<Interpolation, (t: number) => number> = {
    linear,
    ease,
    ease_in: cubicIn,
    ease_out: cubicOut,
    ease_in_out: cubicInOut,
    spring
};

export function getEasing(name: Interpolation): (t: number) => number {
    return EASING[name];
}
