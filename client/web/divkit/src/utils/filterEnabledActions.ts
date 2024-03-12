import type { Action, DisappearAction, VisibilityAction } from '../../typings/common';
import type { MaybeMissing } from '../expressions/json';
import { correctBooleanInt } from './correctBooleanInt';
import { LogError } from './wrapError';

export function filterEnabledActions(
    action: MaybeMissing<Action | VisibilityAction | DisappearAction>,
    logError: LogError
): boolean {
    return correctBooleanInt(action.is_enabled, true, logError);
}
