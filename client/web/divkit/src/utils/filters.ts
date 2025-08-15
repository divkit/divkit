import type { Filter } from '../types/filter';
import type { MaybeMissing } from '../expressions/json';
import { type LogError, wrapError } from './wrapError';
import { isPositiveNumber } from './isPositiveNumber';
import { pxToEmWithUnits } from './pxToEm';

export function getCssFilter(
    filters: MaybeMissing<Filter>[],
    logError: LogError
): string {
    return filters.map(filter => {
        if (!filter) {
            logError(wrapError(new Error('Incorrect filter'), {
                level: 'warn'
            }));
            return;
        }
        if (filter.type === 'blur') {
            if (isPositiveNumber(filter.radius)) {
                return `blur(${pxToEmWithUnits(filter.radius / 2)})`;
            }
        } else if (filter.type === 'rtl_mirror') {
            // processed in component
            return;
        } else {
            logError(wrapError(new Error('Unknown filter'), {
                level: 'warn',
                additional: {
                    filter: filter.type
                }
            }));
        }
    }).filter(Boolean).join(' ');
}
