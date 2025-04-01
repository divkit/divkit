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

function tan(_ctx: EvalContext, rad: NumberValue): EvalValue {
    return {
        type: NUMBER,
        value: Math.tan(rad.value)
    };
}

function atan(_ctx: EvalContext, rad: NumberValue): EvalValue {
    return {
        type: NUMBER,
        value: Math.atan(rad.value)
    };
}

function asin(_ctx: EvalContext, rad: NumberValue): EvalValue {
    if (rad.value > 1 || rad.value < -1) {
        throw new Error('Arcsine is undefined for the given value.');
    }

    return {
        type: NUMBER,
        value: Math.asin(rad.value)
    };
}

function acos(_ctx: EvalContext, rad: NumberValue): EvalValue {
    if (rad.value > 1 || rad.value < -1) {
        throw new Error('Arccosine is undefined for the given value.');
    }

    return {
        type: NUMBER,
        value: Math.acos(rad.value)
    };
}

export function registerTrigonometry(): void {
    registerFunc('pi', [], pi);
    registerFunc('toRadians', [NUMBER], toRadians);
    registerFunc('toDegrees', [NUMBER], toDegrees);
    registerFunc('sin', [NUMBER], sin);
    registerFunc('cos', [NUMBER], cos);
    registerFunc('tan', [NUMBER], tan);
    registerFunc('atan', [NUMBER], atan);
    registerFunc('asin', [NUMBER], asin);
    registerFunc('acos', [NUMBER], acos);
}
