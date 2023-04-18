import { tick } from 'svelte';
import type { DivExtension, DivExtensionContext } from '../../typings/common';
import type { WrappedError } from '../utils/wrapError';

interface Params {
    width_variable_name?: string;
    height_variable_name?: string;
}

const MAX_ITERATIONS = 8;

export class SizeProvider implements DivExtension {
    private widthVariableName: string | undefined;
    private heightVariableName: string | undefined;
    private resizeObserver: ResizeObserver | undefined;
    private context: DivExtensionContext | undefined;
    private node: HTMLElement | undefined;
    private sizeHistory: Record<string, Set<number>> = {};

    constructor(params: Params) {
        this.widthVariableName = params.width_variable_name;
        this.heightVariableName = params.height_variable_name;
    }

    private setVariable(variableName: string | undefined, value: number): boolean {
        if (!this.context) {
            return false;
        }

        if (variableName) {
            const instance = this.context.variables.get(variableName);
            if (instance && instance.getType() === 'integer') {
                value = Math.round(value);

                if (!this.sizeHistory[variableName]) {
                    this.sizeHistory[variableName] = new Set();
                }
                if (!this.sizeHistory[variableName].has(value)) {
                    instance.setValue(value);
                    this.sizeHistory[variableName].add(value);
                    return true;
                }
            } else {
                const err: WrappedError = new Error('Missing variable') as WrappedError;
                err.level = 'error';
                err.additional = {
                    variableName
                };
                this.context.logError(err);
            }
        }

        return false;
    }

    private recalcProps(): boolean {
        if (!this.node || !this.context) {
            return false;
        }

        const bbox = this.node.getBoundingClientRect();
        const widthRes = this.setVariable(this.widthVariableName, bbox.width);
        const heightRes = this.setVariable(this.heightVariableName, bbox.height);

        return widthRes || heightRes;
    }

    mountView(node: HTMLElement, context: DivExtensionContext): void {
        this.node = node;
        this.context = context;
        if (!this.resizeObserver && typeof ResizeObserver !== 'undefined') {
            this.resizeObserver = new ResizeObserver(async() => {
                let counter = 0;
                while (this.recalcProps()) {
                    if (++counter > MAX_ITERATIONS) {
                        const err: WrappedError = new Error('Recursive layout in size_provider') as WrappedError;
                        err.level = 'warn';
                        err.additional = {
                            widthVariableName: this.widthVariableName,
                            heightVariableName: this.heightVariableName
                        };
                        context.logError(err);
                        break;
                    }
                    await tick();
                }
                this.sizeHistory = {};
            });
        }
        this.resizeObserver?.observe(node);
        this.recalcProps();
    }

    unmountView(_node: HTMLElement, _context: DivExtensionContext): void {
        this.resizeObserver?.disconnect();
        this.resizeObserver = undefined;
    }
}
