import type { ActionUpdateStructure, WrappedError } from '../../typings/common';
import type { Variable } from '../../typings/variables';
import type { MaybeMissing } from '../expressions/json';
import type { ComponentContext } from '../types/componentContext';
import { wrapError } from '../utils/wrapError';

export function updateStructure(
    componentContext: ComponentContext | undefined,
    variables: Map<string, Variable>,
    logError: (error: WrappedError) => void,
    actionTyped: MaybeMissing<ActionUpdateStructure>
): void {
    const { variable_name: name, path, value } = actionTyped;

    if (!value?.value) {
        logError(wrapError(new Error('Missing value for an action'), {
            additional: {
                name
            }
        }));
        return;
    }

    if (typeof path !== 'string' || !path || path.charAt(0) === '/' || path.charAt(path.length - 1) === '/') {
        logError(wrapError(new Error(`Value '${path}' for key 'path' is not valid`), {
            additional: {
                name
            }
        }));
        return;
    }

    if (!name) {
        logError(wrapError(new Error(`Incorrect ${actionTyped.type} action`), {
            additional: {
                name
            }
        }));
        return;
    }

    const variableInstance = componentContext?.getVariable(name) || variables.get(name);

    if (!variableInstance) {
        logError(wrapError(new Error('Cannot find variable'), {
            additional: {
                name
            }
        }));
        return;
    }

    const type = variableInstance.getType();
    if (type === 'dict' || type === 'array') {
        const obj = variableInstance.getValue() as Record<string, unknown>;
        const processed = path.replace(/\/+/g, '/');
        if (processed === '/') {
            logError(wrapError(new Error(`Value '${path}' for key 'path' is not valid`), {
                additional: {
                    name,
                    type,
                    path
                }
            }));
            return;
        }
        const parts = processed.split('/');
        const newObj = type === 'array' ? (obj as unknown as unknown[]).slice() : { ...obj };
        let temp: any = newObj;
        for (let i = 0; i < parts.length; ++i) {
            const part = parts[i];

            if (!part) {
                logError(wrapError(new Error('Path is empty'), {
                    additional: {
                        name,
                        type,
                        path
                    }
                }));
                return;
            }

            if (!temp || typeof temp !== 'object') {
                logError(wrapError(new Error(`Element with path '${parts.slice(0, i).join('/')}' is not ${temp === undefined ? 'found' : 'a structure'}`), {
                    additional: {
                        name,
                        type,
                        path
                    }
                }));
                return;
            }
            if (Array.isArray(temp)) {
                const int = Number(part);
                if (Number.isNaN(int)) {
                    logError(wrapError(new Error(`Unable to use '${part}' as array index`), {
                        additional: {
                            name,
                            type,
                            path
                        }
                    }));
                    return;
                }
                if (i + 1 === parts.length && (int < 0 || int > temp.length)) {
                    logError(wrapError(new Error(`Position '${int}' is out of array bounds`), {
                        additional: {
                            name,
                            type,
                            path
                        }
                    }));
                    return;
                }
            }

            if (i + 1 < parts.length) {
                temp = temp[part];
            }
        }

        temp[parts[parts.length - 1]] = value.value;
        variableInstance.setValue(newObj);
    } else {
        logError(wrapError(new Error('Action requires array or dictionary variable'), {
            additional: {
                name,
                type
            }
        }));
    }
}
