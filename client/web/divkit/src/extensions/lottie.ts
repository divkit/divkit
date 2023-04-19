import type { DivExtension, DivExtensionContext } from '../../typings/common';
import type { WrappedError } from '../utils/wrapError';
import { filterHTMLElements } from '../utils/filterHTMLElements';

interface Params {
    lottie_url?: string;
    lottie_json?: object;
    repeat_count?: number;
    repeat_mode?: 'restart' | 'reverse';
}

interface AnimationItem {
    totalFrames: number;

    addEventListener(type: string, cb: () => void): void;
    destroy(): void;
    play(name?: string): void;
    stop(name?: string): void;
    pause(name?: string): void;
    setDirection(direction: number): void;
    goToAndStop(value: number, isFrame?: boolean, name?: string): void;
    goToAndPlay(value: number, isFrame?: boolean, name?: string): void;
}

interface LoadAnimationParamsWidthPath {
    container: HTMLElement;
    path: string;
    renderer: 'svg' | 'html' | 'canvas';
    loop: boolean | number | undefined;
}

interface LoadAnimationParamsWidthData {
    container: HTMLElement;
    animationData: any;
    renderer: 'svg' | 'html' | 'canvas';
    loop: boolean | number | undefined;
}

type LoadAnimation = (opts: LoadAnimationParamsWidthPath | LoadAnimationParamsWidthData) => AnimationItem;

export function lottieExtensionBuilder(loadAnimation: LoadAnimation) {
    return class Lottie implements DivExtension {
        private params: Params;
        private animItem: AnimationItem | undefined;
        private wrapper: HTMLElement | undefined;

        constructor(params: Params) {
            this.params = params;
        }

        mountView(node: HTMLElement, context: DivExtensionContext): void {
            if (!this.params.lottie_url && !this.params.lottie_json) {
                return;
            }

            const children: HTMLElement[] = Array.from(node.children).filter(filterHTMLElements);
            // hide gif contents before load, so they would not blink after load
            children.forEach(element => {
                element.style.display = 'none';
            });

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
                let direction = 1;
                let count = 0;
                animItem.addEventListener('loopComplete', () => {
                    ++count;
                    if (repeatCount !== -1 && count === repeatCount) {
                        animItem.stop();
                        animItem.goToAndStop(animItem.totalFrames, true);
                    } else {
                        if (this.params.repeat_mode === 'reverse') {
                            direction *= -1;
                            animItem.setDirection(direction);
                        }
                        animItem.goToAndPlay(direction === 1 ? 0 : animItem.totalFrames, true);
                    }
                });
            }
        }

        unmountView(_node: HTMLElement, _context: DivExtensionContext): void {
            this.animItem?.destroy();
            if (this.wrapper) {
                this.wrapper.parentNode?.removeChild(this.wrapper);
                this.wrapper = undefined;
            }
        }
    };
}
