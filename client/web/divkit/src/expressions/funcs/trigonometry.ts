import type { EvalContext, EvalValue, NumberValue } from '../eval';
import { registerFunc } from './funcs';
import { NUMBER } from '../const';

function pi(): EvalValue {
    return {
        type: NUMBER,
        value: Math.PI
    };
}

function toRadians(_ctx: EvalContext, degrees: NumberValue): EvalValue {
    return {
        type: NUMBER,
        value: degrees.value / 180 * Math.PI
    };
}

function toDegrees(_ctx: EvalContext, radians: NumberValue): EvalValue {
    return {
        type: NUMBER,
        value: radians.value / Math.PI * 180
    };
}

function sin(_ctx: EvalContext, rad: NumberValue): EvalValue {
    return {
        type: NUMBER,
        value: Math.sin(rad.value)
    };
}

function cos(_ctx: EvalContext, rad: NumberValue): EvalValue {
    return {
        type: NUMBER,
        value: Math.cos(rad.value)
    };
}

function atan(_ctx: EvalContext, rad: NumberValue): EvalValue {
    return {
        type: NUMBER,
        value: Math.atan(rad.value)
    };
}

export function registerTrigonometry(): void {
    registerFunc('pi', [], pi);
    registerFunc('toRadians', [NUMBER], toRadians);
    registerFunc('toDegrees', [NUMBER], toDegrees);
    registerFunc('sin', [NUMBER], sin);
    registerFunc('cos', [NUMBER], cos);
    registerFunc('atan', [NUMBER], atan);
}
