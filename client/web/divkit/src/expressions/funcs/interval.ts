import type { EvalContext, EvalValue, IntegerValue } from '../eval';
import { registerFunc } from './funcs';
import { INTEGER } from '../const';
import { toBigInt } from '../bigint';
import { checkIntegerOverflow } from '../utils';

function getDuration(ctx: EvalContext, milliseconds: IntegerValue, delimiter: number, whole?: number): EvalValue {
    if (milliseconds.value < 0) {
        throw new Error('Expecting non-negative number of milliseconds.');
    }

    let val: number | bigint = (toBigInt(milliseconds.value) as bigint) /
        (toBigInt(delimiter) as bigint);

    checkIntegerOverflow(ctx, val);

    if (typeof val === 'number') {
        val = Math.floor(val);
    }

    if (whole) {
        val = (toBigInt(val) as bigint) % (toBigInt(whole) as bigint);
    }

    return {
        type: INTEGER,
        value: val
    };
}

const MS_IN_SECOND = 1000;
const SECONDS_IN_MINUTE = 60;
const MS_IN_MINUTE = 1000 * 60;
const MINUTES_IN_HOUR = 60;
const MS_IN_HOUR = 1000 * 60 * 60;
const HOURS_IN_DAY = 24;
const MS_IN_DAY = 1000 * 60 * 60 * 24;
const MS_IN_WEEK = 1000 * 60 * 60 * 24 * 7;

function getIntervalSeconds(ctx: EvalContext, milliseconds: IntegerValue): EvalValue {
    return getDuration(ctx, milliseconds, MS_IN_SECOND, SECONDS_IN_MINUTE);
}

function getIntervalTotalSeconds(ctx: EvalContext, milliseconds: IntegerValue): EvalValue {
    return getDuration(ctx, milliseconds, MS_IN_SECOND);
}

function getIntervalMinutes(ctx: EvalContext, milliseconds: IntegerValue): EvalValue {
    return getDuration(ctx, milliseconds, MS_IN_MINUTE, MINUTES_IN_HOUR);
}

function getIntervalTotalMinutes(ctx: EvalContext, milliseconds: IntegerValue): EvalValue {
    return getDuration(ctx, milliseconds, MS_IN_MINUTE);
}

function getIntervalHours(ctx: EvalContext, milliseconds: IntegerValue): EvalValue {
    return getDuration(ctx, milliseconds, MS_IN_HOUR, HOURS_IN_DAY);
}

function getIntervalTotalHours(ctx: EvalContext, milliseconds: IntegerValue): EvalValue {
    return getDuration(ctx, milliseconds, MS_IN_HOUR);
}

function getIntervalTotalDays(ctx: EvalContext, milliseconds: IntegerValue): EvalValue {
    return getDuration(ctx, milliseconds, MS_IN_DAY);
}

function getIntervalTotalWeeks(ctx: EvalContext, milliseconds: IntegerValue): EvalValue {
    return getDuration(ctx, milliseconds, MS_IN_WEEK);
}

export function registerInterval(): void {
    registerFunc('getIntervalSeconds', [INTEGER], getIntervalSeconds);
    registerFunc('getIntervalTotalSeconds', [INTEGER], getIntervalTotalSeconds);
    registerFunc('getIntervalMinutes', [INTEGER], getIntervalMinutes);
    registerFunc('getIntervalTotalMinutes', [INTEGER], getIntervalTotalMinutes);
    registerFunc('getIntervalHours', [INTEGER], getIntervalHours);
    registerFunc('getIntervalTotalHours', [INTEGER], getIntervalTotalHours);
    registerFunc('getIntervalTotalDays', [INTEGER], getIntervalTotalDays);
    registerFunc('getIntervalTotalWeeks', [INTEGER], getIntervalTotalWeeks);
}
