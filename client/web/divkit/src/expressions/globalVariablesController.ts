import type { Writable } from 'svelte/store';
import { writable } from 'svelte/store';
import type { Variable } from './variable';

const controllerToStore = new Map<GlobalVariablesController, Writable<string>>();
const controllerToVars = new Map<GlobalVariablesController, Map<string, Variable>>();

export function getControllerStore(controller: GlobalVariablesController): Writable<string> {
    const store = controllerToStore.get(controller) || writable('');

    if (!controllerToStore.has(controller)) {
        controllerToStore.set(controller, store);
    }

    return store;
}

export function getControllerVars(controller: GlobalVariablesController): Map<string, Variable> {
    const map = controllerToVars.get(controller) || new Map();

    if (!controllerToVars.has(controller)) {
        controllerToVars.set(controller, map);
    }

    return map;
}

export class GlobalVariablesController {
    setVariable(variable: Variable): void {
        const name = variable.getName();
        const vars = getControllerVars(this);

        if (vars.has(name)) {
            throw new Error('Variable with the same name already exist');
        } else {
            vars.set(name, variable);
            const store = getControllerStore(this);
            store.set(name);
        }
    }

    getVariable(variableName: string): Variable | undefined {
        const vars = getControllerVars(this);

        return vars.get(variableName);
    }
}

export function createGlobalVariablesController() {
    return new GlobalVariablesController();
}
