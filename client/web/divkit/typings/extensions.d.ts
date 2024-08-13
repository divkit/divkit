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
    stop(): void;
    setDirection(direction: number): void;
    goToAndPlay(value: number, isFrame?: boolean): void;
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

export function lottieExtensionBuilder(loadAnimation: LoadAnimation): typeof Lottie;

export type MarkdownProcessor = (markdown: string) => string;

export interface MarkdownOptions {
    cssClass?: string;
}

export function markdownExtensionBuilder(process: MarkdownProcessor, options?: MarkdownOptions): typeof Markdown;
