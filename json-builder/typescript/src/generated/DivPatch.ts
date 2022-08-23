// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    Div,
} from './';

/**
 * Edits the element.
 */
export interface IDivPatch {
    /**
     * Element changes.
     */
    changes: Type<NonEmptyArray<IDivPatchChange>>;
    /**
     * Procedure for applying changes:`transactional` — if an error occurs during application of at
     * least one element, the changes aren't applied.`partial` — all possible changes are applied. If
     * there are errors, they are reported.
     */
    mode?: Type<DivPatchMode> | DivExpression;
}

export type DivPatchMode =
    | 'transactional'
    | 'partial';

export interface IDivPatchChange {
    /**
     * ID of an element to be replaced or removed.
     */
    id: Type<string>;
    /**
     * Elements to be inserted. If the parameter isn't specified, the element will be removed.
     */
    items?: Type<NonEmptyArray<Div>>;
}
