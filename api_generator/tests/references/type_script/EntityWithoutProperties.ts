// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithoutProperties<T extends EntityWithoutPropertiesProps = EntityWithoutPropertiesProps> {
    readonly _props?: Exact<EntityWithoutPropertiesProps, T>;

    readonly type = 'entity_without_properties';

    constructor(props?: Exact<EntityWithoutPropertiesProps, T>) {
    }
}

export interface EntityWithoutPropertiesProps {
}
