/**
 * разбивает строку на токены.
 * Токен имеет тип Number, если его можно представить числом; или String в ином случае.
 * Символы '.' имеет специальное значение и поэтому выделяются в отдельные токены
 * @param {String} str строка для токенизации
 * @returns {Array}
 */
function tokenizeString(str: string): (string | number)[] {
    const tokens: (string | number)[] = [];

    str.replace(/(\d+)|\.|[^.\d]+/g, function(match: string, number: string) {
        if (number) {
            tokens.push(Number(number));
        } else {
            tokens.push(match);
        }
        return '';
    });

    return tokens;
}
/**
 * Compare versions
 * @param {String | Number} v1
 * @param {String | Number} v2
 * @returns {Number} Одно из 4 значений:
 *  v1  <  v2 => -1
 *  v1 === v2 =>  0
 *  v1  >  v2 =>  1
 *  v1 или v2 равен null или undefined => NaN
 */
export function compareVersions(v1: string | number | undefined, v2: string | number | undefined): number {
    if (v1 === v2) {
        return 0;
    }

    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    if (v1 === undefined || v2 === undefined ||
        // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
        v1 === null || v2 === null) {
        return NaN;
    }

    const v1parts = tokenizeString(String(v1));
    const v2parts = tokenizeString(String(v2));

    const len = Math.max(v1parts.length, v2parts.length);

    for (let i = 0; i < len; ++i) {
        let left = v1parts[i];
        let right = v2parts[i];

        // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
        if (left === undefined) {
            return -1;
        }
        // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
        if (right === undefined) {
            return 1;
        }

        /* если с одной стороны точка встретилась раньше - значит с этой стороны версия меньше */
        if (left === '.' && right !== '.') {
            return -1;
        }
        if (right === '.' && left !== '.') {
            return 1;
        }

        if (typeof left !== typeof right) {
            left = String(left);
            right = String(right);
        }
        if (left !== right) {
            return left > right ? 1 : -1;
        }
    }

    return 0;
}
