import type { MaybeMissing } from '../expressions/json';
import type { AnimatedTextGradient, StaticTextGradient, TextGradient } from '../types/text';
import type { RadialBackground } from '../types/background';
import { getAnimatedGradientBackground, getBackground } from './background';

const DEFAULT_ANIMATION_DURATION = 1600;

function resolveAnimationDuration(duration: number | undefined): number {
    if (duration === undefined) {
        return DEFAULT_ANIMATION_DURATION;
    }

    return Number.isFinite(duration) && duration >= 0 ? duration : DEFAULT_ANIMATION_DURATION;
}

export interface ResolvedTextGradient {
    backgroundImage: string;
    animation?: {
        duration: number;
        backgroundImage: string;
        radialGradient?: MaybeMissing<RadialBackground>;
    };
}

export function resolveTextGradient(
    textGradient: MaybeMissing<TextGradient> | undefined,
    direction: 'ltr' | 'rtl'
): ResolvedTextGradient | undefined {
    const animatedTextGradient = textGradient?.type === 'animated' ?
        textGradient as MaybeMissing<AnimatedTextGradient> :
        undefined;
    const staticTextGradient = animatedTextGradient ?
        animatedTextGradient.gradient :
        textGradient as MaybeMissing<StaticTextGradient> | undefined;

    if (staticTextGradient?.type !== 'gradient' && staticTextGradient?.type !== 'radial_gradient') {
        return undefined;
    }

    const backgroundImage = getBackground([staticTextGradient], direction).image;
    if (!backgroundImage) {
        return undefined;
    }

    const animationDuration = animatedTextGradient ?
        resolveAnimationDuration(animatedTextGradient.duration) :
        undefined;

    if (animationDuration === undefined || animationDuration === 0) {
        return { backgroundImage };
    }

    const animatedBackgroundImage = getAnimatedGradientBackground(staticTextGradient);
    if (!animatedBackgroundImage) {
        return { backgroundImage };
    }

    return {
        backgroundImage,
        animation: {
            duration: animationDuration,
            backgroundImage: animatedBackgroundImage,
            radialGradient: staticTextGradient.type === 'radial_gradient' ? staticTextGradient : undefined
        }
    };
}
