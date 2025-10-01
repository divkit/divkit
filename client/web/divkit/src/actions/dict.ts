import type { ActionDictSetValue, WrappedError } from '../../typings/common';
import type { Variable } from '../../typings/variables';
import type { MaybeMissing } from '../expressions/json';
import { convertTypedValue } from '../expressions/utils';
import type { ComponentContext } from '../types/componentContext';
import { wrapError } from '../utils/wrapError';

export function dictSetValue(
    componentContext: ComponentContext | undefined,
    variables: Map<string, Variable>,
    logError: (error: WrappedError) => void,
    actionTyped: MaybeMissing<ActionDictSetValue>
): void {
    const { variable_name: name, key, value } = actionTyped;

    if (typeof key !== 'string') {
        logError(wrapError(new Error('Incorrect dict_set_value action'), {
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

    if (value && !value.type) {
        logError(wrapError(new Error('Incorrect value type'), {
            additional: {
                name
            }
        }));
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
    if (type === 'dict') {
        const dict = variableInstance.getValue() as Record<string, unknown>;
        const newDict = { ...dict };
        if (value) {
            newDict[key] = convertTypedValue(value);
        } else {
            delete newDict[key];
        }
        variableInstance.setValue(newDict);
    } else {
        logError(wrapError(new Error('Trying to set value into the non-dict'), {
            additional: {
                name,
                type
            }
        }));
    }
}
