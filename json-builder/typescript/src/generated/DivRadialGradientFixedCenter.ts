// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

import {
    DivSizeUnit,
} from './';

/**
 * Fixed central point of the gradient.
 */
export class DivRadialGradientFixedCenter<T extends DivRadialGradientFixedCenterProps = DivRadialGradientFixedCenterProps> {
    readonly _props?: Exact<DivRadialGradientFixedCenterProps, T>;

    readonly type = 'fixed';
    /**
     * Unit of measurement. To learn more about units of size measurement, see [Layout inside the
     * card](../../layout.dita).
     */
    unit?: Type<DivSizeUnit | DivExpression>;
    /**
     * Shift value of the fixed central point of the gradient.
     */
    value: Type<number | DivExpression>;

    constructor(props: Exact<DivRadialGradientFixedCenterProps, T>) {
        this.unit = props.unit;
        this.value = props.value;
    }
}

export interface DivRadialGradientFixedCenterProps {
    /**
     * Unit of measurement. To learn more about units of size measurement, see [Layout inside the
     * card](../../layout.dita).
     */
    unit?: Type<DivSizeUnit | DivExpression>;
    /**
     * Shift value of the fixed central point of the gradient.
     */
    value: Type<number | DivExpression>;
}
