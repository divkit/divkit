// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class WithoutDefault<T extends WithoutDefaultProps = WithoutDefaultProps> {
    readonly _props?: Exact<WithoutDefaultProps, T>;

    readonly type = 'non_default';

    constructor(props?: Exact<WithoutDefaultProps, T>) {
    }
}

export interface WithoutDefaultProps {
}
