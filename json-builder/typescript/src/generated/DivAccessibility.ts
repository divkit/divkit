// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Accessibility for disabled people.
 */
export interface IDivAccessibility {
    /**
     * Element description. It is used as the main description for screen reading applications.
     */
    description?: Type<string> | DivExpression;
    /**
     * A tooltip of what will happen during interaction. If Speak Hints is enabled in the VoiceOver
     * settings on iOS, a tooltip is played after `description`.
     */
    hint?: Type<string> | DivExpression;
    /**
     * The way the accessibility tree is organized. In the `merge` mode the accessibility service
     * perceives an element together with a subtree as a whole. In the `exclude` mode an element
     * together with a subtree isn't available for accessibility.
     */
    mode?: Type<DivAccessibilityMode> | DivExpression;
    /**
     * Mutes the sound of the screen reader after interacting with the element.
     */
    mute_after_action?: Type<IntBoolean> | DivExpression;
    /**
     * Description of the current state of an element. For example, in the description you can
     * specify a selected date for a date selection element and an on/off state for a switch.
     */
    state_description?: Type<string> | DivExpression;
    /**
     * Element role. It is used for correct identification of an element by an accessibility service.
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
