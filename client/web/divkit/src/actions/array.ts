import type { ActionArrayInsertValue, ActionArrayRemoveValue, ActionArraySetValue, WrappedError } from '../../typings/common';
import type { ArrayVariable, Variable } from '../../typings/variables';
import type { MaybeMissing } from '../expressions/json';
import { wrapError } from '../utils/wrapError';

export function arrayInsert(
    variables: Map<string, Variable>,
    logError: (error: WrappedError) => void,
    actionTyped: MaybeMissing<ActionArrayInsertValue>
): void {
    const { variable_name: name, index, value } = actionTyped;

    if (!value || typeof index !== 'number' && index !== undefined) {
        logError(wrapError(new Error('Incorrect array_insert_value action'), {
            additional: {
                name
            }
        }));
        return;
    }

    handle(variables, logError, actionTyped, variableInstance => {
        const list = variableInstance.getValue();
        if (typeof index === 'number' && (index < 0 || index > list.length)) {
            logError(wrapError(new Error(`Index out of bound for mutation ${actionTyped.type}`), {
                additional: {
                    name,
                    index,
                    length: list.length
                }
            }));
        } else {
            const newList = list.slice();
            if (typeof index === 'number') {
                newList.splice(index, 0, value.value);
            } else {
                newList.push(value.value);
            }
            variableInstance.setValue(newList);
        }
    });
}

export function arrayRemove(
    variables: Map<string, Variable>,
    logError: (error: WrappedError) => void,
    actionTyped: MaybeMissing<ActionArrayRemoveValue>
): void {
    const { variable_name: name, index } = actionTyped;

    if (typeof index !== 'number') {
        logError(wrapError(new Error('Incorrect array_remove_value action'), {
            additional: {
                name
            }
        }));
        return;
    }

    handle(variables, logError, actionTyped, variableInstance => {
        const list = variableInstance.getValue();
        if (typeof index === 'number' && (index < 0 || index >= list.length)) {
            logError(wrapError(new Error(`Index out of bound for mutation ${actionTyped.type}`), {
                additional: {
                    name,
                    index,
                    length: list.length
                }
            }));
        } else {
            const newList = list.slice();
            newList.splice(index, 1);
            variableInstance.setValue(newList);
        }
    });
}

export function arraySet(
    variables: Map<string, Variable>,
    logError: (error: WrappedError) => void,
    actionTyped: MaybeMissing<ActionArraySetValue>
): void {
    const { variable_name: name, index, value } = actionTyped;

    if (!value || typeof index !== 'number') {
        logError(wrapError(new Error('Incorrect array_set_value action'), {
            additional: {
                name
            }
        }));
        return;
    }

    handle(variables, logError, actionTyped, variableInstance => {
        const list = variableInstance.getValue();
        if (typeof index === 'number' && (index < 0 || index >= list.length)) {
            logError(wrapError(new Error(`Index out of bound for mutation ${actionTyped.type}`), {
                additional: {
                    name,
                    index,
                    length: list.length
                }
            }));
        } else {
            const newList = list.slice();
            newList[index] = value.value;
            variableInstance.setValue(newList);
        }
    });
}

function handle(
    variables: Map<string, Variable>,
    logError: (error: WrappedError) => void,
    actionTyped: MaybeMissing<ActionArrayRemoveValue | ActionArrayInsertValue | ActionArraySetValue>,
    cb: (variableInstance: ArrayVariable) => void
): void {
    const { variable_name: name } = actionTyped;

    if (!name) {
        logError(wrapError(new Error(`Incorrect ${actionTyped.type} action`), {
            additional: {
                name
            }
        }));
        return;
    }

    const variableInstance = variables.get(name);

    if (!variableInstance) {
        logError(wrapError(new Error('Cannot find variable'), {
            additional: {
                name
            }
        }));
        return;
    }

    const type = variableInstance.getType();
    if (type === 'array') {
        cb(variableInstance as ArrayVariable);
    } else {
        logError(wrapError(new Error('Trying to insert value into the non-array'), {
            additional: {
                name,
                type
            }
        }));
    }
}
