import type { Action, DisappearAction, VisibilityAction } from '../../typings/common';
import type { MaybeMissing } from '../expressions/json';

export function filterEnabledActions(action: MaybeMissing<Action | VisibilityAction | DisappearAction>): boolean {
    return action.is_enabled !== 0 && action.is_enabled !== false;
}
