// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivPercentageSize,
} from './';

/**
 * Процентное значение ширины страницы.
 */
export class DivPageSize<T extends DivPageSizeProps = DivPageSizeProps> {
    readonly _props?: Exact<DivPageSizeProps, T>;

    readonly type = 'percentage';
    /**
     * Ширина страницы в процентах от ширины родительского элемента.
     */
    page_width: Type<DivPercentageSize>;

    constructor(props: Exact<DivPageSizeProps, T>) {
        this.page_width = props.page_width;
    }
}

interface DivPageSizeProps {
    /**
     * Ширина страницы в процентах от ширины родительского элемента.
     */
    page_width: Type<DivPercentageSize>;
}
