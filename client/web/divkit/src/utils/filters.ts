import type { Filter } from '../types/filter';
import type { MaybeMissing } from '../expressions/json';
import { LogError, wrapError } from './wrapError';
import { isPositiveNumber } from './isPositiveNumber';
import { pxToEmWithUnits } from './pxToEm';

export function getCssFilter(filters: MaybeMissing<Filter>[], logError: LogError): string {
    return filters.map(filter => {
        if (filter?.type === 'blur') {
            if (isPositiveNumber(filter.radius)) {
                return `blur(${pxToEmWithUnits(filter.radius / 2)})`;
            }
        } else {
            logError(wrapError(new Error('Unknown filter'), {
                level: 'warn',
                additional: {
                    filter: filter?.type
                }
            }));
        }
    }).filter(Boolean).join(' ');
}
