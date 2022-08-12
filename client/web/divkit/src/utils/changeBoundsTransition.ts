import { linear, cubicIn, cubicOut, cubicInOut } from 'svelte/easing';
import type { ChangeBoundsTransition, Interpolation } from '../types/base';
import { ease } from './easings/ease';
import { spring } from './easings/spring';
import { lerp } from './lerp';

const EASING: Record<Interpolation, (t: number) => number> = {
    linear,
    ease,
    ease_in: cubicIn,
    ease_out: cubicOut,
    ease_in_out: cubicInOut,
    spring
};

const DEFAULT_DURATION = 200;
const DEFAULT_DELAY = 0;

export interface TransitionProps {
    rootBbox: DOMRect;
    beforeBbox: DOMRect;
    afterBbox: DOMRect;
    transition: ChangeBoundsTransition;
}

export function changeBoundsTransition(node: HTMLElement, {
    rootBbox,
    beforeBbox,
    afterBbox,
    transition
}: TransitionProps) {
    return {
        delay: transition.start_delay ?? DEFAULT_DELAY,
        duration: transition.duration ?? DEFAULT_DURATION,
        easing: (transition.interpolator && transition.interpolator in EASING) ?
            EASING[transition.interpolator] :
            cubicInOut,
        css: (t: number) => {
            return [
                `top:${lerp(beforeBbox.top, afterBbox.top, t) - rootBbox.top}px`,
                `left:${lerp(beforeBbox.left, afterBbox.left, t) - rootBbox.left}px`,
                `width:${lerp(beforeBbox.width, afterBbox.width, t)}px`,
                `height:${lerp(beforeBbox.height, afterBbox.height, t)}px`
            ].join(';');
        }
    };
}
