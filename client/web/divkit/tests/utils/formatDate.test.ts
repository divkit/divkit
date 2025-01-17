import {
    describe,
    expect,
    test
} from 'vitest';

/* eslint-disable max-len */
import { formatDate } from '../../src/utils/formatDate';

const testDate = new Date(0);
const testDate2 = new Date(1970, 1, 15, 17);
const testDate3 = new Date(1970, 1, 15, 12, 30, 30, 123);
const testDate4 = new Date(1970, 1, 15, 0, 30);
const year1 = new Date(1, 5, 1);
const year0 = new Date(1, 5, 1);
const yearMinus1 = new Date(1, 5, 1);
year1.setFullYear(1);
year0.setFullYear(0);
yearMinus1.setFullYear(-1);

describe('formatDate', () => {
    describe('fields', () => {
        test('G', () => {
            expect(formatDate(testDate, 'G', {
                locale: 'en-US'
            })).toEqual('AD');
            expect(formatDate(testDate, 'GG', {
                locale: 'en-US'
            })).toEqual('AD');
            expect(formatDate(testDate, 'GGG', {
                locale: 'en-US'
            })).toEqual('AD');
            expect(formatDate(testDate, 'GGGG', {
                locale: 'en-US'
            })).toEqual('Anno Domini');
            expect(formatDate(testDate, 'GGGGG', {
                locale: 'en-US'
            })).toEqual('A');
        });

        test('y', () => {
            expect(formatDate(testDate, 'y', {
                locale: 'en-US'
            })).toEqual('1970');
            expect(formatDate(testDate, 'yy', {
                locale: 'en-US'
            })).toEqual('70');
            expect(formatDate(testDate, 'yyy', {
                locale: 'en-US'
            })).toEqual('1970');
            expect(formatDate(testDate, 'yyyy', {
                locale: 'en-US'
            })).toEqual('1970');
            expect(formatDate(testDate, 'yyyyy', {
                locale: 'en-US'
            })).toEqual('01970');

            expect(formatDate(year1, 'y G', {
                locale: 'en-US'
            })).toEqual('1 AD');
            expect(formatDate(year0, 'y G', {
                locale: 'en-US'
            })).toEqual('1 BC');
            expect(formatDate(yearMinus1, 'y G', {
                locale: 'en-US'
            })).toEqual('2 BC');

            expect(formatDate(new Date(2008, 11, 29), 'y', {
                locale: 'en-US'
            })).toEqual('2008');

            expect(formatDate(new Date(2010, 0, 3), 'y', {
                locale: 'en-US'
            })).toEqual('2010');
        });

        test('Y', () => {
            expect(formatDate(testDate, 'Y', {
                locale: 'en-US'
            })).toEqual('1970');
            expect(formatDate(testDate, 'YY', {
                locale: 'en-US'
            })).toEqual('70');
            expect(formatDate(testDate, 'YYY', {
                locale: 'en-US'
            })).toEqual('1970');
            expect(formatDate(testDate, 'YYYY', {
                locale: 'en-US'
            })).toEqual('1970');
            expect(formatDate(testDate, 'YYYYY', {
                locale: 'en-US'
            })).toEqual('01970');

            expect(formatDate(year1, 'Y G', {
                locale: 'en-US'
            })).toEqual('1 AD');
            expect(formatDate(year0, 'Y G', {
                locale: 'en-US'
            })).toEqual('1 BC');
            expect(formatDate(yearMinus1, 'Y G', {
                locale: 'en-US'
            })).toEqual('2 BC');

            expect(formatDate(new Date(2008, 11, 29), 'Y', {
                locale: 'en-US'
            })).toEqual('2009');

            expect(formatDate(new Date(2010, 0, 3), 'Y', {
                locale: 'en-US'
            })).toEqual('2010');
        });

        test('u', () => {
            expect(formatDate(testDate, 'u', {
                locale: 'en-US'
            })).toEqual('1970');
            expect(formatDate(testDate, 'uu', {
                locale: 'en-US'
            })).toEqual('1970');
            expect(formatDate(testDate, 'uuu', {
                locale: 'en-US'
            })).toEqual('1970');
            expect(formatDate(testDate, 'uuuu', {
                locale: 'en-US'
            })).toEqual('1970');
            expect(formatDate(testDate, 'uuuuu', {
                locale: 'en-US'
            })).toEqual('01970');

            expect(formatDate(year1, 'u G', {
                locale: 'en-US'
            })).toEqual('1 AD');
            expect(formatDate(year0, 'u G', {
                locale: 'en-US'
            })).toEqual('0 BC');
            expect(formatDate(yearMinus1, 'u G', {
                locale: 'en-US'
            })).toEqual('-1 BC');

            expect(formatDate(new Date(2008, 11, 29), 'u', {
                locale: 'en-US'
            })).toEqual('2008');

            expect(formatDate(new Date(2010, 0, 3), 'u', {
                locale: 'en-US'
            })).toEqual('2010');
        });

        test('M', () => {
            expect(formatDate(testDate, 'M', {
                locale: 'en-US'
            })).toEqual('1');
            expect(formatDate(testDate, 'MM', {
                locale: 'en-US'
            })).toEqual('01');
            expect(formatDate(testDate, 'MMM', {
                locale: 'en-US'
            })).toEqual('Jan');
            expect(formatDate(testDate, 'MMMM', {
                locale: 'en-US'
            })).toEqual('January');
            expect(formatDate(testDate, 'MMMMM', {
                locale: 'en-US'
            })).toEqual('J');
        });

        test('w', () => {
            expect(formatDate(testDate2, 'w', {
                locale: 'en-US',
                weekStartDay: 0
            })).toEqual('8');
            expect(formatDate(testDate2, 'ww', {
                locale: 'en-US',
                weekStartDay: 0
            })).toEqual('08');
            expect(formatDate(testDate2, 'www', {
                locale: 'en-US',
                weekStartDay: 0
            })).toEqual('008');

            expect(formatDate(testDate2, 'w', {
                locale: 'en-US',
                weekStartDay: 1
            })).toEqual('7');

            expect(formatDate(new Date(2005, 0 /* Jan */, 2), 'w', {
                locale: 'en-US',
                weekStartDay: 0
            })).toEqual('2');
            expect(formatDate(new Date(2008, 11 /* Dec */, 29), 'w', {
                locale: 'en-US',
                weekStartDay: 0
            })).toEqual('1');
            expect(formatDate(new Date(2008, 11 /* Dec */, 29), 'w', {
                locale: 'en-US',
                weekStartDay: 0
            })).toEqual('1');
            expect(formatDate(new Date(1970, 1 /* Feb */, 15), 'w', {
                locale: 'en-US',
                weekStartDay: 0
            })).toEqual('8');
            expect(formatDate(new Date(1970, 1 /* Feb */, 15), 'w', {
                locale: 'en-US',
                weekStartDay: 1
            })).toEqual('7');
        });

        test('W', () => {
            expect(formatDate(testDate2, 'W', {
                locale: 'en-US'
            })).toEqual('3');
            expect(formatDate(testDate2, 'WW', {
                locale: 'en-US'
            })).toEqual('03');
            expect(formatDate(testDate2, 'WWW', {
                locale: 'en-US'
            })).toEqual('003');
        });

        test('d', () => {
            expect(formatDate(testDate2, 'd', {
                locale: 'en-US'
            })).toEqual('15');
            expect(formatDate(testDate2, 'dd', {
                locale: 'en-US'
            })).toEqual('15');
            expect(formatDate(testDate2, 'ddd', {
                locale: 'en-US'
            })).toEqual('015');
        });

        test('D', () => {
            expect(formatDate(testDate2, 'D', {
                locale: 'en-US'
            })).toEqual('46');
            expect(formatDate(testDate2, 'DD', {
                locale: 'en-US'
            })).toEqual('46');
            expect(formatDate(testDate2, 'DDD', {
                locale: 'en-US'
            })).toEqual('046');
        });

        test('F', () => {
            expect(formatDate(testDate2, 'F', {
                locale: 'en-US'
            })).toEqual('3');
            expect(formatDate(testDate2, 'FF', {
                locale: 'en-US'
            })).toEqual('03');
            expect(formatDate(testDate2, 'FFF', {
                locale: 'en-US'
            })).toEqual('003');
        });

        test('E', () => {
            expect(formatDate(testDate, 'E', {
                locale: 'en-US'
            })).toEqual('Thu');
            expect(formatDate(testDate, 'EE', {
                locale: 'en-US'
            })).toEqual('Thu');
            expect(formatDate(testDate, 'EEE', {
                locale: 'en-US'
            })).toEqual('Thu');
            expect(formatDate(testDate, 'EEEE', {
                locale: 'en-US'
            })).toEqual('Thursday');
            expect(formatDate(testDate, 'EEEEE', {
                locale: 'en-US'
            })).toEqual('T');
        });

        test('e', () => {
            expect(formatDate(testDate, 'e', {
                locale: 'en-US'
            })).toEqual('5');
            expect(formatDate(testDate, 'ee', {
                locale: 'en-US'
            })).toEqual('05');
            expect(formatDate(testDate, 'eee', {
                locale: 'en-US'
            })).toEqual('Thu');
            expect(formatDate(testDate, 'eeee', {
                locale: 'en-US'
            })).toEqual('Thursday');
            expect(formatDate(testDate, 'eeeee', {
                locale: 'en-US'
            })).toEqual('T');
        });

        test('a', () => {
            expect(formatDate(testDate, 'a', {
                locale: 'en-US'
            })).toEqual('AM');
            expect(formatDate(testDate, 'aa', {
                locale: 'en-US'
            })).toEqual('AM');
            expect(formatDate(testDate, 'aaa', {
                locale: 'en-US'
            })).toEqual('AM');
            expect(formatDate(testDate, 'aaaa', {
                locale: 'en-US'
            })).toEqual('AM');
        });

        test('h', () => {
            expect(formatDate(testDate2, 'h', {
                locale: 'en-US'
            })).toEqual('5');
            expect(formatDate(testDate2, 'hh', {
                locale: 'en-US'
            })).toEqual('05');
            expect(formatDate(testDate2, 'hhh', {
                locale: 'en-US'
            })).toEqual('005');

            expect(formatDate(testDate3, 'h', {
                locale: 'en-US'
            })).toEqual('12');
            expect(formatDate(testDate3, 'hh', {
                locale: 'en-US'
            })).toEqual('12');
            expect(formatDate(testDate3, 'hhh', {
                locale: 'en-US'
            })).toEqual('012');
        });

        test('H', () => {
            expect(formatDate(testDate2, 'H', {
                locale: 'en-US'
            })).toEqual('17');
            expect(formatDate(testDate2, 'HH', {
                locale: 'en-US'
            })).toEqual('17');
            expect(formatDate(testDate2, 'HHH', {
                locale: 'en-US'
            })).toEqual('017');

            expect(formatDate(testDate4, 'H', {
                locale: 'en-US'
            })).toEqual('0');
            expect(formatDate(testDate4, 'HH', {
                locale: 'en-US'
            })).toEqual('00');
            expect(formatDate(testDate4, 'HHH', {
                locale: 'en-US'
            })).toEqual('000');
        });

        test('K', () => {
            expect(formatDate(testDate2, 'K', {
                locale: 'en-US'
            })).toEqual('5');
            expect(formatDate(testDate2, 'KK', {
                locale: 'en-US'
            })).toEqual('05');
            expect(formatDate(testDate2, 'KKK', {
                locale: 'en-US'
            })).toEqual('005');

            expect(formatDate(testDate3, 'K', {
                locale: 'en-US'
            })).toEqual('0');
            expect(formatDate(testDate3, 'KK', {
                locale: 'en-US'
            })).toEqual('00');
            expect(formatDate(testDate3, 'KKK', {
                locale: 'en-US'
            })).toEqual('000');
        });

        test('k', () => {
            expect(formatDate(testDate2, 'k', {
                locale: 'en-US'
            })).toEqual('17');
            expect(formatDate(testDate2, 'kk', {
                locale: 'en-US'
            })).toEqual('17');
            expect(formatDate(testDate2, 'kkk', {
                locale: 'en-US'
            })).toEqual('017');

            expect(formatDate(testDate4, 'k', {
                locale: 'en-US'
            })).toEqual('24');
            expect(formatDate(testDate4, 'kk', {
                locale: 'en-US'
            })).toEqual('24');
            expect(formatDate(testDate4, 'kkk', {
                locale: 'en-US'
            })).toEqual('024');
        });

        test('m', () => {
            expect(formatDate(testDate2, 'm', {
                locale: 'en-US'
            })).toEqual('0');
            expect(formatDate(testDate2, 'mm', {
                locale: 'en-US'
            })).toEqual('00');
            expect(formatDate(testDate2, 'mmm', {
                locale: 'en-US'
            })).toEqual('000');

            expect(formatDate(testDate3, 'm', {
                locale: 'en-US'
            })).toEqual('30');
            expect(formatDate(testDate3, 'mm', {
                locale: 'en-US'
            })).toEqual('30');
            expect(formatDate(testDate3, 'mmm', {
                locale: 'en-US'
            })).toEqual('030');
        });

        test('s', () => {
            expect(formatDate(testDate2, 's', {
                locale: 'en-US'
            })).toEqual('0');
            expect(formatDate(testDate2, 'ss', {
                locale: 'en-US'
            })).toEqual('00');
            expect(formatDate(testDate2, 'sss', {
                locale: 'en-US'
            })).toEqual('000');

            expect(formatDate(testDate3, 's', {
                locale: 'en-US'
            })).toEqual('30');
            expect(formatDate(testDate3, 'ss', {
                locale: 'en-US'
            })).toEqual('30');
            expect(formatDate(testDate3, 'sss', {
                locale: 'en-US'
            })).toEqual('030');
        });

        test('S', () => {
            expect(formatDate(testDate2, 'S', {
                locale: 'en-US'
            })).toEqual('0');
            expect(formatDate(testDate2, 'SS', {
                locale: 'en-US'
            })).toEqual('00');
            expect(formatDate(testDate2, 'SSS', {
                locale: 'en-US'
            })).toEqual('000');

            expect(formatDate(testDate3, 'S', {
                locale: 'en-US'
            })).toEqual('1');
            expect(formatDate(testDate3, 'SS', {
                locale: 'en-US'
            })).toEqual('12');
            expect(formatDate(testDate3, 'SSS', {
                locale: 'en-US'
            })).toEqual('123');
        });

        test('z', () => {
            expect(formatDate(testDate2, 'z', {
                locale: 'en-US',
                isUTC: true
            })).toEqual('UTC');
            expect(formatDate(testDate2, 'zz', {
                locale: 'en-US',
                isUTC: true
            })).toEqual('UTC');
            expect(formatDate(testDate2, 'zzz', {
                locale: 'en-US',
                isUTC: true
            })).toEqual('UTC');
            expect(formatDate(testDate2, 'zzzz', {
                locale: 'en-US',
                isUTC: true
            })).toEqual('Coordinated Universal Time');
        });

        test('Z', () => {
            expect(formatDate(testDate2, 'Z', {
                locale: 'en-US',
                isUTC: true
            })).toEqual('+0000');
            expect(formatDate(testDate2, 'ZZ', {
                locale: 'en-US',
                isUTC: true
            })).toEqual('+0000');
            expect(formatDate(testDate2, 'ZZZ', {
                locale: 'en-US',
                isUTC: true
            })).toEqual('+0000');
            expect(formatDate(testDate2, 'ZZZZ', {
                locale: 'en-US',
                isUTC: true
            })).toEqual('+0000');
        });
    });

    // Based on date-fns tests https://github.com/date-fns/date-fns/blob/main/src/format/test.ts (MIT License)
    describe('complex', () => {
        const date = new Date(1986, 3 /* Apr */, 4, 10, 32, 55, 123);

        /* const offset = date.getTimezoneOffset();
        const absoluteOffset = Math.abs(offset);
        const hours = Math.trunc(absoluteOffset / 60);
        const hoursLeadingZero = hours < 10 ? '0' : '';
        const minutes = absoluteOffset % 60;
        const minutesLeadingZero = minutes < 10 ? '0' : '';
        const sign = offset > 0 ? '-' : '+'; */

        /* const timezone =
            sign + hoursLeadingZero + hours + ':' + minutesLeadingZero + minutes;
        const timezoneShort = timezone.replace(':', '');
        const timezoneWithOptionalMinutesShort =
            minutes === 0 ? sign + hoursLeadingZero + hours : timezoneShort; */

        /* const timezoneWithZ = offset === 0 ? 'Z' : timezone;
        const timezoneWithZShort = offset === 0 ? 'Z' : timezoneShort;
        const timezoneWithOptionalMinutesAndZShort =
            offset === 0 ? 'Z' : timezoneWithOptionalMinutesShort;

        const timezoneGMTShort =
            minutes === 0 ?
                'GMT' + sign + hours :
                'GMT' + sign + hours + ':' + minutesLeadingZero + minutes;
        const timezoneGMT = 'GMT' + timezone;

        const timestamp = date.getTime().toString();
        const secondsTimestamp = Math.trunc(date.getTime() / 1000).toString(); */

        test('escapes characters between the single quote characters', () => {
            const result = formatDate(date, "'yyyy-'MM-dd'THH:mm:ss.SSSX' yyyy-'MM-dd'");
            expect(result).toBe('yyyy-04-04THH:mm:ss.SSSX 1986-MM-dd');
        });

        test('two single quote characters are transformed into a "real" single quote', () => {
            const date = new Date(2014, 3, 4, 5);
            expect(formatDate(date, "''h 'o''clock'''")).toBe("'5 o'clock'");
        });

        test('accepts new line charactor', () => {
            const date = new Date(2014, 3, 4, 5);
            expect(formatDate(date, "yyyy-MM-dd'\n'HH:mm:ss")).toBe('2014-04-04\n05:00:00');
        });

        test('alias formatDate has same behavior as format', () => {
            const date = new Date(2014, 3, 4, 5);
            expect(formatDate(date, "yyyy-MM-dd'\n'HH:mm:ss")).toBe(formatDate(date, "yyyy-MM-dd'\n'HH:mm:ss"));
        });

        /* xdescribe('ordinal numbers', () => {
            test('ordinal day of an ordinal month', () => {
                const result = formatDate(date, "do 'day of the' Mo 'month of' yyyy");
                expect(result).toBe('4th day of the 4th month of 1986');
            });

            test('should return a correct ordinal number', () => {
                const result = [];
                for (let i = 1; i <= 31; i++) {
                    result.push(formatDate(new Date(2015, 0, i), 'do'));
                }
                const expected = [
                    '1st',
                    '2nd',
                    '3rd',
                    '4th',
                    '5th',
                    '6th',
                    '7th',
                    '8th',
                    '9th',
                    '10th',
                    '11th',
                    '12th',
                    '13th',
                    '14th',
                    '15th',
                    '16th',
                    '17th',
                    '18th',
                    '19th',
                    '20th',
                    '21st',
                    '22nd',
                    '23rd',
                    '24th',
                    '25th',
                    '26th',
                    '27th',
                    '28th',
                    '29th',
                    '30th',
                    '31st',
                ];
                expect(result).toEqual(expected);
            });
        }); */

        test('era', () => {
            const result = formatDate(date, 'G GG GGG GGGG GGGGG');
            expect(result).toBe('AD AD AD Anno Domini A');

            const bcDate = new Date();
            bcDate.setFullYear(-1, 0 /* Jan */, 1);
            const bcResult = formatDate(bcDate, 'G GG GGG GGGG GGGGG');
            expect(bcResult).toBe('BC BC BC Before Christ B');
        });

        describe('year', () => {
            describe('regular year', () => {
                /* test('works as expected', () => {
                    const result = formatDate(date, 'y yo yy yyy yyyy yyyyy');
                    expect(result).toBe('1986 1986th 86 1986 1986 01986');
                }); */
                test('works as expected', () => {
                    const result = formatDate(date, 'y yy yyy yyyy yyyyy');
                    expect(result).toBe('1986 86 1986 1986 01986');
                });

                test('1 BC formats as 1', () => {
                    const date = new Date(0);
                    date.setFullYear(0, 0 /* Jan */, 1);
                    date.setHours(0, 0, 0, 0);
                    const result = formatDate(date, 'y');
                    expect(result).toBe('1');
                });

                test('2 BC formats as 2', () => {
                    const date = new Date(0);
                    date.setFullYear(-1, 0 /* Jan */, 1);
                    date.setHours(0, 0, 0, 0);
                    const result = formatDate(date, 'y');
                    expect(result).toBe('2');
                });

                /* xit('2 BC formats as 2nd', () => {
                    const date = new Date();
                    date.setFullYear(-1, 0 /* Jan *\/, 1);
                    date.setHours(0, 0, 0, 0);
                    const result = formatDate(date, 'yo');
                    expect(result).toBe('2nd');
                }); */
                test('2 BC formats as 2nd', () => {
                    const date = new Date();
                    date.setFullYear(-1, 0 /* Jan */, 1);
                    date.setHours(0, 0, 0, 0);
                    const result = formatDate(date, 'y');
                    expect(result).toBe('2');
                });
            });

            describe('local week-numbering year', () => {
                /* xit('works as expected', () => {
                    const result = formatDate(date, 'Y Yo YY YYY YYYY YYYYY', {
                        useAdditionalWeekYearTokens: true,
                    });
                    expect(result).toBe('1986 1986th 86 1986 1986 01986');
                }); */
                test('works as expected', () => {
                    const result = formatDate(date, 'Y YY YYY YYYY YYYYY');
                    expect(result).toBe('1986 86 1986 1986 01986');
                });

                test('the first week of the next year', () => {
                    const result = formatDate(new Date(2013, 11 /* Dec */, 29), 'YYYY');
                    expect(result).toBe('2014');
                });

                /* xit('allows to specify `weekStartsOn` and `firstWeekContainsDate` in options', () => {
                    const result = formatDate(new Date(2013, 11 /* Dec *\/, 29), 'YYYY', {
                        weekStartsOn: 1,
                        firstWeekContainsDate: 4,
                        useAdditionalWeekYearTokens: true,
                    });
                    expect(result).toBe('2013');
                }); */

                test('the first week of year', () => {
                    const result = formatDate(new Date(2016, 0 /* Jan */, 1), 'YYYY');
                    expect(result).toBe('2016');
                });

                test('1 BC formats as 1', () => {
                    const date = new Date(0);
                    date.setFullYear(0, 6 /* Jul */, 2);
                    date.setHours(0, 0, 0, 0);
                    const result = formatDate(date, 'Y');
                    expect(result).toBe('1');
                });

                test('2 BC formats as 2', () => {
                    const date = new Date(0);
                    date.setFullYear(-1, 6 /* Jul */, 2);
                    date.setHours(0, 0, 0, 0);
                    const result = formatDate(date, 'Y');
                    expect(result).toBe('2');
                });
            });

            /* xdescribe('ISO week-numbering year', () => {
                test('works as expected', () => {
                    const result = formatDate(date, 'R RR RRR RRRR RRRRR');
                    expect(result).toBe('1986 1986 1986 1986 01986');
                });

                test('the first week of the next year', () => {
                    const result = formatDate(new Date(2013, 11 /* Dec *\/, 30), 'RRRR');
                    expect(result).toBe('2014');
                });

                test('the last week of the previous year', () => {
                    const result = formatDate(new Date(2016, 0 /* Jan *\/, 1), 'RRRR');
                    expect(result).toBe('2015');
                });

                test('1 BC formats as 0', () => {
                    const date = new Date(0);
                    date.setFullYear(0, 6 /* Jul *\/, 2);
                    date.setHours(0, 0, 0, 0);
                    const result = formatDate(date, 'R');
                    expect(result).toBe('0');
                });

                test('2 BC formats as -1', () => {
                    const date = new Date(0);
                    date.setFullYear(-1, 6 /* Jul *\/, 2);
                    date.setHours(0, 0, 0, 0);
                    const result = formatDate(date, 'R');
                    expect(result).toBe('-1');
                });
            }); */

            describe('extended year', () => {
                test('works as expected', () => {
                    const result = formatDate(date, 'u uu uuu uuuu uuuuu');
                    expect(result).toBe('1986 1986 1986 1986 01986');
                });

                test('1 BC formats as 0', () => {
                    const date = new Date(0);
                    date.setFullYear(0, 0, 1);
                    date.setHours(0, 0, 0, 0);
                    const result = formatDate(date, 'u');
                    expect(result).toBe('0');
                });

                test('2 BC formats as -1', () => {
                    const date = new Date(0);
                    date.setFullYear(-1, 0, 1);
                    date.setHours(0, 0, 0, 0);
                    const result = formatDate(date, 'u');
                    expect(result).toBe('-1');
                });
            });
        });

        /* xdescribe('quarter', () => {
            test('formatting quarter', () => {
                const result = formatDate(date, 'Q Qo QQ QQQ QQQQ QQQQQ');
                expect(result).toBe('2 2nd 02 Q2 2nd quarter 2');
            });

            test('stand-alone quarter', () => {
                const result = formatDate(date, 'q qo qq qqq qqqq qqqqq');
                expect(result).toBe('2 2nd 02 Q2 2nd quarter 2');
            });

            test('returns a correct quarter for each month', () => {
                const result = [];
                for (let i = 0; i <= 11; i++) {
                    result.push(formatDate(new Date(1986, i, 1), 'Q'));
                }
                const expected = [
                    '1',
                    '1',
                    '1',
                    '2',
                    '2',
                    '2',
                    '3',
                    '3',
                    '3',
                    '4',
                    '4',
                    '4',
                ];
                expect(result).toEqual(expected);
            });
        }); */

        describe('month', () => {
            /* test('formatting month', () => {
                const result = formatDate(date, 'M Mo MM MMM MMMM MMMMM');
                expect(result).toBe('4 4th 04 Apr April A');
            }); */
            test('formatting month', () => {
                const result = formatDate(date, 'M MM MMM MMMM MMMMM');
                expect(result).toBe('4 04 Apr April A');
            });

            /* test('stand-alone month', () => {
                const result = formatDate(date, 'L Lo LL LLL LLLL LLLLL');
                expect(result).toBe('4 4th 04 Apr April A');
            }); */
        });

        describe('week', () => {
            describe('local week of year', () => {
                /* test('works as expected', () => {
                    const date = new Date(1986, 3 /* Apr *\/, 6);
                    const result = formatDate(date, 'w wo ww');
                    expect(result).toBe('15 15th 15');
                }); */
                test('works as expected', () => {
                    const date = new Date(1986, 3 /* Apr */, 6);
                    const result = formatDate(date, 'w ww');
                    expect(result).toBe('15 15');
                });

                /* test('allows to specify `weekStartsOn` and `firstWeekContainsDate` in options', () => {
                    const date = new Date(1986, 3 /* Apr *\/, 6);
                    const result = formatDate(date, 'w wo ww', {
                        weekStartsOn: 1,
                        firstWeekContainsDate: 4,
                    });
                    expect(result).toBe('14 14th 14');
                }); */
            });

            /* xit('ISO week of year', () => {
                const date = new Date(1986, 3 /* Apr *\/, 6);
                const result = formatDate(date, 'I Io II');
                expect(result).toBe('14 14th 14');
            }); */
        });

        describe('day', () => {
            /* xit('date', () => {
                const result = formatDate(date, 'd do dd');
                expect(result).toBe('4 4th 04');
            }); */
            test('date', () => {
                const result = formatDate(date, 'd dd');
                expect(result).toBe('4 04');
            });

            describe('day of year', () => {
                /* xit('works as expected', () => {
                    const result = formatDate(date, 'D Do DD DDD DDDDD', {
                        useAdditionalDayOfYearTokens: true,
                    });
                    expect(result).toBe('94 94th 94 094 00094');
                }); */
                test('works as expected', () => {
                    const result = formatDate(date, 'D DD DDD DDDDD');
                    expect(result).toBe('94 94 094 00094');
                });

                test('returns a correct day number for the last day of a leap year', () => {
                    const result = formatDate(
                        new Date(1992, 11 /* Dec */, 31, 23, 59, 59, 999),
                        'D'
                    );
                    expect(result).toBe('366');
                });
            });
        });

        describe('week day', () => {
            describe('day of week', () => {
                /* test('works as expected', () => {
                    const result = formatDate(date, 'E EE EEE EEEE EEEEE EEEEEE');
                    expect(result).toBe('Fri Fri Fri Friday F Fr');
                }); */
                test('works as expected', () => {
                    const result = formatDate(date, 'E EE EEE EEEE EEEEE');
                    expect(result).toBe('Fri Fri Fri Friday F');
                });
            });

            /* xdescribe('ISO day of week', () => {
                test('works as expected', () => {
                    const result = formatDate(date, 'i io ii iii iiii iiiii iiiiii');
                    expect(result).toBe('5 5th 05 Fri Friday F Fr');
                });

                test('returns a correct day of an ISO week', () => {
                    const result = [];
                    for (let i = 1; i <= 7; i++) {
                        result.push(formatDate(new Date(1986, 8 /* Sep *\/, i), 'i'));
                    }
                    const expected = ['1', '2', '3', '4', '5', '6', '7'];
                    expect(result).toEqual(expected);
                });
            }); */

            describe('formatting day of week', () => {
                /* xit('works as expected', () => {
                    const result = formatDate(date, 'e eo ee eee eeee eeeee eeeeee');
                    expect(result).toBe('6 6th 06 Fri Friday F Fr');
                }); */
                test('works as expected', () => {
                    const result = formatDate(date, 'e ee eee eeee eeeee');
                    expect(result).toBe('6 06 Fri Friday F');
                });

                test('by default, 1 is Sunday, 2 is Monday, ...', () => {
                    const result = [];
                    for (let i = 7; i <= 13; i++) {
                        result.push(formatDate(new Date(1986, 8 /* Sep */, i), 'e'));
                    }
                    const expected = ['1', '2', '3', '4', '5', '6', '7'];
                    expect(result).toEqual(expected);
                });

                test('allows to specify which day is the first day of the week', () => {
                    const result = [];
                    for (let i = 1; i <= 7; i++) {
                        result.push(
                            formatDate(new Date(1986, 8 /* Sep */, i), 'e', { weekStartDay: 1 }),
                        );
                    }
                    const expected = ['1', '2', '3', '4', '5', '6', '7'];
                    expect(result).toEqual(expected);
                });
            });

            /* xdescribe('stand-alone day of week', () => {
                test('works as expected', () => {
                    const result = formatDate(date, 'c co cc ccc cccc ccccc cccccc');
                    expect(result).toBe('6 6th 06 Fri Friday F Fr');
                });

                test('by default, 1 is Sunday, 2 is Monday, ...', () => {
                    const result = [];
                    for (let i = 7; i <= 13; i++) {
                        result.push(formatDate(new Date(1986, 8 /* Sep *\/, i), 'c'));
                    }
                    const expected = ['1', '2', '3', '4', '5', '6', '7'];
                    expect(result).toEqual(expected);
                });

                test('allows to specify which day is the first day of the week', () => {
                    const result = [];
                    for (let i = 1; i <= 7; i++) {
                        result.push(
                            formatDate(new Date(1986, 8 /* Sep *\/, i), 'c', { weekStartsOn: 1 }),
                        );
                    }
                    const expected = ['1', '2', '3', '4', '5', '6', '7'];
                    expect(result).toEqual(expected);
                });
            }); */
        });

        describe('day period and hour', () => {
            /* xit('hour [1-12]', () => {
                const result = formatDate(
                    new Date(2018, 0 /* Jan *\/, 1, 0, 0, 0, 0),
                    'h ho hh',
                );
                expect(result).toBe('12 12th 12');
            }); */
            test('hour [1-12]', () => {
                const result = formatDate(
                    new Date(2018, 0 /* Jan */, 1, 0, 0, 0, 0),
                    'h hh',
                );
                expect(result).toBe('12 12');
            });

            /* xit('hour [0-23]', () => {
                const result = formatDate(
                    new Date(2018, 0 /* Jan *\/, 1, 0, 0, 0, 0),
                    'H Ho HH',
                );
                expect(result).toBe('0 0th 00');
            }); */
            test('hour [0-23]', () => {
                const result = formatDate(
                    new Date(2018, 0 /* Jan */, 1, 0, 0, 0, 0),
                    'H HH',
                );
                expect(result).toBe('0 00');
            });

            /* xit('hour [0-11]', () => {
                const result = formatDate(
                    new Date(2018, 0 /* Jan *\/, 1, 0, 0, 0, 0),
                    'K Ko KK',
                );
                expect(result).toBe('0 0th 00');
            }); */
            test('hour [0-11]', () => {
                const result = formatDate(
                    new Date(2018, 0 /* Jan */, 1, 0, 0, 0, 0),
                    'K KK',
                );
                expect(result).toBe('0 00');
            });

            /* xit('hour [1-24]', () => {
                const result = formatDate(
                    new Date(2018, 0 /* Jan *\/, 1, 0, 0, 0, 0),
                    'k ko kk',
                );
                expect(result).toBe('24 24th 24');
            }); */
            test('hour [1-24]', () => {
                const result = formatDate(
                    new Date(2018, 0 /* Jan */, 1, 0, 0, 0, 0),
                    'k kk',
                );
                expect(result).toBe('24 24');
            });

            describe('AM, PM', () => {
                /* xit('works as expected', () => {
                    const result = formatDate(
                        new Date(2018, 0 /* Jan *\/, 1, 0, 0, 0, 0),
                        'a aa aaa aaaa aaaaa',
                    );
                    expect(result).toBe('AM AM am a.m. a');
                }); */
                test('works as expected', () => {
                    const result = formatDate(
                        new Date(2018, 0 /* Jan */, 1, 0, 0, 0, 0),
                        'a aa aaa aaaa aaaaa',
                    );
                    expect(result).toBe('AM AM AM AM AM');
                });

                test('12 PM', () => {
                    const date = new Date(1986, 3 /* Apr */, 4, 12, 0, 0, 900);
                    expect(formatDate(date, 'h H K k a')).toBe('12 12 0 12 PM');
                });

                test('12 AM', () => {
                    const date = new Date(1986, 3 /* Apr */, 6, 0, 0, 0, 900);
                    expect(formatDate(date, 'h H K k a')).toBe('12 0 0 24 AM');
                });
            });

            /* xdescribe('AM, PM, noon, midnight', () => {
                test('works as expected', () => {
                    const result = formatDate(
                        new Date(1986, 3 /* Apr *\/, 6, 2, 0, 0, 900),
                        'b bb bbb bbbb bbbbb',
                    );
                    expect(result).toBe('AM AM am a.m. a');

                    const pmResult = formatDate(
                        new Date(1986, 3 /* Apr *\/, 6, 13, 0, 0, 900),
                        'b bb bbb bbbb bbbbb',
                    );
                    expect(pmResult).toBe('PM PM pm p.m. p');
                });

                test('12 PM', () => {
                    const date = new Date(1986, 3 /* Apr *\/, 4, 12, 0, 0, 900);
                    expect(formatDate(date, 'b bb bbb bbbb bbbbb')).toBe('noon noon noon noon n');
                });

                test('12 AM', () => {
                    const date = new Date(1986, 3 /* Apr *\/, 6, 0, 0, 0, 900);
                    expect(formatDate(date, 'b bb bbb bbbb bbbbb')).toBe('midnight midnight midnight midnight mi');
                });
            }); */

            /* xdescribe('flexible day periods', () => {
                test('works as expected', () => {
                    const result = formatDate(date, 'B, BB, BBB, BBBB, BBBBB');
                    expect(result).toBe(
                        'in the morning, in the morning, in the morning, in the morning, in the morning'
                    );
                });

                test('12 PM', () => {
                    const date = new Date(1986, 3 /* Apr *\/, 4, 12, 0, 0, 900);
                    expect(formatDate(date, 'h B')).toBe('12 in the afternoon');
                });

                test('5 PM', () => {
                    const date = new Date(1986, 3 /* Apr *\/, 6, 17, 0, 0, 900);
                    expect(formatDate(date, 'h B')).toBe('5 in the evening');
                });

                test('12 AM', () => {
                    const date = new Date(1986, 3 /* Apr *\/, 6, 0, 0, 0, 900);
                    expect(formatDate(date, 'h B')).toBe('12 at night');
                });

                test('4 AM', () => {
                    const date = new Date(1986, 3 /* Apr *\/, 6, 4, 0, 0, 900);
                    expect(formatDate(date, 'h B')).toBe('4 in the morning');
                });
            }); */
        });

        /* xit('minute', () => {
            const result = formatDate(date, 'm mo mm');
            expect(result).toBe('32 32nd 32');
        }); */
        test('minute', () => {
            const result = formatDate(date, 'm mm');
            expect(result).toBe('32 32');
        });

        describe('second', () => {
            /* xit('second', () => {
                const result = formatDate(date, 's so ss');
                expect(result).toBe('55 55th 55');
            }); */
            test('second', () => {
                const result = formatDate(date, 's ss');
                expect(result).toBe('55 55');
            });

            test('fractional seconds', () => {
                const result = formatDate(date, 'S SS SSS SSSS');
                expect(result).toBe('1 12 123 1230');
            });
        });

        /* xdescribe('time zone', () => {
            test('ISO-8601 with Z', () => {
                const result = formatDate(date, 'X XX XXX XXXX XXXXX');
                const expectedResult = [
                    timezoneWithOptionalMinutesAndZShort,
                    timezoneWithZShort,
                    timezoneWithZ,
                    timezoneWithZShort,
                    timezoneWithZ,
                ].join(' ');
                expect(result).toBe(expectedResult);

                const getTimezoneOffsetStub = sinon.stub(
                    Date.prototype,
                    'getTimezoneOffset',
                );
                getTimezoneOffsetStub.returns(0);
                const resultZeroOffset = formatDate(date, 'X XX XXX XXXX XXXXX');
                expect(resultZeroOffset).toBe('Z Z Z Z Z');

                getTimezoneOffsetStub.returns(480);
                const resultNegativeOffset = formatDate(date, 'X XX XXX XXXX XXXXX');
                expect(resultNegativeOffset).toBe('-08 -0800 -08:00 -0800 -08:00');

                getTimezoneOffsetStub.returns(450);
                const resultNegative30Offset = formatDate(date, 'X XX XXX XXXX XXXXX');
                expect(resultNegative30Offset).toBe('-0730 -0730 -07:30 -0730 -07:30');

                getTimezoneOffsetStub.restore();
            });

            test('ISO-8601 without Z', () => {
                const result = formatDate(date, 'x xx xxx xxxx xxxxx');
                const expectedResult = [
                    timezoneWithOptionalMinutesShort,
                    timezoneShort,
                    timezone,
                    timezoneShort,
                    timezone,
                ].join(' ');
                expect(result).toBe(expectedResult);
            });

            test('GMT', () => {
                const result = formatDate(date, 'O OO OOO OOOO');
                const expectedResult = [
                    timezoneGMTShort,
                    timezoneGMTShort,
                    timezoneGMTShort,
                    timezoneGMT,
                ].join(' ');
                expect(result).toBe(expectedResult);

                const getTimezoneOffsetStub = sinon.stub(
                    Date.prototype,
                    'getTimezoneOffset',
                );
                getTimezoneOffsetStub.returns(480);
                const resultNegativeOffset = formatDate(date, 'O OO OOO OOOO');
                expect(resultNegativeOffset).toBe('GMT-8 GMT-8 GMT-8 GMT-08:00');

                getTimezoneOffsetStub.returns(450);
                const resultNegative30Offset = formatDate(date, 'O OO OOO OOOO');
                expect(resultNegative30Offset).toBe('GMT-7:30 GMT-7:30 GMT-7:30 GMT-07:30');

                getTimezoneOffsetStub.restore();
            });

            test('Specific non-location', () => {
                const result = formatDate(date, 'z zz zzz zzzz');
                const expectedResult = [
                    timezoneGMTShort,
                    timezoneGMTShort,
                    timezoneGMTShort,
                    timezoneGMT,
                ].join(' ');
                expect(result).toBe(expectedResult);
            });
        }); */

        /* xdescribe('timestamp', () => {
            test('seconds timestamp', () => {
                const result = formatDate(date, 't');
                expect(result).toBe(secondsTimestamp);
            });

            test('milliseconds timestamp', () => {
                const result = formatDate(date, 'T');
                expect(result).toBe(timestamp);
            });

            test('seconds timestamp handles negative numbers', () => {
                expect(formatDate(new Date(1001), 't')).toBe('1');
                expect(formatDate(new Date(-1001), 't')).toBe('-1');
            });
        }); */

        /* xdescribe('long format', () => {
            test('short date', () => {
                const result = formatDate(date, 'P');
                expect(result).toBe('04/04/1986');
            });

            test('medium date', () => {
                const result = formatDate(date, 'PP');
                expect(result).toBe('Apr 4, 1986');
            });

            test('long date', () => {
                const result = formatDate(date, 'PPP');
                expect(result).toBe('April 4th, 1986');
            });

            test('full date', () => {
                const result = formatDate(date, 'PPPP');
                expect(result).toBe('Friday, April 4th, 1986');
            });

            test('short time', () => {
                const result = formatDate(date, 'p');
                expect(result).toBe('10:32 AM');
            });

            test('medium time', () => {
                const result = formatDate(date, 'pp');
                expect(result).toBe('10:32:55 AM');
            });

            test('long time', () => {
                const result = formatDate(date, 'ppp');
                expect(result).toBe('10:32:55 AM ' + timezoneGMTShort);
            });

            test('full time', () => {
                const result = formatDate(date, 'pppp');
                expect(result).toBe('10:32:55 AM ' + timezoneGMT);
            });

            test('short date + time', () => {
                const result = formatDate(date, 'Pp');
                expect(result).toBe('04/04/1986, 10:32 AM');
            });

            test('medium date + time', () => {
                const result = formatDate(date, 'PPpp');
                expect(result).toBe('Apr 4, 1986, 10:32:55 AM');
            });

            test('long date + time', () => {
                const result = formatDate(date, 'PPPppp');
                expect(result).toBe('April 4th, 1986 at 10:32:55 AM ' + timezoneGMTShort);
            });

            test('full date + time', () => {
                const result = formatDate(date, 'PPPPpppp');
                expect(result).toBe('Friday, April 4th, 1986 at 10:32:55 AM ' + timezoneGMT);
            });

            test('allows arbitrary combination of date and time', () => {
                const result = formatDate(date, 'Ppppp');
                expect(result).toBe('04/04/1986, 10:32:55 AM ' + timezoneGMT);
            });
        }); */

        /* xdescribe('edge cases', () => {
            test('throws RangeError if the time value is invalid', () => {
                expect(format.bind(null, new Date(NaN), 'MMMM d, yyyy')).toThrow(RangeError);
            });

            test('handles dates before 100 AD', () => {
                const initialDate = new Date(0);
                initialDate.setFullYear(7, 11 /* Dec *\/, 31);
                initialDate.setHours(0, 0, 0, 0);
                expect(formatDate(initialDate, 'Y ww i')).toBe('8 01 1');
            });
        }); */

        /* xdescribe('locale features', () => {
            test('allows to pass a custom locale', () => {
                const customLocale = {
                    localize: {
                        month: () => {
                            return 'works';
                        },
                    },
                    formatLong: {
                        date: () => {
                            return "'It' MMMM!";
                        },
                    },
                };
                const result = formatDate(date, 'PPPP', {
                    // @ts-expect-error - It's ok to have incomplete locale
                    locale: customLocale,
                });
                expect(result).toBe('It works!');
            });

            test('allows a localize preprocessor', () => {
                const customLocale = {
                    localize: {
                        month: (v: number) => ['janvier'][v],
                        ordinalNumber: (v: number) => String(v) + 'er',

                        preprocessor: (date: Date, parts: FormatPart[]) => {
                            // replace `do` tokens to `d` if not the first of the month
                            if (date.getDate() === 1) return parts;

                            return parts.map(part =>
                                part.isToken && part.value === 'do' ?
                                    { isToken: true, value: 'd' } :
                                    part,
                            );
                        },
                    },
                };

                let result = formatDate(new Date(2024, 0, 1), 'do MMMM', {
                    // @ts-expect-error - It's ok to have incomplete locale
                    locale: customLocale,
                });
                expect(result).toEqual('1er janvier');

                result = formatDate(new Date(2024, 0, 2), 'do MMMM', {
                    // @ts-expect-error - It's ok to have incomplete locale
                    locale: customLocale,
                });
                expect(result).toEqual('2 janvier');
            });
        }); */

        /* xit('throws RangeError exception if the format string contains an unescaped latin alphabet character', () => {
            expect(format.bind(null, date, 'yyyy-MM-dd-nnnn')).toThrow(RangeError);
        }); */

        describe('useAdditionalWeekYearTokens and useAdditionalDayOfYearTokens options', () => {
            /* xit('throws an error if D token is used', () => {
                expect(() => formatDate(date, 'yyyy-MM-D')).toThrow('Use `d` instead of `D`');
            }); */

            test('allows D token if useAdditionalDayOfYearTokens is set to true', () => {
                const result = formatDate(date, 'yyyy-MM-D');
                expect(result).toEqual('1986-04-94');
            });

            /* xit('throws an error if DD token is used', () => {
                expect(() => formatDate(date, 'yyyy-MM-DD')).toThrow(
                    'Use `dd` instead of `DD`',
                );
            }); */

            test('allows DD token if useAdditionalDayOfYearTokens is set to true', () => {
                const result = formatDate(date, 'yyyy-MM-DD');
                expect(result).toEqual('1986-04-94');
            });

            /* xit('throws an error if YY token is used', () => {
                expect(() => formatDate(date, 'YY-MM-dd')).toThrow(
                    'Use `yy` instead of `YY`',
                );
            }); */

            test('allows YY token if useAdditionalWeekYearTokens is set to true', () => {
                const result = formatDate(date, 'YY-MM-dd');
                expect(result).toEqual('86-04-04');
            });

            /* xit('throws an error if YYYY token is used', () => {
                expect(() => formatDate(date, 'YYYY-MM-dd')).toThrow(
                    'Use `yyyy` instead of `YYYY`',
                );
            }); */

            test('allows YYYY token if useAdditionalWeekYearTokens is set to true', () => {
                const result = formatDate(date, 'YYYY-MM-dd');
                expect(result).toEqual('1986-04-04');
            });

            /* xdescribe('console.warn', () => {
                let warn: SpyInstance;

                beforeEach(() => {
                    warn = vi.spyOn(console, 'warn').mockImplementation(() => undefined);
                });

                afterEach(() => {
                    warn.mockRestore();
                });

                test('warns if "D" token is used', () => {
                    try {
                        formatDate(date, 'yyyy-MM-D');
                    } catch (_) {
                        // Ignore the error
                    }
                    expect(warn).toBeCalledWith(
                        'Use `d` instead of `D` (in `yyyy-MM-D`) for formatting days of the month to the input `' +
                        date +
                        '`; see: https://github.com/date-fns/date-fns/blob/master/docs/unicodeTokens.md',
                    );
                });

                test('warns if "DD" token is used', () => {
                    try {
                        formatDate(date, 'yyyy-MM-DD');
                    } catch (_) {
                        // Ignore the error
                    }
                    expect(warn).toBeCalledWith(
                        'Use `dd` instead of `DD` (in `yyyy-MM-DD`) for formatting days of the month to the input `' +
                        date +
                        '`; see: https://github.com/date-fns/date-fns/blob/master/docs/unicodeTokens.md',
                    );
                });

                test('warns if "DDD" token is used', () => {
                    try {
                        formatDate(date, 'yyyy-MM-DDD');
                    } catch (_) {
                        // Ignore the error
                    }
                    expect(warn).toBeCalledWith(
                        'Use `ddd` instead of `DDD` (in `yyyy-MM-DDD`) for formatting days of the month to the input `' +
                        date +
                        '`; see: https://github.com/date-fns/date-fns/blob/master/docs/unicodeTokens.md',
                    );
                });

                test('warns if "DDDD" token is used', () => {
                    try {
                        formatDate(date, 'yyyy-MM-DDDD');
                    } catch (_) {
                        // Ignore the error
                    }
                    expect(warn).toBeCalledWith(
                        'Use `dddd` instead of `DDDD` (in `yyyy-MM-DDDD`) for formatting days of the month to the input `' +
                        date +
                        '`; see: https://github.com/date-fns/date-fns/blob/master/docs/unicodeTokens.md',
                    );
                });

                test('warns if "DDDDD" token is used', () => {
                    try {
                        formatDate(date, 'yyyy-MM-DDDDD');
                    } catch (_) {
                        // Ignore the error
                    }
                    expect(warn).toBeCalledWith(
                        'Use `ddddd` instead of `DDDDD` (in `yyyy-MM-DDDDD`) for formatting days of the month to the input `' +
                        date +
                        '`; see: https://github.com/date-fns/date-fns/blob/master/docs/unicodeTokens.md',
                    );
                });

                test('warns if "Y" token is used', () => {
                    try {
                        formatDate(date, 'Y-MM-dd');
                    } catch (_) {
                        // Ignore the error
                    }
                    expect(warn).toBeCalledWith(
                        'Use `y` instead of `Y` (in `Y-MM-dd`) for formatting years to the input `' +
                        date +
                        '`; see: https://github.com/date-fns/date-fns/blob/master/docs/unicodeTokens.md',
                    );
                });

                test('warns if "YY" token is used', () => {
                    try {
                        formatDate(date, 'YY-MM-dd');
                    } catch (_) {
                        // Ignore the error
                    }
                    expect(warn).toBeCalledWith(
                        'Use `yy` instead of `YY` (in `YY-MM-dd`) for formatting years to the input `' +
                        date +
                        '`; see: https://github.com/date-fns/date-fns/blob/master/docs/unicodeTokens.md',
                    );
                });

                test('warns if "YYY" token is used', () => {
                    try {
                        formatDate(date, 'YYY-MM-dd');
                    } catch (_) {
                        // Ignore the error
                    }
                    expect(warn).toBeCalledWith(
                        'Use `yyy` instead of `YYY` (in `YYY-MM-dd`) for formatting years to the input `' +
                        date +
                        '`; see: https://github.com/date-fns/date-fns/blob/master/docs/unicodeTokens.md',
                    );
                });

                test('warns if "YYYY" token is used', () => {
                    try {
                        formatDate(date, 'YYYY-MM-dd');
                    } catch (_) {
                        // Ignore the error
                    }
                    expect(warn).toBeCalledWith(
                        'Use `yyyy` instead of `YYYY` (in `YYYY-MM-dd`) for formatting years to the input `' +
                        date +
                        '`; see: https://github.com/date-fns/date-fns/blob/master/docs/unicodeTokens.md',
                    );
                });

                test('warns if "YYYYY" token is used', () => {
                    try {
                        formatDate(date, 'YYYYY-MM-dd');
                    } catch (_) {
                        // Ignore the error
                    }
                    expect(warn).toBeCalledWith(
                        'Use `yyyyy` instead of `YYYYY` (in `YYYYY-MM-dd`) for formatting years to the input `' +
                        date +
                        '`; see: https://github.com/date-fns/date-fns/blob/master/docs/unicodeTokens.md',
                    );
                });
            }); */
        });
    });
});
