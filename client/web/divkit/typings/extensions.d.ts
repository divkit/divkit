import type { DivExtension } from './common';

export class SizeProvider implements DivExtension {}

export class Gesture implements DivExtension {}

declare class Lottie implements DivExtension {}

declare class Markdown implements DivExtension {}

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
    play(): void;
    pause(): void;
    stop(): void;
    setDirection(direction: number): void;
    goToAndStop(value: number, isFrame?: boolean): void;
    goToAndPlay(value: number, isFrame?: boolean): void;
}

interface LoadAnimationParamsWidthPath {
    container: HTMLElement;
    path: string;
    renderer: 'svg' | 'html' | 'canvas';
    loop: boolean | number | undefined;
    autoplay?: boolean;
    rendererSettings: {
        preserveAspectRatio: string;
    };
}

interface LoadAnimationParamsWidthData {
    container: HTMLElement;
    animationData: any;
    renderer: 'svg' | 'html' | 'canvas';
    loop: boolean | number | undefined;
    autoplay?: boolean;
    rendererSettings: {
        preserveAspectRatio: string;
    };
}

type LoadAnimation = (opts: LoadAnimationParamsWidthPath | LoadAnimationParamsWidthData) => AnimationItem;

interface LottiePlayer {
    loadAnimation: LoadAnimation;
}

export interface LottieOptions {
    /**
     * Loads the lottie_url file in place of fetch and response.json(), and resolves with the animation for the player.
     * Inline lottie_json bypasses the loader. The signal aborts once lottie_url changes or the view is gone,
     * and a result after that is dropped. The player changes the animation it gets, so every call resolves
     * with a new object, not one cached for the URL
     */
    loadAnimationData?(url: string, opts: { signal?: AbortSignal }): Promise<object>;
}

interface LazyLottiePlayer {
    /**
     * Loads the player. Called for every animation, so it should load the player only once, as a dynamic import does
     */
    loadPlayer(): Promise<LottiePlayer>;
}

export function lottieExtensionBuilder(
    player: LoadAnimation | LazyLottiePlayer,
    options?: LottieOptions
): typeof Lottie;

export type MarkdownProcessor = (markdown: string) => string;

export interface MarkdownOptions {
    cssClass?: string;
}

export function markdownExtensionBuilder(process: MarkdownProcessor, options?: MarkdownOptions): typeof Markdown;
