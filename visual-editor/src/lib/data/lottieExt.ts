import type { DivExtension, DivExtensionContext, WrappedError } from '@divkitframework/divkit/typings/common';
import type { AnimationItem, AnimationDirection } from 'lottie-web';

interface Params {
    lottie_url?: string;
    lottie_json?: object;
    repeat_count?: number;
    repeat_mode?: 'restart' | 'reverse';
}

interface ScaleProps {
    attribute: string;
    noScale: boolean;
    hAlign: string;
    vAlign: string;
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

        this.alive = true;

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

        Promise.all([
            import('./lottieApi'),
            this.loadData()
        ]).then(([{ loadAnimation }, json]) => {
            if (!this.alive) {
                return;
            }

            const animItem = this.animItem = loadAnimation({
                container: wrapper,
                animationData: json,
                renderer: 'svg',
                loop: true,
                rendererSettings: {
                    preserveAspectRatio: scale.attribute
                }
            });
            this.setSvgScale(scale);
            this.animItem.addEventListener('data_failed', onError);
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
                            direction = direction === -1 ? 1 : -1;
                            animItem.setDirection(direction);
                        }
                        animItem.goToAndPlay(direction === 1 ? 0 : animItem.totalFrames, true);
                    }
                });
            }
        }).catch(onError);
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
        this.alive = false;
        this.animItem?.destroy();
        if (this.wrapper) {
            this.wrapper.parentNode?.removeChild(this.wrapper);
            this.wrapper = undefined;
        }
        node.removeAttribute('data-lottie');
    }
}
