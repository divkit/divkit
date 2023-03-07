import type { DatetimeValue, EvalValue, IntegerValue } from '../eval';
import type { VariablesMap } from '../eval';
import { registerFunc } from './funcs';
import { DATETIME, INTEGER } from '../const';
import { valToString } from '../utils';

function getMaxDate(date: Date): number {
    const copy = new Date(date);

    copy.setUTCMonth(copy.getUTCMonth() + 1);
    copy.setUTCDate(0);

    return copy.getUTCDate();
}

function parseUnixTime(_vars: VariablesMap, arg: IntegerValue): EvalValue {
    return {
        type: DATETIME,
        value: new Date(arg.value * 1000)
    };
}

function nowLocal(): EvalValue {
    return {
        type: DATETIME,
        value: new Date()
    };
}

function addMillis(_vars: VariablesMap, datetime: DatetimeValue, milliseconds: IntegerValue): EvalValue {
    return {
        type: DATETIME,
        value: new Date(datetime.value.getTime() + milliseconds.value)
    };
}

function setYear(_vars: VariablesMap, datetime: DatetimeValue, year: IntegerValue): EvalValue {
    const copy = new Date(datetime.value);

    copy.setUTCFullYear(year.value);

    return {
        type: DATETIME,
        value: copy
    };
}

function setMonth(_vars: VariablesMap, datetime: DatetimeValue, month: IntegerValue): EvalValue {
    if (month.value < 1 || month.value > 12) {
        throw new Error(`Expecting month in [1..12], instead got ${month.value}.`);
    }

    const copy = new Date(datetime.value);

    copy.setUTCMonth(month.value - 1);

    return {
        type: DATETIME,
        value: copy
    };
}

function setDay(_vars: VariablesMap, datetime: DatetimeValue, day: IntegerValue): EvalValue {
    const copy = new Date(datetime.value);

    if (day.value <= 0 && day.value !== -1 || day.value > getMaxDate(copy)) {
        throw new Error(`Unable to set day ${day.value} for date ${valToString(datetime)}.`);
    }

    copy.setUTCDate(day.value === -1 ? 0 : day.value);

    return {
        type: DATETIME,
        value: copy
    };
}

function setHours(_vars: VariablesMap, datetime: DatetimeValue, hours: IntegerValue): EvalValue {
    if (hours.value < 0 || hours.value > 23) {
        throw new Error(`Expecting hours in [0..23], instead got ${hours.value}.`);
    }

    const copy = new Date(datetime.value);

    copy.setUTCHours(hours.value);

    return {
        type: DATETIME,
        value: copy
    };
}

function setMinutes(_vars: VariablesMap, datetime: DatetimeValue, minutes: IntegerValue): EvalValue {
    if (minutes.value < 0 || minutes.value > 59) {
        throw new Error(`Expecting minutes in [0..59], instead got ${minutes.value}.`);
    }

    const copy = new Date(datetime.value);

    copy.setUTCMinutes(minutes.value);

    return {
        type: DATETIME,
        value: copy
    };
}

function setSeconds(_vars: VariablesMap, datetime: DatetimeValue, seconds: IntegerValue): EvalValue {
    if (seconds.value < 0 || seconds.value > 59) {
        throw new Error(`Expecting seconds in [0..59], instead got ${seconds.value}.`);
    }

    const copy = new Date(datetime.value);

    copy.setUTCSeconds(seconds.value);

    return {
        type: DATETIME,
        value: copy
    };
}

function setMillis(_vars: VariablesMap, datetime: DatetimeValue, millis: IntegerValue): EvalValue {
    if (millis.value < 0 || millis.value > 999) {
        throw new Error(`Expecting millis in [0..999], instead got ${millis.value}.`);
    }

    const copy = new Date(datetime.value);

    copy.setUTCMilliseconds(millis.value);

    return {
        type: DATETIME,
        value: copy
    };
}

const getter = (
    method: 'getUTCFullYear' | 'getUTCMonth' | 'getUTCDate' | 'getUTCDay' | 'getUTCHours' | 'getUTCMinutes' |
        'getUTCSeconds' | 'getUTCMilliseconds'
) => {
    return (_vars: VariablesMap, datetime: DatetimeValue): EvalValue => {
        const copy = new Date(datetime.value.getTime());

        let value: number = copy[method]();

        if (method === 'getUTCMonth') {
            ++value;
        } else if (method === 'getUTCDay' && value === 0) {
            value = 7;
        }

        return {
            type: INTEGER,
            value
        };
    };
};

const getYear = getter('getUTCFullYear');
const getMonth = getter('getUTCMonth');
const getDay = getter('getUTCDate');
const getDayOfWeek = getter('getUTCDay');
const getHours = getter('getUTCHours');
const getMinutes = getter('getUTCMinutes');
const getSeconds = getter('getUTCSeconds');
const getMillis = getter('getUTCMilliseconds');

export function registerDatetime(): void {
    registerFunc('parseUnixTime', [INTEGER], parseUnixTime);
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
}
