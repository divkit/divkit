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

        private loadData(): Promise<object> {
            if (this.params.lottie_json) {
                return Promise.resolve(this.params.lottie_json);
            }

            const url = this.params.lottie_url;
            if (url) {
                return fetch(url)
                    .then(res => {
                        if (!res.ok) {
                            throw new Error('Response is not ok');
                        }

                        return res.json();
                    });
            }

            return Promise.reject('Missing data');
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
            node.setAttribute('data-lottie', 'true');

            // create wrapper for an animation, because "lottie-web" destroys container on "destroy" call,
            // and gif node itself cannot be used
            const wrapper = this.wrapper = document.createElement('div');
            this.wrapper.style.width = '100%';
            this.wrapper.style.height = '100%';
            node.appendChild(this.wrapper);

            const repeatCount = Number(this.params.repeat_count || -1);
            const onError = () => {
                this.animItem?.destroy();
                // reveal back gif contents
                children.forEach(element => {
                    element.style.display = '';
                });
                node.removeAttribute('data-lottie');
                if (this.wrapper) {
                    this.wrapper.parentNode?.removeChild(this.wrapper);
                    this.wrapper = undefined;
                }
                const err: WrappedError = new Error('Failed to load lottie animation') as WrappedError;
                err.level = 'error';
                err.additional = {
                    url: this.params.lottie_url
                };
                context.logError(err);
            };
            this.loadData().then(json => {
                const animItem = this.animItem = loadAnimation({
                    container: wrapper,
                    animationData: json,
                    renderer: 'svg',
                    loop: true
                });
                this.animItem.addEventListener('data_failed', onError);
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
            }).catch(onError);
        }

        unmountView(node: HTMLElement, _context: DivExtensionContext): void {
            this.animItem?.destroy();
            if (this.wrapper) {
                this.wrapper.parentNode?.removeChild(this.wrapper);
                this.wrapper = undefined;
            }
            node.removeAttribute('data-lottie');
        }
    };
}
