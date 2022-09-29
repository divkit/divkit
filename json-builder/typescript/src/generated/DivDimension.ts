// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

import {
    DivSizeUnit,
} from './';

/**
 * Element dimension value.
 */
export interface IDivDimension {
    unit?: Type<DivSizeUnit | DivExpression>;
    /**
     * Value.
     */
    value: Type<number | DivExpression>;
}
