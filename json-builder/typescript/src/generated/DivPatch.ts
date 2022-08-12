// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    Div,
} from './';

/**
 * Редактирует элемент.
 */
export interface IDivPatch {
    /**
     * Изменения элементов.
     */
    changes: Type<NonEmptyArray<IDivPatchChange>>;
    /**
     * Порядок применения изменений:`transactional` — если во время применения хотя бы одного
     * элемента произошла ошибка, то изменения не применяются.`partial` — применяются все возможные
     * изменения. Если есть ошибки, то о них сообщается.
     */
    mode?: Type<DivPatchMode> | DivExpression;
}

export type DivPatchMode =
    | 'transactional'
    | 'partial';

/**
 * Описания пока нет
 */
export interface IDivPatchChange {
    /**
     * Идентификатор элемента для замены или удаления.
     */
    id: Type<string>;
    /**
     * Элементы для вставки. Если параметр не задан, то элемент будет удален.
     */
    items?: Type<NonEmptyArray<Div>>;
}

