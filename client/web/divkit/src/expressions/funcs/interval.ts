import type { EvalValue, IntegerValue } from '../eval';
import type { VariablesMap } from '../eval';
import { registerFunc } from './funcs';
import { INTEGER } from '../const';

function getDuration(milliseconds: IntegerValue, delimiter: number, whole?: number): EvalValue {
    if (milliseconds.value < 0) {
        throw new Error('Expecting non-negative number of milliseconds.');
    }

    let val = Math.floor(milliseconds.value / delimiter);
    if (whole) {
        val = val % whole;
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

function getIntervalSeconds(_vars: VariablesMap, milliseconds: IntegerValue): EvalValue {
    return getDuration(milliseconds, MS_IN_SECOND, SECONDS_IN_MINUTE);
}

function getIntervalTotalSeconds(_vars: VariablesMap, milliseconds: IntegerValue): EvalValue {
    return getDuration(milliseconds, MS_IN_SECOND);
}

function getIntervalMinutes(_vars: VariablesMap, milliseconds: IntegerValue): EvalValue {
    return getDuration(milliseconds, MS_IN_MINUTE, MINUTES_IN_HOUR);
}

function getIntervalTotalMinutes(_vars: VariablesMap, milliseconds: IntegerValue): EvalValue {
    return getDuration(milliseconds, MS_IN_MINUTE);
}

function getIntervalHours(_vars: VariablesMap, milliseconds: IntegerValue): EvalValue {
    return getDuration(milliseconds, MS_IN_HOUR, HOURS_IN_DAY);
}

function getIntervalTotalHours(_vars: VariablesMap, milliseconds: IntegerValue): EvalValue {
    return getDuration(milliseconds, MS_IN_HOUR);
}

function getIntervalTotalDays(_vars: VariablesMap, milliseconds: IntegerValue): EvalValue {
    return getDuration(milliseconds, MS_IN_DAY);
}

function getIntervalTotalWeeks(_vars: VariablesMap, milliseconds: IntegerValue): EvalValue {
    return getDuration(milliseconds, MS_IN_WEEK);
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
