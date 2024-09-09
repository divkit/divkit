import type { DivExtension, DivExtensionContext, WrappedError } from '@divkitframework/divkit/typings/common';
import type { AnimationItem, AnimationDirection } from 'lottie-web';

interface Params {
    lottie_url?: string;
    lottie_json?: object;
    repeat_count?: number;
    repeat_mode?: 'restart' | 'reverse';
}

function filterHTMLElements(it: Node): it is HTMLElement {
    return it instanceof HTMLElement;
}

export class Lottie implements DivExtension {
    private params: Params;
    private animItem: AnimationItem | undefined;
    private wrapper: HTMLElement | undefined;
    private alive: boolean;

    constructor(params: Params) {
        this.params = params;
        this.alive = false;
    }

    mountView(node: HTMLElement, context: DivExtensionContext): void {
        if (!this.params.lottie_url && !this.params.lottie_json) {
            return;
        }

        this.alive = true;

        const children: HTMLElement[] = Array.from(node.children).filter(filterHTMLElements);
        // hide gif contents before load, so they would not blink after load
        children.forEach(element => {
            element.style.display = 'none';
        });
        node.setAttribute('data-lottie', 'true');

        import('./lottieApi').then(({ loadAnimation }) => {
            if (!this.alive) {
                return;
            }

            // create wrapper for an animation, because "lottie-web" destroys container on "destroy" call,
            // and gif node itself cannot be used
            this.wrapper = document.createElement('div');
            this.wrapper.style.width = '100%';
            this.wrapper.style.height = '100%';
            node.appendChild(this.wrapper);

            const repeatCount = Number(this.params.repeat_count || -1);
            const animItem = this.animItem = loadAnimation({
                container: this.wrapper,
                path: this.params.lottie_url,
                animationData: this.params.lottie_json,
                renderer: 'svg',
                loop: true
            });
            this.animItem.addEventListener('data_failed', () => {
                this.animItem?.destroy();
                // reveal back gif contents
                children.forEach(element => {
                    element.style.display = '';
                });
                const err: WrappedError = new Error('Failed to load lottie animation') as WrappedError;
                err.level = 'error';
                err.additional = {
                    url: this.params.lottie_url
                };
                context.logError(err);
            });
            if (this.params.repeat_mode === 'reverse' || repeatCount !== -1) {
                let direction: AnimationDirection = 1;
                let count = 0;
                animItem.addEventListener('loopComplete', () => {
                    ++count;
                    if (repeatCount !== -1 && count === repeatCount) {
                        animItem.stop();
                        animItem.goToAndStop(animItem.totalFrames, true);
                    } else {
                        if (this.params.repeat_mode === 'reverse') {
                            direction = direction === 1 ? -1 : 1;
                            animItem.setDirection(direction);
                        }
                        animItem.goToAndPlay(direction === 1 ? 0 : animItem.totalFrames, true);
                    }
                });
            }
        });
    }

    unmountView(node: HTMLElement, _context: DivExtensionContext): void {
        this.alive = false;
        this.animItem?.destroy();
        if (this.wrapper) {
            this.wrapper.parentNode?.removeChild(this.wrapper);
            this.wrapper = undefined;
        }
        node.removeAttribute('data-lottie');
    }
}
