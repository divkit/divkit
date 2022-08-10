import type { DatetimeValue, EvalValue, IntegerValue } from '../eval';
import { registerFunc } from './funcs';
import { DATETIME, INTEGER } from '../const';
import { valToString } from '../utils';

function parseUnixTime(arg: IntegerValue): EvalValue {
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

function addMillis(datetime: DatetimeValue, milliseconds: IntegerValue): EvalValue {
    return {
        type: DATETIME,
        value: new Date(datetime.value.getTime() + milliseconds.value)
    };
}

function setYear(datetime: DatetimeValue, year: IntegerValue): EvalValue {
    const copy = new Date(datetime.value);

    copy.setFullYear(year.value);

    return {
        type: DATETIME,
        value: copy
    };
}

function setMonth(datetime: DatetimeValue, month: IntegerValue): EvalValue {
    if (month.value < 1 || month.value > 12) {
        throw new Error(`Expecting month in [1..12], instead got ${month.value}.`);
    }

    const copy = new Date(datetime.value);

    copy.setMonth(month.value - 1);

    return {
        type: DATETIME,
        value: copy
    };
}

function getMaxDate(date: Date): number {
    const copy = new Date(date);

    copy.setMonth(copy.getMonth() + 1);
    copy.setDate(0);

    return copy.getDate();
}

function setDay(datetime: DatetimeValue, day: IntegerValue): EvalValue {
    const copy = new Date(datetime.value);

    if (day.value <= 0 && day.value !== -1 || day.value > getMaxDate(copy)) {
        throw new Error(`Unable to set day ${day.value} for date ${valToString(datetime)}.`);
    }

    copy.setDate(day.value === -1 ? 0 : day.value);

    return {
        type: DATETIME,
        value: copy
    };
}

function setHours(datetime: DatetimeValue, hours: IntegerValue): EvalValue {
    if (hours.value < 0 || hours.value > 23) {
        throw new Error(`Expecting hours in [0..23], instead got ${hours.value}.`);
    }

    const copy = new Date(datetime.value);

    copy.setHours(hours.value - copy.getTimezoneOffset() / 60);

    return {
        type: DATETIME,
        value: copy
    };
}

function setMinutes(datetime: DatetimeValue, minutes: IntegerValue): EvalValue {
    if (minutes.value < 0 || minutes.value > 59) {
        throw new Error(`Expecting minutes in [0..59], instead got ${minutes.value}.`);
    }

    const copy = new Date(datetime.value);

    copy.setMinutes(minutes.value);

    return {
        type: DATETIME,
        value: copy
    };
}

function setSeconds(datetime: DatetimeValue, seconds: IntegerValue): EvalValue {
    if (seconds.value < 0 || seconds.value > 59) {
        throw new Error(`Expecting seconds in [0..59], instead got ${seconds.value}.`);
    }

    const copy = new Date(datetime.value);

    copy.setSeconds(seconds.value);

    return {
        type: DATETIME,
        value: copy
    };
}

function setMillis(datetime: DatetimeValue, millis: IntegerValue): EvalValue {
    if (millis.value < 0 || millis.value > 999) {
        throw new Error(`Expecting millis in [0..999], instead got ${millis.value}.`);
    }

    const copy = new Date(datetime.value);

    copy.setMilliseconds(millis.value);

    return {
        type: DATETIME,
        value: copy
    };
}

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
}
