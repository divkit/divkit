import type { AnimatorDirection, TypedValue } from '../../typings/common';
import type { MaybeMissing } from '../expressions/json';
import type { Animator } from '../types/base';
import type { Variable, VariableType } from '../../typings/variables';
import type { ExecAnyActionsFunc } from '../context/root';
import { stringifyColor } from '../expressions/utils';
import { clamp } from './clamp';
import { parseColor } from './correctColor';
import { correctNonNegativeNumber } from './correctNonNegativeNumber';
import { correctPositiveNumber } from './correctPositiveNumber';
import { getEasing } from './easing';
import { lerp } from './lerp';

function correctDirection(direction: string | undefined): AnimatorDirection | undefined {
    if (direction === 'normal' || direction === 'reverse' || direction === 'alternate' || direction === 'alternate_reverse') {
        return direction;
    }
}

export type CalcedAnimator = MaybeMissing<Animator & {
    start_value_typed: TypedValue;
    end_value_typed: TypedValue;
}>;

export interface AnimatorInstance {
    stop(): void;
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function createAnimator(
    animator: CalcedAnimator,
    variableInstance: Variable<any, VariableType>,
    onEnd: () => void,
    execAnyActions: ExecAnyActionsFunc
): AnimatorInstance | undefined {
    const duration = correctPositiveNumber(animator.duration, 0);

    if (!duration || animator.type !== 'color_animator' && animator.type !== 'number_animator') {
        return;
    }

    const startValue = (animator.start_value_typed ? animator.start_value_typed.value : animator.start_value) ??
        variableInstance.getValue();
    const endValue = animator.end_value_typed ? animator.end_value_typed.value : animator.end_value;

    if (startValue === undefined || endValue === undefined) {
        return;
    }

    if (
        animator.type === 'color_animator' && (typeof startValue !== 'string' && startValue !== undefined || typeof endValue !== 'string') ||
        animator.type === 'number_animator' && (typeof startValue !== 'number' && startValue !== undefined || typeof endValue !== 'number')
    ) {
        return;
    }

    const parsedStart = animator.type === 'color_animator' && parseColor(startValue as string);
    const parsedEnd = animator.type === 'color_animator' && parseColor(endValue as string);
    if (animator.type === 'color_animator' && (!parsedStart || !parsedEnd)) {
        return;
    }

    const delay = correctNonNegativeNumber(animator.start_delay, 0);
    const easing = getEasing(animator.interpolator || 'linear');
    const direction = correctDirection(animator.direction) || 'normal';
    // eslint-disable-next-line no-nested-ternary
    const repeatCount = animator.repeat_count?.type === 'infinity' ?
        Infinity :
        (animator.repeat_count?.type === 'fixed' ? correctNonNegativeNumber(animator.repeat_count?.value, 1) : 1);

    let animationTime = 0;
    let localTime = performance.now();
    const maxTime = repeatCount === Infinity ? Infinity : repeatCount * duration + delay;

    function mix(t: number): string | number {
        if (animator.type === 'color_animator') {
            if (!parsedStart || !parsedEnd) {
                throw new Error('Missing start/end value');
            }
            return stringifyColor({
                a: clamp(lerp(parsedStart.a, parsedEnd.a, t), 0, 255),
                r: clamp(lerp(parsedStart.r, parsedEnd.r, t), 0, 255),
                g: clamp(lerp(parsedStart.g, parsedEnd.g, t), 0, 255),
                b: clamp(lerp(parsedStart.b, parsedEnd.b, t), 0, 255)
            });
        }
        return lerp(startValue as number, endValue as number, t);
    }

    function tick(now: number) {
        const diff = now - localTime;
        localTime = now;

        animationTime += diff;

        if (animationTime >= delay) {
            let iterationNumber = Math.floor((animationTime - delay) / duration);

            let t = (animationTime - delay - iterationNumber * duration) / duration;

            if (iterationNumber >= repeatCount) {
                iterationNumber = repeatCount - 1;
                t = 1;
            }

            let dir: 'normal' | 'reverse';
            if (
                direction === 'normal' ||
                (direction === 'alternate' && iterationNumber % 2 === 0) ||
                direction === 'alternate_reverse' && iterationNumber % 2 === 1
            ) {
                dir = 'normal';
            } else {
                dir = 'reverse';
            }

            if (dir === 'reverse') {
                t = 1 - t;
            }

            const value = mix(easing(t));
            variableInstance.setValue(value);
        }

        if (animationTime < maxTime) {
            timer = requestAnimationFrame(tick);
        } else {
            onEnd();
            execAnyActions(animator.end_actions);
        }
    }

    let timer = requestAnimationFrame(tick);

    return {
        stop() {
            cancelAnimationFrame(timer);
            execAnyActions(animator.cancel_actions);
            execAnyActions(animator.end_actions);
        }
    };
}
