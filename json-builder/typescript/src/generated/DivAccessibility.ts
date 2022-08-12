// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Доступность для людей с ограниченными возможностями.
 */
export interface IDivAccessibility {
    /**
     * Описание элемента. Используется в качестве основного описания для программ экранного чтения.
     */
    description?: Type<string> | DivExpression;
    /**
     * Подсказка, что произойдет при взаимодействии. Если на iOS в настройках VoiceOver включен Speak
     * Hints, то подсказка воспроизводится после `description`.
     */
    hint?: Type<string> | DivExpression;
    /**
     * Способ организации дерева accessibility. В режиме `merge` accessibility-сервис воспринимает
     * элемент вместе с поддеревом как одно целое. В режиме `exclude` элемент вместе с поддеревом
     * недоступен для accessibility.
     */
    mode?: Type<DivAccessibilityMode> | DivExpression;
    /**
     * Заглушает звук чтения экрана после взаимодействия с элементом.
     */
    mute_after_action?: Type<IntBoolean> | DivExpression;
    /**
     * Описание текущего состояния элемента. Например, в описании можно указать выбранную дату для
     * элемента выбора даты, а для переключателя — состояние включен/выключен.
     */
    state_description?: Type<string> | DivExpression;
    /**
     * Роль элемента. Используется для правильной идентификации элемента accessibility-сервисом.
     */
    type?: Type<DivAccessibilityType>;
}

export type DivAccessibilityMode =
    | 'default'
    | 'merge'
    | 'exclude';

export type DivAccessibilityType =
    | 'none'
    | 'button'
    | 'image'
    | 'text'
    | 'edit_text'
    | 'header'
    | 'tab_bar';

