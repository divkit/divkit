import type { BooleanInt, DivExtension, DivExtensionContext, Unsubscriber } from '../../typings/common';
import type { WrappedError } from '../utils/wrapError';
import { filterHTMLElements } from '../utils/filterHTMLElements';

interface Params {
    lottie_url?: string;
    lottie_json?: object;
    repeat_count?: number;
    repeat_mode?: 'restart' | 'reverse';
    is_playing?: BooleanInt;
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

interface LoadAnimationParamsBase {
    container: HTMLElement;
    renderer: 'svg' | 'html' | 'canvas';
    loop: boolean | number | undefined;
    autoplay?: boolean;
    rendererSettings: {
        preserveAspectRatio: string;
    };
}

interface LoadAnimationParamsWidthPath extends LoadAnimationParamsBase {
    path: string;
}

interface LoadAnimationParamsWidthData extends LoadAnimationParamsBase {
    animationData: any;
}

type LoadAnimation = (opts: LoadAnimationParamsWidthPath | LoadAnimationParamsWidthData) => AnimationItem;

interface LottiePlayer {
    loadAnimation: LoadAnimation;
}

interface LazyLottiePlayer {
    // Called for every animation, so it should load the player only once, as a dynamic import does
    loadPlayer(): Promise<LottiePlayer>;
}

interface ScaleProps {
    attribute: string;
    noScale: boolean;
    hAlign: string;
    vAlign: string;
}

// An element its parents fold to no area stays intersecting, and the observer reports
// a change only when the share crosses a threshold, so the threshold is above zero
const ON_SCREEN_SHARE = 0.01;

type ScreenListener = (isOnScreen: boolean) => void;

export function lottieExtensionBuilder(player: LoadAnimation | LazyLottiePlayer) {
    // A lazy player loads together with the animation file. One that throws at once fails
    // the animation as one that fails to load
    const loadPlayer = typeof player === 'function' ?
        () => Promise.resolve(player) :
        () => Promise.resolve()
            .then(() => player.loadPlayer())
            .then(it => it.loadAnimation.bind(it));

    // All the animations share one observer: one callback for all the views that change at once
    let screenObserver: IntersectionObserver | undefined;
    const screenListeners = new Map<Element, ScreenListener>();

    function watchScreen(node: Element, listener: ScreenListener): void {
        if (typeof IntersectionObserver === 'undefined') {
            return;
        }
        // A later entry of the same view is the newer one
        screenObserver ||= new IntersectionObserver(entries => {
            entries.forEach(entry => {
                screenListeners.get(entry.target)?.(entry.intersectionRatio >= ON_SCREEN_SHARE);
            });
        }, { threshold: ON_SCREEN_SHARE });
        screenListeners.set(node, listener);
        screenObserver.observe(node);
    }

    function unwatchScreen(node: Element): void {
        if (!screenListeners.delete(node)) {
            return;
        }
        screenObserver?.unobserve(node);
        if (!screenListeners.size) {
            screenObserver?.disconnect();
            screenObserver = undefined;
        }
    }

    return class Lottie implements DivExtension {
        private params: Params;
        private animItem: AnimationItem | undefined;
        private wrapper: HTMLElement | undefined;
        private isPlayingUnsubscriber: Unsubscriber | undefined;
        private isPlaying = true;
        private actionPlaybackState: 'started' | 'stopped' | undefined;
        private unsubscribe: (() => void) | undefined;
        // A newer lottie_url or the unmount makes the previous load stale
        private loadNumber = 0;
        private loadAbort: AbortController | undefined;
        private isOnScreen = true;
        // A player may not tell its animation has played to the end, so the extension marks it
        private isFinished = false;

        constructor(params: Params) {
            this.params = params;
        }

        // An extension action decides over is_playing whether the animation plays, and one played
        // to its end plays only once is_playing or a start action plays it again
        private shouldPlay(): boolean {
            if (this.isFinished) {
                return false;
            }
            return this.actionPlaybackState ? this.actionPlaybackState === 'started' : this.isPlaying;
        }

        // As on Android, an animation that is not seen pauses and goes on once seen again. Here it
        // is not seen with almost none of it on screen. Whether it goes on comes from its state then
        private setOnScreen(isOnScreen: boolean): void {
            if (this.isOnScreen === isOnScreen) {
                return;
            }
            this.isOnScreen = isOnScreen;

            if (!this.animItem) {
                return;
            }
            if (!isOnScreen) {
                this.animItem.pause();
            } else if (this.shouldPlay()) {
                this.animItem.play();
            }
        }

        private startLoad(): number {
            this.loadAbort?.abort();
            this.loadAbort = typeof AbortController === 'undefined' ? undefined : new AbortController();
            return ++this.loadNumber;
        }

        private stopLoad(): void {
            this.loadAbort?.abort();
            this.loadAbort = undefined;
            ++this.loadNumber;
        }

        private loadData(url: string | undefined, signal: AbortSignal | undefined): Promise<object> {
            if (this.params.lottie_json) {
                return Promise.resolve(this.params.lottie_json);
            }

            if (url) {
                return fetch(url, { signal })
                    .then(res => {
                        if (!res.ok) {
                            throw new Error('Response is not ok');
                        }

                        return res.json();
                    });
            }

            return Promise.reject('Missing data');
        }

        private getRatio(context: DivExtensionContext): number | undefined {
            const ratio = (context.getComponentProperty('aspect') as {
                ratio: number;
            } | undefined)?.ratio;

            if (typeof ratio === 'number' && ratio > 0) {
                return ratio;
            }

            return undefined;
        }

        private getScale(context: DivExtensionContext): ScaleProps {
            const scale = context.getComponentProperty('scale') as string | undefined;

            if (scale === 'stretch') {
                return {
                    attribute: 'none',
                    noScale: false,
                    hAlign: 'center',
                    vAlign: 'center'
                };
            }

            let hAlign = context.getComponentProperty('content_alignment_horizontal') as string | undefined;
            let vAlign = context.getComponentProperty('content_alignment_vertical') as string | undefined;
            let hVal = 'Mid';
            let vVal = 'Mid';

            if (hAlign === 'start') {
                hAlign = context.direction === 'ltr' ? 'start' : 'end';
            } else if (hAlign === 'end') {
                hAlign = context.direction === 'ltr' ? 'end' : 'start';
            } else if (hAlign === 'left') {
                hAlign = 'start';
            } else if (hAlign === 'right') {
                hAlign = 'end';
            } else {
                hAlign = 'center';
            }

            if (vAlign === 'top') {
                vAlign = 'start';
            } else if (vAlign === 'bottom') {
                vAlign = 'end';
            } else {
                vAlign = 'center';
            }

            if (scale === 'no_scale') {
                return {
                    attribute: 'xMidYMid meet',
                    noScale: true,
                    hAlign,
                    vAlign
                };
            }

            if (hAlign === 'start') {
                hVal = 'Min';
            } else if (hAlign === 'end') {
                hVal = 'Max';
            }

            if (vAlign === 'start') {
                vVal = 'Min';
            } else if (vAlign === 'end') {
                vVal = 'Max';
            }

            if (scale === 'fit' || scale === 'no_scale') {
                return {
                    attribute: `x${hVal}Y${vVal} meet`,
                    noScale: false,
                    hAlign,
                    vAlign
                };
            }

            return {
                attribute: `x${hVal}Y${vVal} slice`,
                noScale: false,
                hAlign,
                vAlign
            };
        }

        private getSvg(): SVGElement | undefined {
            const svg = this.wrapper?.firstElementChild;
            if (svg instanceof SVGElement) {
                return svg;
            }
        }

        private setWrapperScale(scale: ScaleProps): void {
            if (!this.wrapper) {
                return;
            }

            if (scale.noScale) {
                this.wrapper.style.display = 'flex';
                this.wrapper.style.alignItems = scale.vAlign;
                this.wrapper.style.justifyContent = scale.hAlign;
            } else {
                this.wrapper.style.display = '';
                this.wrapper.style.alignItems = '';
                this.wrapper.style.justifyContent = '';
            }
        }

        private setSvgScale(scale: ScaleProps): void {
            const svg = this.getSvg();
            if (!svg) {
                return;
            }

            if (scale.noScale) {
                svg.style.flex = '0 0 auto';
                svg.style.width = '';
                svg.style.height = '';
            } else {
                svg.style.flex = '';
                svg.style.width = '100%';
                svg.style.height = '100%';
            }
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
            const ratio = this.getRatio(context);
            const scale = this.getScale(context);
            if (ratio) {
                this.wrapper.style.aspectRatio = String(ratio);
            }
            this.setWrapperScale(scale);
            node.appendChild(this.wrapper);

            watchScreen(node, isOnScreen => this.setOnScreen(isOnScreen));

            const repeatCount = Number(context.processExpressions(this.params.repeat_count) ?? -1);
            const repeatMode = context.processExpressions(this.params.repeat_mode);
            const onError = () => {
                this.animItem?.destroy();
                this.animItem = undefined;
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

            this.unsubscribe = context.derviedExpression(this.params.lottie_url).subscribe(url => {
                const loadNumber = this.startLoad();
                const loading = Promise.all([this.loadData(url, this.loadAbort?.signal), loadPlayer()]);
                loading.then(([json, loadAnimation]) => {
                    if (loadNumber !== this.loadNumber) {
                        return;
                    }

                    this.animItem?.destroy();

                    const loop = repeatCount !== 0;
                    this.isFinished = false;
                    const animItem = this.animItem = loadAnimation({
                        container: wrapper,
                        animationData: json,
                        renderer: 'svg',
                        loop,
                        // With autoplay lottie-web starts the animation itself once its fonts
                        // load, past a pause asked for meanwhile, so the extension starts it.
                        // Before that the animation does not move, and a pause holds
                        autoplay: false,
                        rendererSettings: {
                            preserveAspectRatio: scale.attribute
                        }
                    });
                    if (this.actionPlaybackState === 'started') {
                        if (this.isOnScreen) {
                            animItem.goToAndPlay(0, true);
                        }
                    } else if (this.actionPlaybackState === 'stopped') {
                        animItem.goToAndStop(0, true);
                    } else if (this.isPlaying && this.isOnScreen) {
                        animItem.play();
                    }
                    this.setSvgScale(scale);
                    this.animItem.addEventListener('data_failed', onError);
                    // Without a loop the player ends the animation itself
                    animItem.addEventListener('complete', () => {
                        this.isFinished = true;
                    });
                    if (loop && (repeatMode === 'reverse' || repeatCount !== -1)) {
                        let direction = 1;
                        let count = 0;
                        animItem.addEventListener('loopComplete', () => {
                            ++count;
                            if (repeatCount !== -1 && count === repeatCount + 1) {
                                this.isFinished = true;
                                animItem.stop();
                                animItem.goToAndStop(animItem.totalFrames, true);
                            } else {
                                if (repeatMode === 'reverse') {
                                    direction *= -1;
                                    animItem.setDirection(direction);
                                }
                                animItem.goToAndPlay(direction === 1 ? 0 : animItem.totalFrames, true);
                            }
                        });
                    }
                }).catch(() => {
                    if (loadNumber === this.loadNumber) {
                        onError();
                    }
                });
            });

            this.isPlayingUnsubscriber = context.derviedExpression(this.params.is_playing).subscribe(val => {
                this.isPlaying = val !== false;
                if (!this.animItem || this.actionPlaybackState) {
                    return;
                }
                if (this.isPlaying) {
                    this.isFinished = false;
                }
                // Off screen the animation waits to be seen
                if (this.isOnScreen) {
                    this.animItem[this.isPlaying ? 'play' : 'pause']();
                }
            });
        }

        handleAction(payload: Record<string, unknown> | undefined): boolean {
            if (payload?.type === 'start') {
                this.actionPlaybackState = 'started';
                this.isFinished = false;
                if (this.isOnScreen) {
                    this.animItem?.goToAndPlay(0, true);
                } else {
                    // Not seen, it starts from the first frame once it is
                    this.animItem?.goToAndStop(0, true);
                }
                return true;
            }
            if (payload?.type === 'stop') {
                this.actionPlaybackState = 'stopped';
                this.animItem?.goToAndStop(0, true);
                return true;
            }
            return false;
        }

        updateView(_node: HTMLElement, context: DivExtensionContext): void {
            if (!this.wrapper) {
                return;
            }

            const ratio = this.getRatio(context);
            if (ratio) {
                this.wrapper.style.aspectRatio = String(ratio);
            }
            const svg = this.getSvg();
            if (svg) {
                const scale = this.getScale(context);
                this.setWrapperScale(scale);
                this.setSvgScale(scale);
                svg.setAttribute('preserveAspectRatio', scale.attribute);
            }
        }

        unmountView(node: HTMLElement, _context: DivExtensionContext): void {
            this.stopLoad();
            unwatchScreen(node);
            this.animItem?.destroy();
            this.animItem = undefined;
            if (this.wrapper) {
                this.wrapper.parentNode?.removeChild(this.wrapper);
                this.wrapper = undefined;
            }
            const children: HTMLElement[] = Array.from(node.children).filter(filterHTMLElements);
            children.forEach(element => {
                element.style.display = '';
            });
            node.removeAttribute('data-lottie');

            this.unsubscribe?.();
            this.isPlayingUnsubscriber?.();
        }
    };
}
