import type { ActionCopyToClipboard, WrappedError } from '../../typings/common';
import type { MaybeMissing } from '../expressions/json';
import { wrapError } from '../utils/wrapError';

export function copyToClipboard(
    logError: (error: WrappedError) => void,
    actionTyped: MaybeMissing<ActionCopyToClipboard>
): void {
    if (!(
        actionTyped.content && (actionTyped.content.type === 'text' || actionTyped.content.type === 'url') &&
        typeof actionTyped.content.value === 'string'
    )) {
        logError(wrapError(new Error('Incorrect action'), {
            additional: {
                action: actionTyped
            }
        }));
        return;
    }

    if (!(
        typeof navigator !== 'undefined' &&
        'clipboard' in navigator &&
        navigator.clipboard &&
        'write' in navigator.clipboard &&
        typeof navigator.clipboard.writeText === 'function'
    )) {
        logError(wrapError(new Error('Clipboard is unavailable'), {
            additional: {
                action: actionTyped
            }
        }));
        return;
    }

    navigator.clipboard.writeText(actionTyped.content.value).catch(err => {
        logError(wrapError(new Error('Failed to copy to the clipboard'), {
            additional: {
                originalError: String(err)
            }
        }));
    });
}
