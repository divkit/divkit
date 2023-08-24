// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class WithDefault<T extends WithDefaultProps = WithDefaultProps> {
    readonly _props?: Exact<WithDefaultProps, T>;

    readonly type = 'default';

    constructor(props?: Exact<WithDefaultProps, T>) {
    }
}

export interface WithDefaultProps {
}
