// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest';
import { tick } from 'svelte';
import { render } from '../../src/client';
import { lottieExtensionBuilder } from '../../src/extensions/lottie';
import type { DivkitInstance, DivVariable, TypedValue, WrappedError } from '../../typings/common';

type LoadAnimation = Extract<Parameters<typeof lottieExtensionBuilder>[0], (...args: never[]) => unknown>;
type LoadAnimationOptions = Parameters<LoadAnimation>[0];

interface FakeAnimation {
    options: LoadAnimationOptions;
    isPaused: boolean;
    destroyed: boolean;
}

interface LoadingAnimation extends FakeAnimation {
    finishLoading(): void;
}

interface PendingFile {
    url: string;
    resolve(data: object): void;
}

let instance: DivkitInstance | undefined;
let target: HTMLElement;
let errors: WrappedError[];
let animations: FakeAnimation[];
let files: PendingFile[];

// Plays like lottie-web: without autoplay set the animation starts right away
const loadAnimation = vi.fn<LoadAnimation>(options => {
    const animation = {
        options,
        isPaused: (options as { autoplay?: boolean }).autoplay === false,
        destroyed: false,
        totalFrames: 10,
        addEventListener: vi.fn(),
        destroy() {
            animation.destroyed = true;
        },
        play() {
            animation.isPaused = false;
        },
        pause() {
            animation.isPaused = true;
        },
        stop() {
            animation.isPaused = true;
        },
        setDirection: vi.fn(),
        goToAndStop() {
            animation.isPaused = true;
        },
        goToAndPlay() {
            animation.isPaused = false;
        }
    };
    animations.push(animation);
    return animation;
});

let loadingAnimations: LoadingAnimation[];

// Plays like lottie-web with fonts to wait for: the animation does not move until it loads,
// and with autoplay it plays once loaded, whatever was asked of it meanwhile
const loadAnimationWithFonts = vi.fn<LoadAnimation>(options => {
    const animation = {
        options,
        isPaused: true,
        destroyed: false,
        totalFrames: 10,
        addEventListener: vi.fn(),
        destroy() {
            animation.destroyed = true;
        },
        play() {
            animation.isPaused = false;
        },
        pause() {
            animation.isPaused = true;
        },
        stop() {
            animation.isPaused = true;
        },
        setDirection: vi.fn(),
        goToAndStop() {
            animation.isPaused = true;
        },
        goToAndPlay() {
            animation.isPaused = false;
        },
        finishLoading() {
            if ((options as { autoplay?: boolean }).autoplay !== false) {
                animation.play();
            }
        }
    };
    loadingAnimations.push(animation);
    return animation;
});

beforeEach(() => {
    errors = [];
    animations = [];
    loadingAnimations = [];
    files = [];
    loadAnimation.mockClear();
    loadAnimationWithFonts.mockClear();
    target = document.createElement('div');
    document.body.append(target);
    // Every file comes when the test resolves it
    vi.stubGlobal('fetch', vi.fn((url: string) => new Promise(resolve => {
        files.push({
            url,
            resolve(data) {
                resolve({ ok: true, json: () => Promise.resolve(data) });
            }
        });
    })));
});

afterEach(() => {
    instance?.$destroy();
    instance = undefined;
    target.remove();
    vi.unstubAllGlobals();
});

function renderLottie(
    variables: DivVariable[],
    params: Record<string, unknown>,
    extension = lottieExtensionBuilder(loadAnimation)
): void {
    instance = render({
        target,
        id: 'lottie',
        json: { card: { log_id: 'test', variables, states: [{ state_id: 0, div: {
            type: 'gif',
            id: 'animation',
            gif_url: 'https://example.com/preview.gif',
            width: { type: 'fixed', value: 100 },
            height: { type: 'fixed', value: 100 },
            extensions: [{ id: 'lottie', params }]
        } }] } },
        extensions: new Map([['lottie', extension]]),
        onError({ error }) {
            errors.push(error);
        }
    });
}

function setVariable(name: string, value: TypedValue): void {
    instance?.execAction({ log_id: `set_${name}`, typed: { type: 'set_variable', variable_name: name, value } });
}

function extensionAction(type: 'start' | 'stop'): void {
    instance?.execAction({
        log_id: type,
        typed: { type: 'extension_action', div_id: 'animation', extension_id: 'lottie' },
        payload: { type }
    });
}

interface FakeObserver {
    callback: IntersectionObserverCallback;
    options: IntersectionObserverInit | undefined;
    targets: Element[];
    disconnected: boolean;
}

/** The screen as IntersectionObserver reports it: the share of each observed element on screen. */
function observeScreen() {
    const observers: FakeObserver[] = [];
    vi.stubGlobal('IntersectionObserver', class {
        observer: FakeObserver;
        constructor(callback: IntersectionObserverCallback, options?: IntersectionObserverInit) {
            this.observer = { callback, options, targets: [], disconnected: false };
            observers.push(this.observer);
        }
        observe(target: Element) {
            this.observer.targets.push(target);
        }
        unobserve(target: Element) {
            this.observer.targets = this.observer.targets.filter(it => it !== target);
        }
        disconnect() {
            this.observer.disconnected = true;
        }
    });
    return {
        observers,
        // A banner folded by scroll stays intersecting with no area
        report(share: number, isIntersecting = share > 0) {
            for (const observer of observers.filter(it => !it.disconnected)) {
                const entries = observer.targets.map(target => ({ target, intersectionRatio: share, isIntersecting }));
                observer.callback(entries as unknown as IntersectionObserverEntry[], {} as IntersectionObserver);
            }
        },
        reportFor(target: Element, share: number) {
            for (const observer of observers.filter(it => !it.disconnected && it.targets.includes(target))) {
                const entries = [{ target, intersectionRatio: share, isIntersecting: share > 0 }];
                observer.callback(entries as unknown as IntersectionObserverEntry[], {} as IntersectionObserver);
            }
        }
    };
}

function renderGifs(
    into: HTMLElement,
    urls: string[],
    extension: ReturnType<typeof lottieExtensionBuilder>
): DivkitInstance {
    return render({
        target: into,
        id: `lottie-${urls.join('-')}`,
        json: { card: { log_id: 'test', states: [{ state_id: 0, div: {
            type: 'container',
            items: urls.map(lottie_url => ({
                type: 'gif',
                gif_url: 'https://example.com/preview.gif',
                width: { type: 'fixed', value: 100 },
                height: { type: 'fixed', value: 100 },
                extensions: [{ id: 'lottie', params: { lottie_url } }]
            }))
        } }] } },
        extensions: new Map([['lottie', extension]])
    });
}

/** A player whose animations do not tell they are paused, with what the extension asks of them. */
function playerWithoutIsPaused() {
    const calls: string[] = [];
    const listeners: Record<string, () => void> = {};
    const extension = lottieExtensionBuilder(() => ({
        totalFrames: 10,
        addEventListener(type: string, cb: () => void) {
            listeners[type] = cb;
        },
        destroy: vi.fn(),
        play: () => calls.push('play'),
        pause: () => calls.push('pause'),
        stop: () => calls.push('stop'),
        setDirection: vi.fn(),
        goToAndStop: () => calls.push('stop at a frame'),
        goToAndPlay: () => calls.push('play from a frame')
    }));
    return { calls, listeners, extension };
}

async function settle(): Promise<void> {
    // The extension mounts after a tick, the file and its json come in a few more
    for (let i = 0; i < 10; ++i) {
        await tick();
    }
}

describe('Lottie extension', () => {
    test('an animation with is_playing false is created paused and starts once is_playing is true', async() => {
        // Arrange
        renderLottie([{ name: 'playing', type: 'boolean', value: false }], {
            lottie_url: 'https://example.com/animation.json',
            is_playing: '@{playing}'
        });
        await settle();
        files[0].resolve({ frames: 'first' });
        await settle();

        // Act
        const pausedAtStart = animations[0]?.isPaused;
        setVariable('playing', { type: 'boolean', value: true });
        await settle();

        // Assert
        expect(animations).toHaveLength(1);
        expect(pausedAtStart).toBe(true);
        expect(animations[0].isPaused).toBe(false);
        expect(errors).toEqual([]);
    });

    test('a file that comes after the view is gone makes no animation', async() => {
        // Arrange
        renderLottie([], { lottie_url: 'https://example.com/animation.json' });
        await settle();

        // Act
        instance?.$destroy();
        instance = undefined;
        files[0].resolve({ frames: 'late' });
        await settle();

        // Assert
        expect(files).toHaveLength(1);
        expect(loadAnimation).not.toHaveBeenCalled();
        expect(errors).toEqual([]);
    });

    test('only the file of the current lottie_url makes the animation', async() => {
        // Arrange
        renderLottie([{ name: 'url', type: 'string', value: 'https://example.com/first.json' }], {
            lottie_url: '@{url}'
        });
        await settle();
        setVariable('url', { type: 'string', value: 'https://example.com/second.json' });
        await settle();

        // Act: the first file comes after the second one
        files[1].resolve({ frames: 'second' });
        await settle();
        files[0].resolve({ frames: 'first' });
        await settle();

        // Assert
        expect(files.map(file => file.url)).toEqual([
            'https://example.com/first.json',
            'https://example.com/second.json'
        ]);
        expect(animations.map(animation => animation.options)).toMatchObject([{ animationData: { frames: 'second' } }]);
        expect(animations[0].destroyed).toBe(false);
        expect(errors).toEqual([]);
    });

    test('an animation off screen is paused and goes on back on screen', async() => {
        // Arrange
        const screen = observeScreen();
        renderLottie([], { lottie_url: 'https://example.com/animation.json' });
        await settle();
        screen.report(1);
        files[0].resolve({ frames: 'first' });
        await settle();
        const states: boolean[] = [];

        // Act
        states.push(animations[0].isPaused);
        screen.report(0);
        states.push(animations[0].isPaused);
        screen.report(1);
        states.push(animations[0].isPaused);
        screen.report(0, true);
        states.push(animations[0].isPaused);

        // Assert: playing, off screen, back, folded to no area
        expect(states).toEqual([false, true, false, true]);
        expect(screen.observers).toHaveLength(1);
        // Without a threshold above zero a folded banner gets no callback
        expect(screen.observers[0].options?.threshold).toBeGreaterThan(0);
    });

    test('an animation created off screen starts on screen, and is_playing off screen waits for it', async() => {
        // Arrange
        const screen = observeScreen();
        renderLottie([{ name: 'playing', type: 'boolean', value: false }], {
            lottie_url: 'https://example.com/animation.json',
            is_playing: '@{playing}'
        });
        await settle();
        screen.report(0);
        files[0].resolve({ frames: 'first' });
        await settle();
        const states: boolean[] = [];

        // Act
        setVariable('playing', { type: 'boolean', value: true });
        await settle();
        states.push(animations[0].isPaused);
        screen.report(1);
        states.push(animations[0].isPaused);

        // Assert
        expect(animations[0].options).toMatchObject({ autoplay: false });
        expect(states).toEqual([true, false]);
    });

    test('an animation paused by is_playing off screen stays paused back on screen', async() => {
        // Arrange
        const screen = observeScreen();
        renderLottie([{ name: 'playing', type: 'boolean', value: true }], {
            lottie_url: 'https://example.com/animation.json',
            is_playing: '@{playing}'
        });
        await settle();
        screen.report(1);
        files[0].resolve({ frames: 'first' });
        await settle();

        // Act
        screen.report(0);
        setVariable('playing', { type: 'boolean', value: false });
        await settle();
        screen.report(1);

        // Assert
        expect(animations[0].isPaused).toBe(true);
    });

    test('is_playing false holds back on screen with a player whose animations do not tell they are paused', async() => {
        // Arrange
        const screen = observeScreen();
        const calls: string[] = [];
        const extension = lottieExtensionBuilder(() => ({
            totalFrames: 10,
            addEventListener: vi.fn(),
            destroy: vi.fn(),
            play: () => calls.push('play'),
            pause: () => calls.push('pause'),
            stop: vi.fn(),
            setDirection: vi.fn(),
            goToAndStop: vi.fn(),
            goToAndPlay: vi.fn()
        }));
        renderLottie([], { lottie_url: 'https://example.com/animation.json', is_playing: false }, extension);
        await settle();
        screen.report(1);
        files[0].resolve({ frames: 'first' });
        await settle();

        // Act
        screen.report(0);
        screen.report(1);

        // Assert
        expect(calls).toEqual(['pause']);
    });

    test('an animation played its repeat_count times stays at its end back on screen with a player that does not tell it is paused', async() => {
        // Arrange
        const screen = observeScreen();
        const player = playerWithoutIsPaused();
        renderLottie([], {
            lottie_url: 'https://example.com/animation.json',
            repeat_count: 1
        }, player.extension);
        await settle();
        screen.report(1);
        files[0].resolve({ frames: 'first' });
        await settle();
        player.listeners.loopComplete();
        player.listeners.loopComplete();
        player.calls.length = 0;

        // Act
        screen.report(0);
        screen.report(1);

        // Assert
        expect(player.calls).toEqual(['pause']);
    });

    test('an animation played once stays at its end back on screen with a player that does not tell it is paused', async() => {
        // Arrange
        const screen = observeScreen();
        const player = playerWithoutIsPaused();
        renderLottie([], {
            lottie_url: 'https://example.com/animation.json',
            repeat_count: 0
        }, player.extension);
        await settle();
        screen.report(1);
        files[0].resolve({ frames: 'first' });
        await settle();
        player.listeners.complete();
        player.calls.length = 0;

        // Act
        screen.report(0);
        screen.report(1);

        // Assert
        expect(player.calls).toEqual(['pause']);
    });

    test('a finished animation that a start action plays again goes on back on screen', async() => {
        // Arrange
        const screen = observeScreen();
        const player = playerWithoutIsPaused();
        renderLottie([], {
            lottie_url: 'https://example.com/animation.json',
            repeat_count: 0
        }, player.extension);
        await settle();
        screen.report(1);
        files[0].resolve({ frames: 'first' });
        await settle();
        player.listeners.complete();
        extensionAction('start');
        await settle();
        player.calls.length = 0;

        // Act
        screen.report(0);
        screen.report(1);

        // Assert
        expect(player.calls).toEqual(['pause', 'play']);
    });

    test('a finished animation that is_playing plays again goes on back on screen', async() => {
        // Arrange
        const screen = observeScreen();
        const player = playerWithoutIsPaused();
        renderLottie([{ name: 'playing', type: 'boolean', value: true }], {
            lottie_url: 'https://example.com/animation.json',
            is_playing: '@{playing}',
            repeat_count: 0
        }, player.extension);
        await settle();
        screen.report(1);
        files[0].resolve({ frames: 'first' });
        await settle();
        player.listeners.complete();
        setVariable('playing', { type: 'boolean', value: false });
        await settle();
        setVariable('playing', { type: 'boolean', value: true });
        await settle();
        player.calls.length = 0;

        // Act
        screen.report(0);
        screen.report(1);

        // Assert
        expect(player.calls).toEqual(['pause', 'play']);
    });

    test('is_playing false while the animation waits for its fonts holds once it has loaded', async() => {
        // Arrange
        renderLottie([{ name: 'playing', type: 'boolean', value: true }], {
            lottie_url: 'https://example.com/animation.json',
            is_playing: '@{playing}'
        }, lottieExtensionBuilder(loadAnimationWithFonts));
        await settle();
        files[0].resolve({ frames: 'with fonts' });
        await settle();

        // Act
        setVariable('playing', { type: 'boolean', value: false });
        await settle();
        loadingAnimations[0].finishLoading();

        // Assert
        expect(loadingAnimations).toHaveLength(1);
        expect(loadingAnimations[0].isPaused).toBe(true);
    });

    test('an animation that leaves the screen while it waits for its fonts stays paused there and plays back on screen', async() => {
        // Arrange
        const screen = observeScreen();
        renderLottie([], { lottie_url: 'https://example.com/animation.json' }, lottieExtensionBuilder(loadAnimationWithFonts));
        await settle();
        screen.report(1);
        files[0].resolve({ frames: 'with fonts' });
        await settle();
        const states: boolean[] = [];

        // Act
        screen.report(0);
        loadingAnimations[0].finishLoading();
        states.push(loadingAnimations[0].isPaused);
        screen.report(1);
        states.push(loadingAnimations[0].isPaused);

        // Assert
        expect(states).toEqual([true, false]);
    });

    test('the animations share one IntersectionObserver, and each one gets the reports of its own view', async() => {
        // Arrange
        const screen = observeScreen();
        instance = renderGifs(target, ['https://example.com/first.json', 'https://example.com/second.json'],
            lottieExtensionBuilder(loadAnimation));
        await settle();
        files.forEach(file => file.resolve({ frames: file.url }));
        await settle();
        const [first, second] = Array.from(target.querySelectorAll('[data-lottie]'));
        const animationIn = (view: Element) => animations.find(it => view.contains(it.options.container));

        // Act
        screen.reportFor(first, 0);

        // Assert
        expect(screen.observers).toHaveLength(1);
        expect(screen.observers[0].targets).toEqual([first, second]);
        expect(animationIn(first)?.isPaused).toBe(true);
        expect(animationIn(second)?.isPaused).toBe(false);
    });

    test('the shared IntersectionObserver lets go of each view that is gone and stops with the last one', async() => {
        // Arrange
        const screen = observeScreen();
        const extension = lottieExtensionBuilder(loadAnimation);
        const otherTarget = document.createElement('div');
        document.body.append(otherTarget);
        instance = renderGifs(target, ['https://example.com/first.json'], extension);
        const other = renderGifs(otherTarget, ['https://example.com/second.json'], extension);
        await settle();
        const otherView = otherTarget.querySelector('[data-lottie]');

        // Act
        instance.$destroy();
        instance = undefined;
        const afterFirst = {
            targets: screen.observers[0].targets.slice(),
            disconnected: screen.observers[0].disconnected
        };
        other.$destroy();
        otherTarget.remove();

        // Assert
        expect(screen.observers).toHaveLength(1);
        expect(afterFirst).toEqual({ targets: [otherView], disconnected: false });
        expect(screen.observers[0].disconnected).toBe(true);
    });

    test('the view stops watching the screen when it is gone', async() => {
        // Arrange
        const screen = observeScreen();
        renderLottie([], { lottie_url: 'https://example.com/animation.json' });
        await settle();

        // Act
        instance?.$destroy();
        instance = undefined;

        // Assert
        expect(screen.observers.map(observer => observer.disconnected)).toEqual([true]);
    });

    test('a lazy player loads together with the file, and the animation comes once both are here', async() => {
        // Arrange
        let resolvePlayer: (player: { loadAnimation: LoadAnimation }) => void = () => undefined;
        const loadPlayer = vi.fn(() => new Promise<{ loadAnimation: LoadAnimation }>(resolve => {
            resolvePlayer = resolve;
        }));
        renderLottie([], { lottie_url: 'https://example.com/animation.json' }, lottieExtensionBuilder({ loadPlayer }));
        await settle();
        const started = [loadPlayer.mock.calls.length, files.length];

        // Act
        files[0].resolve({ frames: 'first' });
        await settle();
        const beforePlayer = animations.length;
        resolvePlayer({ loadAnimation });
        await settle();

        // Assert
        expect(started).toEqual([1, 1]);
        expect(beforePlayer).toBe(0);
        expect(animations).toHaveLength(1);
        expect(errors).toEqual([]);
    });

    test('a lazy player that comes after the view is gone draws nothing', async() => {
        // Arrange
        let resolvePlayer: (player: { loadAnimation: LoadAnimation }) => void = () => undefined;
        const extension = lottieExtensionBuilder({
            loadPlayer: () => new Promise(resolve => {
                resolvePlayer = resolve;
            })
        });
        renderLottie([], { lottie_url: 'https://example.com/animation.json' }, extension);
        await settle();
        files[0].resolve({ frames: 'first' });
        await settle();

        // Act
        instance?.$destroy();
        instance = undefined;
        resolvePlayer({ loadAnimation });
        await settle();

        // Assert
        expect(loadAnimation).not.toHaveBeenCalled();
        expect(errors).toEqual([]);
    });

    test('a player that fails to load is reported as a failed animation', async() => {
        // Arrange
        const extension = lottieExtensionBuilder({
            loadPlayer: () => Promise.reject(new Error('The player is gone'))
        });

        // Act
        renderLottie([], { lottie_url: 'https://example.com/animation.json' }, extension);
        await settle();
        files[0].resolve({ frames: 'first' });
        await settle();

        // Assert
        expect(loadAnimation).not.toHaveBeenCalled();
        expect(errors.map(error => error.message)).toEqual(['Failed to load lottie animation']);
    });

    test('a player that fails at once is reported as a failed animation, the gif shows and the view lets go', async() => {
        // Arrange
        const screen = observeScreen();
        const extension = lottieExtensionBuilder({
            loadPlayer: () => {
                throw new Error('The player is gone');
            }
        });

        // Act
        renderLottie([], { lottie_url: 'https://example.com/animation.json' }, extension);
        await settle();
        const shown = target.querySelector('[data-lottie]') === null;
        instance?.$destroy();
        instance = undefined;

        // Assert
        expect(errors.map(error => error.message)).toEqual(['Failed to load lottie animation']);
        expect(shown).toBe(true);
        expect(screen.observers.map(observer => observer.disconnected)).toEqual([true]);
    });

    test('a lazy player draws with its loadAnimation called on the player', async() => {
        // Arrange
        class Player {
            loadAnimation(options: LoadAnimationOptions) {
                return this.draw(options);
            }

            draw(options: LoadAnimationOptions) {
                return loadAnimation(options);
            }
        }
        const extension = lottieExtensionBuilder({ loadPlayer: () => Promise.resolve(new Player()) });

        // Act
        renderLottie([], { lottie_url: 'https://example.com/animation.json' }, extension);
        await settle();
        files[0].resolve({ frames: 'first' });
        await settle();

        // Assert
        expect(animations).toHaveLength(1);
        expect(errors).toEqual([]);
    });

    test('a stop action before the file comes holds the first frame, though is_playing is true', async() => {
        // Arrange
        renderLottie([], { lottie_url: 'https://example.com/animation.json' });
        await settle();

        // Act
        extensionAction('stop');
        await settle();
        files[0].resolve({ frames: 'first' });
        await settle();

        // Assert
        expect(animations).toHaveLength(1);
        expect(animations[0].isPaused).toBe(true);
        expect(errors).toEqual([]);
    });

    test('a start action before the file comes plays the animation, though is_playing is false', async() => {
        // Arrange
        renderLottie([], { lottie_url: 'https://example.com/animation.json', is_playing: false });
        await settle();

        // Act
        extensionAction('start');
        await settle();
        files[0].resolve({ frames: 'first' });
        await settle();

        // Assert
        expect(animations).toHaveLength(1);
        expect(animations[0].isPaused).toBe(false);
        expect(errors).toEqual([]);
    });

    test('a start action off screen waits for the screen and plays there', async() => {
        // Arrange
        const screen = observeScreen();
        renderLottie([], { lottie_url: 'https://example.com/animation.json' });
        await settle();
        screen.report(1);
        files[0].resolve({ frames: 'first' });
        await settle();
        screen.report(0);
        const states: boolean[] = [];

        // Act
        extensionAction('start');
        await settle();
        states.push(animations[0].isPaused);
        screen.report(1);
        states.push(animations[0].isPaused);

        // Assert
        expect(states).toEqual([true, false]);
    });

    test('a stop action holds the animation back on screen with a player that does not tell it is paused', async() => {
        // Arrange
        const screen = observeScreen();
        const calls: string[] = [];
        const extension = lottieExtensionBuilder(() => ({
            totalFrames: 10,
            addEventListener: vi.fn(),
            destroy: vi.fn(),
            play: () => calls.push('play'),
            pause: () => calls.push('pause'),
            stop: vi.fn(),
            setDirection: vi.fn(),
            goToAndStop: () => calls.push('stop at the first frame'),
            goToAndPlay: () => calls.push('play from the first frame')
        }));
        renderLottie([], { lottie_url: 'https://example.com/animation.json' }, extension);
        await settle();
        screen.report(1);
        files[0].resolve({ frames: 'first' });
        await settle();

        // Act
        extensionAction('stop');
        await settle();
        screen.report(0);
        screen.report(1);

        // Assert
        expect(calls).toEqual(['play', 'stop at the first frame', 'pause']);
    });
});
