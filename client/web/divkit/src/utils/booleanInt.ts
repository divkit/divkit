import type { BooleanInt } from '../../typings/common';

export function booleanInt(val: BooleanInt): boolean {
    return val === true || val === 1;
}
