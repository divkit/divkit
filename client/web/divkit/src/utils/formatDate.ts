// Some code based on the https://github.com/date-fns (MIT License)

type FormatGetter = (opts: Intl.DateTimeFormatOptions & {
    date?: Date;
}, field: Intl.DateTimeFormatPartTypes | 'week' | 'weekyear' | 'extendedyear' | 'weekofmonth' | 'dayofyear' | 'dayofweekinmonth' | 'weekdaynumeric' | 'timezoneoffset') => string | undefined;

function formatNumber(num: string | undefined, len: number | undefined): string | undefined {
    if (!num || !len) {
        return num;
    }

    return num.padStart(len, '0');
}

const formatters: Record<string, (token: number, getter: FormatGetter) => string | undefined> = {
    G(token, getter) {
        let era: Intl.DateTimeFormatOptions['era'];
        if (token < 4) {
            era = 'short';
        } else if (token === 5) {
            era = 'narrow';
        } else {
            era = 'long';
        }
        return getter({
            era
        }, 'era');
    },
    d(token, getter) {
        return formatNumber(getter({
            day: 'numeric'
        }, 'day'), token > 1 ? token : 0);
    },
    D(token, getter) {
        return formatNumber(getter({}, 'dayofyear'), token > 1 ? token : 0);
    },
    F(token, getter) {
        return formatNumber(getter({}, 'dayofweekinmonth'), token > 1 ? token : 0);
    },
    M(token, getter) {
        let month: Intl.DateTimeFormatOptions['month'];
        if (token === 1) {
            month = 'numeric';
        } else if (token === 2) {
            month = '2-digit';
        } else if (token === 3) {
            month = 'short';
        } else if (token === 5) {
            month = 'narrow';
        } else {
            month = 'long';
        }
        return getter({
            month,
            // to get a genitive case of month
            day: 'numeric'
        }, 'month');
    },
    y(token, getter) {
        return formatNumber(getter({
            year: token === 2 ? '2-digit' : 'numeric'
        }, 'year'), token > 2 ? token : undefined);
    },
    Y(token, getter) {
        return formatNumber(getter({
            year: token === 2 ? '2-digit' : 'numeric'
        }, 'weekyear'), token > 2 ? token : undefined);
    },
    u(token, getter) {
        return formatNumber(getter({
            year: 'numeric'
        }, 'extendedyear'), token > 1 ? token : undefined);
    },
    E(token, getter) {
        let weekday: Intl.DateTimeFormatOptions['weekday'];
        if (token <= 3) {
            weekday = 'short';
        } else if (token === 5) {
            weekday = 'narrow';
        } else {
            weekday = 'long';
        }
        return getter({
            weekday
        }, 'weekday');
    },
    e(token, getter) {
        if (token > 2) {
            return formatters.E(token, getter);
        }
        return formatNumber(getter({}, 'weekdaynumeric'), token > 1 ? token : undefined);
    },
    w(token, getter) {
        return formatNumber(getter({}, 'week'), token > 1 ? token : undefined);
    },
    W(token, getter) {
        return formatNumber(getter({}, 'weekofmonth'), token > 1 ? token : undefined);
    },
    H(token, getter) {
        const hours = getter({
            hour: 'numeric',
            hour12: false,
            hourCycle: 'h23',
        }, 'hour');

        if (!hours) {
            return;
        }

        // v8 may return '24' with 'h23' style
        const fixed = String(Number(hours) % 24);
        return formatNumber(fixed, token > 1 ? token : undefined);
    },
    h(token, getter) {
        return formatNumber(getter({
            hour: 'numeric',
            hour12: true,
            hourCycle: 'h12',
        }, 'hour'), token > 1 ? token : undefined);
    },
    K(token, getter) {
        const hours = getter({
            hour: 'numeric',
            hour12: true,
            hourCycle: 'h11',
        }, 'hour');

        if (!hours) {
            return;
        }

        const fixed = String(Number(hours) % 12);
        return formatNumber(fixed, token > 1 ? token : undefined);
    },
    k(token, getter) {
        return formatNumber(getter({
            hour: 'numeric',
            hour12: false,
            hourCycle: 'h24',
        }, 'hour'), token > 2 ? token : undefined);
    },
    a(_token, getter) {
        return getter({
            hour: 'numeric',
            hour12: true,
            hourCycle: 'h11',
            dayPeriod: undefined
        }, 'dayPeriod');
    },
    m(token, getter) {
        return formatNumber(getter({
            minute: 'numeric'
        }, 'minute'), token > 1 ? token : undefined);
    },
    s(token, getter) {
        return formatNumber(getter({
            second: 'numeric'
        }, 'second'), token > 1 ? token : undefined);
    },
    S(token, getter) {
        const res = getter({
            fractionalSecondDigits: Math.min(3, token) as 1 | 2 | 3
        }, 'fractionalSecond');

        if (res && token > 3) {
            return res.padEnd(token, '0');
        }
        return res;
    },
    z(token, getter) {
        return getter({
            timeZoneName: token === 4 ? 'long' : 'short'
        }, 'timeZoneName');
    },
    Z(_token, getter) {
        const offset = -Number(getter({}, 'timezoneoffset'));
        const timeOffset = Math.abs(offset / 60);
        const fullOffset = Math.floor(timeOffset) * 100 + (timeOffset - Math.floor(timeOffset)) * 60;

        return (offset >= 0 ? '+' : '-') + formatNumber(String(fullOffset), 4);
    },
};

const formattingTokensRegExp =
    /(\w)\1*|''|'(''|[^'])+('|$)|./g;

const escapedStringRegExp = /^'([^]*?)'?$/;
const doubleQuoteRegExp = /''/g;
const unescapedLatinCharacterRegExp = /[a-zA-Z]/;

const msInDay = 1000 * 60 * 60 * 24;

function cleanEscapedString(input: string): string {
    const matched = input.match(escapedStringRegExp);

    if (!matched) {
        return input;
    }

    return matched[1].replace(doubleQuoteRegExp, "'");
}

function getWeekFirstDay(date: Date, isUTC: boolean, weekStartDay: number): Date {
    const weekDay = date[isUTC ? 'getUTCDay' : 'getDay']();

    // Monday logic
    const offset = (weekDay < weekStartDay) ?
        weekStartDay - weekDay - 7 :
        weekStartDay - weekDay;

    return new Date(date.getTime() + msInDay * offset);
}

function getFirstWeekFirstDay(date: Date, isUTC: boolean, weekStartDay: number): Date {
    const firstDay = new Date(date);
    firstDay[isUTC ? 'setUTCDate' : 'setDate'](1);
    firstDay[isUTC ? 'setUTCMonth' : 'setMonth'](0);

    return getWeekFirstDay(firstDay, isUTC, weekStartDay);
}

function dayDiff(date0: Date, date1: Date): number {
    return Math.round((date0.getTime() - date1.getTime()) / msInDay);
}

function getIOSWeekYear(date: Date, isUTC: boolean, weekStartDay: number): {
    week: number;
    year: number;
} {
    let week = 0;

    const firstWeekFirstDay = getFirstWeekFirstDay(date, isUTC || false, weekStartDay);
    const nextYearDate = new Date(date);
    nextYearDate[isUTC ? 'setUTCFullYear' : 'setFullYear'](date[isUTC ? 'getUTCFullYear' : 'getFullYear']() + 1);
    const nextYearFirstWeekFirstDay = getFirstWeekFirstDay(nextYearDate, isUTC || false, weekStartDay);

    const isBeforeFirstWeek = date.getTime() < firstWeekFirstDay.getTime();
    const isAfterLastWeek = date.getTime() >= nextYearFirstWeekFirstDay.getTime();

    let year = date[isUTC ? 'getUTCFullYear' : 'getFullYear']();
    if (isBeforeFirstWeek) {
        --year;
        firstWeekFirstDay[isUTC ? 'setUTCFullYear' : 'setFullYear'](firstWeekFirstDay[isUTC ? 'getUTCFullYear' : 'getFullYear']() - 1);
        const dayOfYear = dayDiff(getWeekFirstDay(date, isUTC, weekStartDay), firstWeekFirstDay);
        week = Math.round(dayOfYear / 7) + 1;
    } else if (isAfterLastWeek) {
        ++year;
        week = 1;
    } else {
        const dayOfYear = dayDiff(getWeekFirstDay(date, isUTC, weekStartDay), firstWeekFirstDay);
        week = Math.round(dayOfYear / 7) + 1;
    }

    return {
        week,
        year
    };
}

export function formatDate(date: Date, format: string, {
    locale,
    isUTC,
    weekStartDay = 0
}: {
    locale?: string;
    isUTC?: boolean;
    weekStartDay?: number;
} = {}): string {
    const getter: FormatGetter = (opts, field): string | undefined => {
        if (field === 'week') {
            const { week } = getIOSWeekYear(date, isUTC || false, weekStartDay);

            return String(week);
        }

        if (field === 'weekofmonth') {
            const weekday = date[isUTC ? 'getUTCDay' : 'getDay']();
            const firstDay = new Date(date);
            firstDay[isUTC ? 'setUTCDate' : 'setDate'](1);
            const firstWeekday = firstDay[isUTC ? 'getUTCDay' : 'getDay']();
            const dayOfMonth = date[isUTC ? 'getUTCDate' : 'getDate']();

            return String(Math.ceil(dayOfMonth / 7) + (weekday < firstWeekday ? 1 : 0));
        }

        if (field === 'dayofweekinmonth') {
            const dayOfMonth = date[isUTC ? 'getUTCDate' : 'getDate']();

            return String(Math.ceil(dayOfMonth / 7));
        }

        if (field === 'weekdaynumeric') {
            let weekday = date[isUTC ? 'getUTCDay' : 'getDay']();

            if (weekday < weekStartDay) {
                weekday += 7;
            }

            return String(weekday - weekStartDay + 1);
        }

        if (field === 'dayofyear') {
            const firstDay = new Date(date);
            firstDay[isUTC ? 'setUTCMonth' : 'setMonth'](0);
            firstDay[isUTC ? 'setUTCDate' : 'setDate'](1);
            firstDay[isUTC ? 'setUTCHours' : 'setHours'](1);
            firstDay[isUTC ? 'setUTCMinutes' : 'setMinutes'](1);
            firstDay[isUTC ? 'setUTCSeconds' : 'setSeconds'](1);

            const dayOfYear = Math.ceil((date.getTime() - firstDay.getTime()) / msInDay);

            return String(dayOfYear);
        }

        if (field === 'weekyear') {
            let { year } = getIOSWeekYear(date, isUTC || false, weekStartDay);

            if (year < 1) {
                year = 1 - year;
            }

            if (opts.year === '2-digit') {
                return String(year % 100);
            }
            return String(year);
        }

        if (field === 'extendedyear') {
            const year = date[isUTC ? 'getUTCFullYear' : 'getFullYear']();

            if (opts.year === '2-digit') {
                return String(year % 100);
            }
            return String(year);
        }

        if (field === 'timezoneoffset') {
            if (isUTC) {
                return '0';
            }
            return String(date.getTimezoneOffset());
        }

        if (isUTC) {
            opts.timeZone = 'UTC';
        }

        const formatter = new Intl.DateTimeFormat(locale, opts);
        const parts = formatter.formatToParts(date);

        for (let i = 0; i < parts.length; ++i) {
            if (parts[i].type === field) {
                return parts[i].value;
            }
        }
    };

    return (format
        .match(formattingTokensRegExp) || [])
        .map(substring => {
            if (substring === "''") {
                return "'";
            }

            const firstCharacter = substring[0];
            if (firstCharacter === "'") {
                return cleanEscapedString(substring);
            }

            if (formatters[firstCharacter]) {
                return formatters[firstCharacter](substring.length, getter);
            }

            if (firstCharacter.match(unescapedLatinCharacterRegExp)) {
                throw new Error(
                    `Format string contains an unescaped latin alphabet character "${firstCharacter}"`
                );
            }

            return substring;
        })
        .join('');
}
