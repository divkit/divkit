// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Size with a fixed aspect ratio. It counts height from width and ignores other specified height
 * values.
 */
export interface IDivAspect {
    /**
     * `ratio = width / height`.
     */
    ratio: Type<number | DivExpression>;
}
