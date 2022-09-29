// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

import {
    DivPercentageSize,
} from './';

/**
 * Percentage value of the page width.
 */
export class DivPageSize<T extends DivPageSizeProps = DivPageSizeProps> {
    readonly _props?: Exact<DivPageSizeProps, T>;

    readonly type = 'percentage';
    /**
     * Page width as a percentage of the parent element width.
     */
    page_width: Type<DivPercentageSize>;

    constructor(props: Exact<DivPageSizeProps, T>) {
        this.page_width = props.page_width;
    }
}

export interface DivPageSizeProps {
    /**
     * Page width as a percentage of the parent element width.
     */
    page_width: Type<DivPercentageSize>;
}
