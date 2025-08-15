import { tick } from 'svelte';
import type { ComponentContext } from '../types/componentContext';
import type { WrappedError } from '../../typings/common';

const MAX_ITERATIONS = 8;

export const layoutProvider = (
    currentNode: HTMLElement,
    componentContext: ComponentContext,
    widthVariable: string | undefined,
    heightVariable: string | undefined
): ResizeObserver | undefined => {
    let observer : ResizeObserver | undefined;

    if ((widthVariable || heightVariable) && typeof ResizeObserver !== 'undefined') {
        observer = new ResizeObserver(async() => {
            let counter = 0;
            const sizeHistory: Record<string, Set<number>> = {};

            const setVariable = (variableName: string | undefined, value: number): boolean => {
                if (variableName) {
                    const instance = componentContext.getVariable(variableName, 'integer');
                    if (instance) {
                        value = Math.round(value);

                        if (!sizeHistory[variableName]) {
                            sizeHistory[variableName] = new Set();
                        }
                        if (!sizeHistory[variableName].has(value)) {
                            instance.setValue(value);
                            sizeHistory[variableName].add(value);
                            return true;
                        }
                    } else {
                        const err: WrappedError = new Error('Missing variable') as WrappedError;
                        err.level = 'error';
                        err.additional = {
                            variableName
                        };
                        componentContext.logError(err);
                    }
                }

                return false;
            };

            const recalcProps = () => {
                if (!currentNode) {
                    return false;
                }

                const bbox = currentNode.getBoundingClientRect();
                const widthRes = setVariable(widthVariable, bbox.width);
                const heightRes = setVariable(heightVariable, bbox.height);

                return widthRes || heightRes;
            };

            while (recalcProps()) {
                if (++counter > MAX_ITERATIONS) {
                    const err: WrappedError = new Error('Recursive layout in size_provider') as WrappedError;
                    err.level = 'warn';
                    err.additional = {
                        widthVariableName: widthVariable,
                        heightVariableName: heightVariable
                    };
                    componentContext.logError(err);
                    break;
                }
                await tick();
            }
        });
        observer.observe(currentNode);
    }

    return observer;
};
