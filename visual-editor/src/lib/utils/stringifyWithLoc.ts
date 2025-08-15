/* modified https://tc39.es/ecma262/multipage/structured-data.html#sec-json.stringify */

const MAX_GAP_LENGTH = 10;

export interface Loc {
    line: number;
    column: number;
    offset: number;
}

export interface Range {
    start: Loc;
    end: Loc;
}

export type Replacer = (key: string, value: unknown) => void | unknown;

export type OnLoc = (value: unknown, range: Range) => void;

interface State {
    loc: Loc;
    replacer: Replacer | null | undefined,
    stack: object[],
    indent: string;
    gap: string;
    propertyList: string[] | undefined;
    onLoc: OnLoc | undefined;
}

export function stringifyWithLoc(
    value: unknown,
    replacer?: null | Replacer,
    space?: string | number,
    onLoc?: OnLoc
): string {
    let gap = '';

    if (typeof space === 'number') {
        const len = Math.min(MAX_GAP_LENGTH, space);
        for (let i = 0; i < len; ++i) {
            gap += ' ';
        }
    } else if (typeof space === 'string') {
        gap = space.substring(0, MAX_GAP_LENGTH);
    }

    return serializeProperty({
        loc: {
            line: 0,
            column: 0,
            offset: 0
        },
        replacer,
        stack: [],
        indent: '',
        gap,
        propertyList: undefined,
        onLoc
    }, '', {
        '': value
    }) || '';
}

function serializeProperty(state: State, key: string, holder: Record<string, unknown> | unknown[]): string | undefined {
    let value = holder[key as keyof typeof holder];

    if (typeof value === 'object' && value !== null || typeof value === 'bigint') {
        const toJSON = (value as {
            toJSON(key: string): string;
        }).toJSON;
        if (typeof toJSON === 'function') {
            value = (value as {
                toJSON(key: string): string;
            }).toJSON(key);
        }
    }

    if (state.replacer) {
        value = state.replacer.call(holder, key, value);
    }

    let res: string | undefined;
    if (value === null) {
        res = 'null';
    } else if (value === true) {
        res = 'true';
    } else if (value === false) {
        res = 'false';
    } else if (typeof value === 'string') {
        res = JSON.stringify(value);
    } else if (typeof value === 'number') {
        if (Number.isFinite(value)) {
            res = String(value);
        } else {
            res = 'null';
        }
    }

    if (typeof res === 'string') {
        shiftLoc(state.loc, res);
        return res;
    }

    if (typeof value === 'bigint') {
        throw new TypeError('Do not know how to serialize a BigInt');
    }
    if (typeof value === 'object' && value !== null) {
        if (Array.isArray(value)) {
            return serializeArray(state, value);
        }
        return serializeObject(state, value as Record<string, unknown>);
    }
}

function countSubstring(str: string, substring: string): number {
    let index = -1;
    let count = 0;

    while (index < str.length - substring.length) {
        const found = str.indexOf(substring, index);

        if (found === -1) {
            return count;
        }
        index = found + 1;
        ++count;
    }

    return 0;
}

function shiftLoc(loc: Loc, str: string): void {
    const newLines = countSubstring(str, '\n');

    if (newLines > 0) {
        const lastSymbols = str.length - str.lastIndexOf('\n') - 1;

        loc.line += newLines;
        loc.column = lastSymbols;
    } else {
        loc.column += str.length;
    }
    loc.offset += str.length;
}

function serializeArray(state: State, value: unknown[]): string {
    if (state.stack.includes(value)) {
        throw new TypeError('Converting circular structure to JSON');
    }

    state.stack.push(value);

    const stepback = state.indent;

    state.indent += state.gap;

    let final = '[';
    if (value.length && state.gap) {
        final += '\n' + state.indent;
    }
    shiftLoc(state.loc, final);

    for (let index = 0, len = value.length; index < len; ++index) {
        const locBefore = { ...state.loc };
        const strP = serializeProperty(state, String(index), value);
        let str: string;

        if (strP === undefined) {
            str = 'null';
            state.loc = locBefore;
            shiftLoc(state.loc, str);
        } else {
            str = strP;
        }

        final += str;

        if (index + 1 < len) {
            let spacer = ',';
            if (state.gap) {
                spacer += '\n' + state.indent;
            }
            final += spacer;
            shiftLoc(state.loc, spacer);
        }
    }

    let end = '';
    if (value.length && state.gap) {
        end += '\n' + stepback;
    }
    end += ']';

    final += end;
    shiftLoc(state.loc, end);

    state.stack.pop();
    state.indent = stepback;

    return final;
}

function serializeObject(state: State, value: Record<string, unknown>): string {
    if (state.stack.includes(value)) {
        throw new TypeError('Converting circular structure to JSON');
    }

    state.stack.push(value);

    const stepback = state.indent;
    const startLoc = { ...state.loc };

    state.indent += state.gap;

    const keys = state.propertyList || Object.keys(value);

    let final = '{';
    shiftLoc(state.loc, final);

    const space = '\n' + state.indent;
    if (state.gap) {
        final += space;
        shiftLoc(state.loc, space);
    }

    let stepbackFinal = final;
    const stepbackLoc = { ...state.loc };
    let hasProps = false;

    keys.forEach(prop => {
        const locBefore = { ...state.loc };
        const finalBefore = final;

        let member = JSON.stringify(prop);
        member += ':';
        if (state.gap) {
            member += ' ';
        }

        final += member;
        shiftLoc(state.loc, member);

        const strP = serializeProperty(state, prop, value);

        if (strP !== undefined) {
            hasProps = true;

            final += strP;

            stepbackFinal = final;
            stepbackLoc.line = state.loc.line;
            stepbackLoc.column = state.loc.column;
            stepbackLoc.offset = state.loc.offset;

            let delimeter = ',';
            if (state.gap) {
                delimeter += '\n' + state.indent;
            }
            final += delimeter;
            shiftLoc(state.loc, delimeter);
        } else {
            state.loc = locBefore;
            final = finalBefore;
        }
    });

    if (hasProps) {
        final = stepbackFinal;
        state.loc = stepbackLoc;

        if (state.gap) {
            const space = '\n' + stepback;
            final += space;
            shiftLoc(state.loc, space);
        }

        final += '}';
        shiftLoc(state.loc, '}');
    } else {
        final = '{}';
        state.loc = { ...startLoc };
        shiftLoc(state.loc, final);
    }

    state.stack.pop();
    state.indent = stepback;

    if (state.onLoc) {
        state.onLoc(value, {
            start: startLoc,
            end: { ...state.loc }
        });
    }

    return final;
}
