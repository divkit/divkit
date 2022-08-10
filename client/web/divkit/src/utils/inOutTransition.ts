import { linear, cubicIn, cubicOut, cubicInOut } from 'svelte/easing';
import { AnyTransition, Interpolation } from '../types/base';
import { ease } from './easings/ease';
import { spring } from './easings/spring';

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
const DEFAULT_ALPHA = 0;
const DEFAULT_SCALE = 0;

export function calcMaxDuration(transitions: AnyTransition[]): number {
    return Math.max(...transitions.map(
        it =>
            (Number(it.duration) || DEFAULT_DURATION) +
            (Number(it.start_delay) || DEFAULT_DELAY)
    ));
}

export interface TransitionProps {
    transitions: AnyTransition[];
    elementBbox: DOMRect;
    rootBbox: DOMRect;
    direction: 'in' | 'out';
    maxDuration: number;
    alpha?: number;
}

export function inOutTransition(node: HTMLElement, {
    transitions,
    elementBbox,
    rootBbox,
    direction,
    maxDuration,
    alpha
}: TransitionProps) {
    const startAlpha = alpha ?? 1;

    return {
        duration: maxDuration,
        css: (t: number) => {
            const tMs = t * maxDuration;

            const parts: {
                active?: boolean;
                opacity?: number;
                translate?: string;
                scale?: string;
            }[] = transitions.map(it => {
                const delay = Number(it.start_delay) || DEFAULT_DELAY;
                const duration = Number(it.duration) || DEFAULT_DURATION;
                const relative = Math.max(0, Math.min(1, (tMs - delay) / duration));
                const oriented = direction === 'in' ? 1 - relative : relative;

                const easing = EASING[it.interpolator || 'ease_in_out'] || cubicInOut;
                const eased = easing(oriented);

                if (it.type === 'fade') {
                    if (eased >= 1) {
                        return {
                            active: false,
                            opacity: 0
                        };
                    }
                    return {
                        active: eased > 0 && eased < 1,
                        opacity: (1 - eased) * startAlpha + eased * (it.alpha || DEFAULT_ALPHA)
                    };
                } else if (it.type === 'slide') {
                    const multiply = (it.edge === 'top' || it.edge === 'left') ? -1 : 1;
                    const prop = (it.edge === 'top' || it.edge === 'bottom' || !it.edge) ? 'translateY' : 'translateX';
                    let distance = it.distance?.value;

                    if (distance === undefined) {
                        if (it.edge === 'top' || it.edge === 'bottom' || !it.edge) {
                            distance = Math.abs(
                                rootBbox[it.edge === 'bottom' ? 'bottom' : 'top'] -
                                elementBbox[it.edge === 'bottom' ? 'top' : 'bottom']
                            );
                        } else {
                            distance = Math.abs(
                                rootBbox[it.edge === 'left' ? 'left' : 'right'] -
                                elementBbox[it.edge === 'left' ? 'right' : 'left']
                            );
                        }
                    }

                    const travel = distance * eased;

                    return {
                        active: eased > 0 && eased < 1,
                        translate: `${prop}(${travel * multiply}px)`
                    };
                } else if (it.type === 'scale') {
                    const scale = (1 - eased) + eased * (it.scale || DEFAULT_SCALE);
                    const pivotX = it.pivot_x ?? .5;
                    const pivotY = it.pivot_y ?? .5;
                    const offsetX = (1 - scale) * elementBbox.width * pivotX;
                    const offsetY = (1 - scale) * elementBbox.height * pivotY;

                    return {
                        active: eased > 0 && eased < 1,
                        scale: `translate(${offsetX}px, ${offsetY}px) scale(${scale})`
                    };
                }

                return {};
            });

            const opacity = (parts
                .map(it => it.opacity)
                .filter(it => it !== undefined) as number[])
                .reduce((acc: number, item: number) => acc * item, 1);

            const translate = parts
                .map(it => it.translate)
                .filter(it => it !== undefined)
                .join(' ');

            const anyScale = parts
                .map(it => it.scale)
                .filter(it => it !== undefined)
                .join(' ');

            const activeScale = parts
                .filter(it => it.active)
                .map(it => it.scale)
                .filter(it => it !== undefined);

            const scale = activeScale.length ? activeScale[activeScale.length - 1] : anyScale;

            const transform = [translate, scale].filter(Boolean).join(' ');

            return `transform:${transform || 'none'};opacity:${opacity}`;
        }
    };
}
