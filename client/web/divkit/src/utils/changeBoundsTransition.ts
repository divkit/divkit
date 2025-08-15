import { cubicInOut } from 'svelte/easing';
import type { ChangeBoundsTransition } from '../types/base';
import { lerp } from './lerp';
import { isPrefersReducedMotion } from './isPrefersReducedMotion';
import { EASING } from './easing';

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
        duration: isPrefersReducedMotion() ? 0 : (transition.duration ?? DEFAULT_DURATION),
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
