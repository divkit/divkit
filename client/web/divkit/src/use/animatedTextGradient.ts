import type { MaybeMissing } from '../expressions/json';
import type { RadialBackground, RadialGradientCenter } from '../types/background';
import type { ResolvedTextGradient } from '../utils/textGradient';
import { pxToEm } from '../utils/pxToEm';
export const textGradientProperties = {
    offset: '--divkit-text-gradient-offset',
    radius: '--divkit-text-gradient-radius'
} as const;

type GradientAnimation = ResolvedTextGradient['animation'];

interface GradientAnimationOptions {
    animation: GradientAnimation;
    fontSize: number;
}

let offsetRegistered = false;

function registerGradientOffset(): boolean {
    if (typeof CSS === 'undefined' || !CSS.registerProperty) {
        return false;
    }
    if (!offsetRegistered) {
        try {
            CSS.registerProperty({
                name: textGradientProperties.offset,
                syntax: '<percentage>',
                inherits: false,
                initialValue: '0%'
            });
        } catch (error) {
            // Another copy of DivKit on the same page may have registered it already.
            if (!(error instanceof DOMException) || error.name !== 'InvalidModificationError') {
                return false;
            }
        }
        offsetRegistered = true;
    }
    return true;
}

function resolveCenter(center: MaybeMissing<RadialGradientCenter> | undefined, size: number, fontSize: number): number {
    if (center?.value === undefined) {
        return size / 2;
    }
    return center.type === 'fixed' ? parseFloat(pxToEm(center.value)) * fontSize : Number(center.value) * size;
}

function resolveRadius(gradient: MaybeMissing<RadialBackground>, node: HTMLElement): number {
    const fontSize = parseFloat(getComputedStyle(node).fontSize);
    if (gradient.radius?.type === 'fixed' && gradient.radius.value !== undefined) {
        return parseFloat(pxToEm(gradient.radius.value)) * fontSize;
    }

    const width = node.clientWidth;
    const height = node.clientHeight;
    const x = resolveCenter(gradient.center_x, width, fontSize);
    const y = resolveCenter(gradient.center_y, height, fontSize);
    const radius = gradient.radius?.value;
    const nearest = radius === 'nearest_corner' || radius === 'nearest_side';
    const horizontal = nearest ?
        Math.min(Math.abs(x), Math.abs(width - x)) : Math.max(Math.abs(x), Math.abs(width - x));
    const vertical = nearest ?
        Math.min(Math.abs(y), Math.abs(height - y)) : Math.max(Math.abs(y), Math.abs(height - y));
    if (radius === 'nearest_side') {
        return Math.min(horizontal, vertical);
    }
    if (radius === 'farthest_side') {
        return Math.max(horizontal, vertical);
    }
    return Math.hypot(horizontal, vertical);
}

class TextGradientAnimation {
    private resizeObserver?: ResizeObserver;
    private animation?: GradientAnimation;
    private frame?: number;
    private startedAt = 0;
    private motionPreference?: MediaQueryList;

    constructor(private node: HTMLElement, options: GradientAnimationOptions) {
        this.update(options);
    }

    update({ animation }: GradientAnimationOptions): void {
        this.destroy();
        this.animation = animation;
        if (!animation) {
            return;
        }
        if (animation.radialGradient) {
            if (typeof ResizeObserver === 'undefined') {
                return;
            }
            this.updateRadius();
            this.resizeObserver = new ResizeObserver(() => this.updateRadius());
            this.resizeObserver.observe(this.node);
        }
        if (registerGradientOffset()) {
            this.node.setAttribute('data-gradient-animation', 'css');
        } else {
            this.motionPreference = matchMedia('(prefers-reduced-motion: no-preference)');
            // addListener also works in Safari versions without MediaQueryList.addEventListener.
            this.motionPreference.addListener(this.restartFallback);
            this.restartFallback();
        }
    }

    private restartFallback = (): void => {
        this.stopFallback();
        if (!this.motionPreference?.matches) {
            return;
        }
        this.startedAt = performance.now();
        this.node.style.setProperty(textGradientProperties.offset, '-100%');
        this.node.setAttribute('data-gradient-animation', 'js');
        this.frame = requestAnimationFrame(this.tick);
    };

    private tick = (now: number): void => {
        if (!this.animation) {
            return;
        }
        const progress = ((now - this.startedAt) % this.animation.duration) / this.animation.duration;
        this.node.style.setProperty(textGradientProperties.offset, `${progress * 200 - 100}%`);
        this.frame = requestAnimationFrame(this.tick);
    };

    private stopFallback(): void {
        if (this.frame !== undefined) {
            cancelAnimationFrame(this.frame);
            this.frame = undefined;
        }
        this.node.removeAttribute('data-gradient-animation');
        this.node.style.removeProperty(textGradientProperties.offset);
    }

    private updateRadius(): void {
        const gradient = this.animation?.radialGradient;
        if (gradient) {
            // Relative radii belong to the unshifted gradient, not its animated center.
            this.node.style.setProperty(textGradientProperties.radius, `${resolveRadius(gradient, this.node)}px`);
        }
    }

    destroy(): void {
        this.stopFallback();
        this.motionPreference?.removeListener(this.restartFallback);
        this.motionPreference = undefined;
        this.resizeObserver?.disconnect();
        this.resizeObserver = undefined;
        this.animation = undefined;
        this.node.style.removeProperty(textGradientProperties.radius);
    }
}

export function animateTextGradient(
    node: HTMLElement,
    options: GradientAnimationOptions
): Pick<TextGradientAnimation, 'update' | 'destroy'> {
    const animation = new TextGradientAnimation(node, options);
    return {
        update: nextOptions => animation.update(nextOptions),
        destroy: () => animation.destroy()
    };
}
