// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    IDivPoint,
} from './';

/**
 * Element shadow.
 */
export interface IDivShadow {
    /**
     * Shadow transparency.
     */
    alpha?: Type<number | DivExpression>;
    /**
     * Blur intensity.
     */
    blur?: Type<number | DivExpression>;
    /**
     * Shadow color.
     */
    color?: Type<string | DivExpression>;
    /**
     * Shadow offset.
     */
    offset: Type<IDivPoint>;
}
