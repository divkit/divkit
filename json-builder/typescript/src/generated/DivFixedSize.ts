// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivSizeUnit,
} from './';

/**
 * Fixed size of an element.
 */
export class DivFixedSize<T extends DivFixedSizeProps = DivFixedSizeProps> {
    readonly _props?: Exact<DivFixedSizeProps, T>;

    readonly type = 'fixed';
    /**
     * Unit of measurement. To learn more about units of size measurement, see [Layout inside the
     * card](../../layout.dita).
     */
    unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Element size.
     */
    value: Type<number> | DivExpression;

    constructor(props: Exact<DivFixedSizeProps, T>) {
        this.unit = props.unit;
        this.value = props.value;
    }
}

interface DivFixedSizeProps {
    /**
     * Unit of measurement. To learn more about units of size measurement, see [Layout inside the
     * card](../../layout.dita).
     */
    unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Element size.
     */
    value: Type<number> | DivExpression;
}
