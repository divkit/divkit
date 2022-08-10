import type { DivJson } from '../../typings/common';
import { wrapError, WrappedError } from './wrapError';
import { TYPE_MAP } from '../components/typeMap';

/**
 * Simple check for json card
 * @param json
 * @returns Error string or null if everything fine
 */
export function simpleCheckInput(json?: Partial<DivJson>): WrappedError | null {
    if (!json) {
        return wrapError(new Error('Missing object'));
    }

    const card = json.card;
    const templates = json.templates || {};

    if (!card) {
        return wrapError(new Error('Missing card'));
    }

    if (!card.states || !card.states.length) {
        return wrapError(new Error('Missing states'));
    }

    for (const templateName in templates) {
        if (templates.hasOwnProperty(templateName)) {
            if (templateName in TYPE_MAP) {
                return wrapError(new Error('Template name collision'), {
                    additional: {
                        templateName
                    }
                });
            }
        }
    }

    for (let i = 0; i < card.states.length; ++i) {
        if (!card.states[i].div) {
            return wrapError(new Error('Missing state div'), {
                additional: {
                    stateId: card.states[i].state_id
                }
            });
        }
    }

    return null;
}
