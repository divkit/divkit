import { tick } from 'svelte';
import type { Action, DivExtension, DivExtensionContext } from '../../typings/common';
import type { WrappedError } from '../utils/wrapError';

interface Params {
    swipe_up?: Action[];
    swipe_down?: Action[];
    swipe_left?: Action[];
    swipe_right?: Action[];
}

const MIN_SWIPE_DIST = 8;

export class Gesture implements DivExtension {
    private context: DivExtensionContext | undefined;
    private params: Params;
    private startCoords?: {
        pageX: number;
        pageY: number;
    };

    constructor(params: Params) {
        this.params = params;

        this.onPointerDown = this.onPointerDown.bind(this);
        this.onPointerMove = this.onPointerMove.bind(this);
        this.onPointerUp = this.onPointerUp.bind(this);
    }

    private processActions(type: keyof Params): void {
        const actions = this.params[type];

        if (Array.isArray(actions) && actions.length && this.context) {
            const processed = this.context.processExpressions(actions);
            processed.forEach(action => {
                this.context?.execAction(action);
            });
        }
    }

    private onPointerDown(event: PointerEvent): void {
        this.startCoords = {
            pageX: event.pageX,
            pageY: event.pageY
        };
    }

    private onPointerMove(event: PointerEvent): void {
        if (!this.startCoords) {
            return;
        }

        const diffX = event.pageX - this.startCoords.pageX;
        const diffY = event.pageY - this.startCoords.pageY;

        if (Math.abs(diffX) > MIN_SWIPE_DIST || Math.abs(diffY) > MIN_SWIPE_DIST) {
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (diffX > MIN_SWIPE_DIST) {
                    this.processActions('swipe_right');
                } else if (diffX < -MIN_SWIPE_DIST) {
                    this.processActions('swipe_left');
                }
            } else {
                // eslint-disable-next-line no-lonely-if
                if (diffY > MIN_SWIPE_DIST) {
                    this.processActions('swipe_down');
                } else if (diffY < -MIN_SWIPE_DIST) {
                    this.processActions('swipe_up');
                }
            }

            this.startCoords = undefined;
        }
    }

    private onPointerUp(): void {
        this.startCoords = undefined;
    }

    mountView(node: HTMLElement, context: DivExtensionContext): void {
        this.context = context;

        node.addEventListener('pointerdown', this.onPointerDown);
        node.addEventListener('pointermove', this.onPointerMove);
        node.addEventListener('pointerup', this.onPointerUp);
        node.addEventListener('pointercancel', this.onPointerUp);
        node.style.pointerEvents = 'auto';
    }

    updateView(node: HTMLElement): void {
        node.style.pointerEvents = 'auto';
    }

    unmountView(node: HTMLElement, _context: DivExtensionContext): void {
        node.removeEventListener('pointerdown', this.onPointerDown);
        node.removeEventListener('pointermove', this.onPointerMove);
        node.removeEventListener('pointerup', this.onPointerUp);
        node.removeEventListener('pointercancel', this.onPointerUp);
        node.style.pointerEvents = '';
    }
}
