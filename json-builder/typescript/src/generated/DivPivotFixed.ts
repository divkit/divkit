// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivSizeUnit,
} from './';

/**
 * Value of the offset of the coordinates of the axis of rotation.
 */
export class DivPivotFixed<T extends DivPivotFixedProps = DivPivotFixedProps> {
    readonly _props?: Exact<DivPivotFixedProps, T>;

    readonly type = 'pivot-fixed';
    /**
     * Unit of size measurement. To learn more about units of size measurement, see [Layout inside
     * the card](../../layout.dita).
     */
    unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Offset.
     */
    value?: Type<number> | DivExpression;

    constructor(props?: Exact<DivPivotFixedProps, T>) {
        this.unit = props?.unit;
        this.value = props?.value;
    }
}

export interface DivPivotFixedProps {
    /**
     * Unit of size measurement. To learn more about units of size measurement, see [Layout inside
     * the card](../../layout.dita).
     */
    unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Offset.
     */
    value?: Type<number> | DivExpression;
}
