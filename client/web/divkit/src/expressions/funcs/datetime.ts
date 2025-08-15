/* eslint-disable no-nested-ternary */
import type { DatetimeValue, EvalContext, EvalValue, IntegerValue, StringValue } from '../eval';
import { registerFunc } from './funcs';
import { DATETIME, INTEGER, STRING } from '../const';
import { valToString } from '../utils';
import { toBigInt } from '../bigint';
import { formatDate } from '../../utils/formatDate';

function getMaxDate(date: Date): number {
    const copy = new Date(date);

    copy.setUTCMonth(copy.getUTCMonth() + 1);
    copy.setUTCDate(0);

    return copy.getUTCDate();
}

function parseUnixTime(_ctx: EvalContext, arg: IntegerValue): EvalValue {
    return {
        type: DATETIME,
        value: new Date(Number(arg.value) * 1000)
    };
}

function parseUnixTimeAsLocal(_ctx: EvalContext, arg: IntegerValue): EvalValue {
    const date = new Date(Number(arg.value) * 1000);
    const offset = date.getTimezoneOffset();
    date.setMinutes(date.getMinutes() - offset);

    return {
        type: DATETIME,
        value: date
    };
}

function nowLocal(): EvalValue {
    return {
        type: DATETIME,
        value: new Date()
    };
}

function addMillis(_ctx: EvalContext, datetime: DatetimeValue, milliseconds: IntegerValue): EvalValue {
    return {
        type: DATETIME,
        value: new Date(datetime.value.getTime() + Number(milliseconds.value))
    };
}

function setYear(_ctx: EvalContext, datetime: DatetimeValue, year: IntegerValue): EvalValue {
    const copy = new Date(datetime.value);

    copy.setUTCFullYear(Number(year.value));

    return {
        type: DATETIME,
        value: copy
    };
}

function setMonth(_ctx: EvalContext, datetime: DatetimeValue, month: IntegerValue): EvalValue {
    const intVal = Number(month.value);

    if (intVal < 1 || intVal > 12) {
        throw new Error(`Expecting month in [1..12], instead got ${intVal}.`);
    }

    const copy = new Date(datetime.value);
    copy.setUTCMonth(intVal - 1);

    return {
        type: DATETIME,
        value: copy
    };
}

function setDay(_ctx: EvalContext, datetime: DatetimeValue, day: IntegerValue): EvalValue {
    const copy = new Date(datetime.value);
    const intVal = Number(day.value);

    if (intVal <= 0 && intVal !== -1 || intVal > getMaxDate(copy)) {
        throw new Error(`Unable to set day ${intVal} for date ${valToString(datetime, false)}.`);
    }

    copy.setUTCDate(intVal === -1 ? 0 : intVal);

    return {
        type: DATETIME,
        value: copy
    };
}

function setHours(_ctx: EvalContext, datetime: DatetimeValue, hours: IntegerValue): EvalValue {
    const intVal = Number(hours.value);

    if (intVal < 0 || intVal > 23) {
        throw new Error(`Expecting hours in [0..23], instead got ${intVal}.`);
    }

    const copy = new Date(datetime.value);
    copy.setUTCHours(intVal);

    return {
        type: DATETIME,
        value: copy
    };
}

function setMinutes(_ctx: EvalContext, datetime: DatetimeValue, minutes: IntegerValue): EvalValue {
    const intVal = Number(minutes.value);

    if (intVal < 0 || intVal > 59) {
        throw new Error(`Expecting minutes in [0..59], instead got ${intVal}.`);
    }

    const copy = new Date(datetime.value);

    copy.setUTCMinutes(intVal);

    return {
        type: DATETIME,
        value: copy
    };
}

function setSeconds(_ctx: EvalContext, datetime: DatetimeValue, seconds: IntegerValue): EvalValue {
    const intVal = Number(seconds.value);

    if (intVal < 0 || intVal > 59) {
        throw new Error(`Expecting seconds in [0..59], instead got ${intVal}.`);
    }

    const copy = new Date(datetime.value);
    copy.setUTCSeconds(intVal);

    return {
        type: DATETIME,
        value: copy
    };
}

function setMillis(_ctx: EvalContext, datetime: DatetimeValue, millis: IntegerValue): EvalValue {
    const intVal = Number(millis.value);

    if (intVal < 0 || intVal > 999) {
        throw new Error(`Expecting millis in [0..999], instead got ${intVal}.`);
    }

    const copy = new Date(datetime.value);
    copy.setUTCMilliseconds(intVal);

    return {
        type: DATETIME,
        value: copy
    };
}

const getter = (
    method: 'getUTCFullYear' | 'getUTCMonth' | 'getUTCDate' | 'getUTCDay' | 'getUTCHours' | 'getUTCMinutes' |
        'getUTCSeconds' | 'getUTCMilliseconds'
) => {
    return (_ctx: EvalContext, datetime: DatetimeValue): EvalValue => {
        const copy = new Date(datetime.value.getTime());

        let value: number = copy[method]();

        if (method === 'getUTCMonth') {
            ++value;
        } else if (method === 'getUTCDay' && value === 0) {
            value = 7;
        }

        return {
            type: INTEGER,
            value: toBigInt(value)
        };
    };
};

function makeFormat(isUTC: boolean) {
    return (ctx: EvalContext, datetime: DatetimeValue, format: StringValue, locale?: StringValue): EvalValue => {
        return {
            type: STRING,
            value: formatDate(datetime.value, format.value, {
                locale: locale?.value,
                isUTC,
                weekStartDay: ctx.weekStartDay
            })
        };
    };
}

const getYear = getter('getUTCFullYear');
const getMonth = getter('getUTCMonth');
const getDay = getter('getUTCDate');
const getDayOfWeek = getter('getUTCDay');
const getHours = getter('getUTCHours');
const getMinutes = getter('getUTCMinutes');
const getSeconds = getter('getUTCSeconds');
const getMillis = getter('getUTCMilliseconds');

const formatAsLocal = makeFormat(false);
const formatAsUTC = makeFormat(true);

export function registerDatetime(): void {
    registerFunc('parseUnixTime', [INTEGER], parseUnixTime);
    registerFunc('parseUnixTimeAsLocal', [INTEGER], parseUnixTimeAsLocal);
    registerFunc('nowLocal', [], nowLocal);
    registerFunc('addMillis', [DATETIME, INTEGER], addMillis);

    registerFunc('setYear', [DATETIME, INTEGER], setYear);
    registerFunc('setMonth', [DATETIME, INTEGER], setMonth);
    registerFunc('setDay', [DATETIME, INTEGER], setDay);
    registerFunc('setHours', [DATETIME, INTEGER], setHours);
    registerFunc('setMinutes', [DATETIME, INTEGER], setMinutes);
    registerFunc('setSeconds', [DATETIME, INTEGER], setSeconds);
    registerFunc('setMillis', [DATETIME, INTEGER], setMillis);

    registerFunc('getYear', [DATETIME], getYear);
    registerFunc('getMonth', [DATETIME], getMonth);
    registerFunc('getDay', [DATETIME], getDay);
    registerFunc('getDayOfWeek', [DATETIME], getDayOfWeek);
    registerFunc('getHours', [DATETIME], getHours);
    registerFunc('getMinutes', [DATETIME], getMinutes);
    registerFunc('getSeconds', [DATETIME], getSeconds);
    registerFunc('getMillis', [DATETIME], getMillis);

    registerFunc('formatDateAsLocal', [DATETIME, STRING], formatAsLocal);
    registerFunc('formatDateAsUTC', [DATETIME, STRING], formatAsUTC);
    registerFunc('formatDateAsLocalWithLocale', [DATETIME, STRING, STRING], formatAsLocal);
    registerFunc('formatDateAsUTCWithLocale', [DATETIME, STRING, STRING], formatAsUTC);
}
