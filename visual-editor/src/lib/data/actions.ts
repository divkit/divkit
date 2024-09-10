import type { ActionArg, ActionDesc } from '../../lib';
import type { DivAction } from '../../types/divjson';
import { decline } from '../utils/decline';

export function actionsPreview(actions: DivAction[], actionsStr: string[], noUrlStr: string): string {
    if (!Array.isArray(actions) || !actions.length) {
        return actionsStr[3];
    }

    if (actions.length > 1) {
        return decline(actions.length, actionsStr).replace('%s', String(actions.length));
    }

    return actions[0].url || noUrlStr;
}

export interface ArgResult {
    desc: ActionArg;
    value: string;
}

export function parseAction(url: string | undefined, customActions: ActionDesc[]): {
    type: string;
    url?: string;
    desc?: ActionDesc;
    args?: ArgResult[];
} {
    const obj: Record<string, string> = {};

    if (!url) {
        return {
            type: 'url',
            url: ''
        };
    }

    try {
        const parsed = new URL(url);
        const params = new URLSearchParams(parsed.search);
        for (const key of params.keys()) {
            obj[key] = params.get(key) as string;
        }
    } catch (err) {
        return {
            type: 'url',
            url
        };
    }

    for (let i = 0; i < customActions.length; ++i) {
        const actionDesc = customActions[i];
        if (url.startsWith(actionDesc.baseUrl)) {
            const args: ArgResult[] = [];

            if (actionDesc.args) {
                for (const desc of actionDesc.args) {
                    args.push({
                        desc,
                        value: obj[desc.name] || ''
                    });
                }
            }

            return {
                type: `custom:${i}`,
                desc: actionDesc,
                url,
                args
            };
        }
    }

    return {
        type: 'url',
        url
    };
}
