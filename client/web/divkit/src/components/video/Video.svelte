<script lang="ts">
    import { getContext, onDestroy, onMount } from 'svelte';
    import type { Unsubscriber } from 'svelte/store';

    import css from './Video.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivVideoData, VideoElements } from '../../types/video';
    import type { ComponentContext } from '../../types/componentContext';
    import type { VideoPlayerInstance, VideoPlayerProviderClient, VideoPlayerProviderData, VideoPlayerProviderServer, VideoSource } from '../../../typings/common';
    import type { MaybeMissing } from '../../expressions/json';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';
    import { wrapError } from '../../utils/wrapError';
    import { createVariable } from '../../expressions/variable';
    import { type PreparedVideoSource, correctVideoSource } from '../../utils/correctVideoSource';
    import { correctBooleanInt } from '../../utils/correctBooleanInt';
    import Outer from '../utilities/Outer.svelte';
    import { prepareBase64 } from '../../utils/prepareBase64';
    import { videoSize } from '../../utils/video';
    import { makeStyle } from '../../utils/makeStyle';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import { genClassName } from '../../utils/genClassName';
    import DevtoolHolder from '../utilities/DevtoolHolder.svelte';

    export let componentContext: ComponentContext<DivVideoData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    const videoPlayerProvider = rootCtx.videoPlayerProvider;

    let prevId: string | undefined;
    let hasError = false;
    let isSelfVariableSet = false;
    let videoElem: HTMLVideoElement;
    let videoParentElem: HTMLElement;
    let sources: PreparedVideoSource[] = [];
    let loop = false;
    let autoplay = false;
    let muted = false;
    let preload = false;
    let poster: string | undefined = undefined;
    let scale = 'fit';
    let aspectPaddingBottom = '0';
    let isAbsolute = false;
    let elapsedVariableUnsubscriber: Unsubscriber | undefined;
    let providedVideoTemplate = '';
    let customVideoInstance: VideoPlayerInstance | undefined;
    let shouldUseVideoProvider = Boolean(videoPlayerProvider);

    if (import.meta.env.SSR && videoPlayerProvider) {
        const provider = videoPlayerProvider as VideoPlayerProviderServer;
        if (typeof provider.template === 'string') {
            providedVideoTemplate = provider.template;
        } else {
            const data = calcVideoProviderData(componentContext.json);
            if (data) {
                providedVideoTemplate = provider.template(data);
            } else {
                shouldUseVideoProvider = false;
            }
        }
    }

    function calcVideoProviderData(json: MaybeMissing<DivVideoData>): VideoPlayerProviderData | undefined {
        const evalled = componentContext.getJsonWithVars({
            sources: json.video_sources,
            repeatable: json.repeatable,
            autostart: json.autostart,
            preloadRequired: json.preload_required,
            muted: json.muted,
            preview: json.preview,
            aspect: json.aspect,
            scale: json.scale,
            payload: json.player_settings_payload
        });
        const repeatable = correctBooleanInt(evalled.repeatable, false);
        const autostart = correctBooleanInt(evalled.autostart, false);
        const preloadRequired = correctBooleanInt(evalled.preloadRequired, false);
        const muted = correctBooleanInt(evalled.muted, false);
        const aspect = evalled.aspect?.ratio && isPositiveNumber(evalled.aspect.ratio) ?
            evalled.aspect.ratio :
            undefined;

        if (!evalled.sources?.length) {
            return;
        }

        return {
            sources: evalled.sources as VideoSource[],
            repeatable,
            autostart,
            preloadRequired,
            muted,
            preview: evalled.preview,
            aspect,
            scale: evalled.scale,
            payload: evalled.payload
        };
    }

    $: if (componentContext.json) {
        loop = false;
        autoplay = false;
        muted = false;
        preload = false;
        poster = undefined;
        scale = 'fit';
        isAbsolute = false;
        shouldUseVideoProvider = Boolean(videoPlayerProvider);
    }

    $: if (componentContext.json && customVideoInstance && (
        $jsonSource ||
        $jsonRepeatable ||
        $jsonAutostart ||
        $jsonMuted ||
        $jsonPreload ||
        $jsonPreview ||
        $jsonScale ||
        $jsonAspect
    )) {
        const data = calcVideoProviderData(componentContext.json);
        if (data) {
            customVideoInstance.update?.(data);
        }
    }

    $: elapsedVariableName = componentContext.json.elapsed_time_variable;
    $: elapsedVariable = elapsedVariableName && (componentContext.getVariable(elapsedVariableName, 'integer') || rootCtx.awaitGlobalVariable(elapsedVariableName, 'integer', 0)) || createVariable('temp', 'integer', 0);

    function variableListener(val: number): void {
        if (isSelfVariableSet) {
            isSelfVariableSet = false;
            return;
        }
        if (customVideoInstance) {
            customVideoInstance.seek?.(Number(val));
        } else if (videoElem) {
            videoElem.currentTime = Number(val) / 1000;
        }
    }

    $: if (elapsedVariable) {
        if (elapsedVariableUnsubscriber) {
            elapsedVariableUnsubscriber();
        }
        elapsedVariableUnsubscriber = elapsedVariable.subscribe(variableListener);
    }

    $: jsonSource = componentContext.getDerivedFromVars(componentContext.json.video_sources);
    $: jsonRepeatable = componentContext.getDerivedFromVars(componentContext.json.repeatable);
    $: jsonAutostart = componentContext.getDerivedFromVars(componentContext.json.autostart);
    $: jsonMuted = componentContext.getDerivedFromVars(componentContext.json.muted);
    $: jsonPreload = componentContext.getDerivedFromVars(componentContext.json.preload_required);
    $: jsonPreview = componentContext.getDerivedFromVars(componentContext.json.preview);
    $: jsonScale = componentContext.getDerivedFromVars(componentContext.json.scale);
    $: jsonAspect = componentContext.getDerivedFromVars(componentContext.json.aspect);
    $: jsonWidth = componentContext.getDerivedFromVars(componentContext.json.width);
    $: jsonHeight = componentContext.getDerivedFromVars(componentContext.json.height);

    $: {
        sources = correctVideoSource($jsonSource, sources);

        if (sources.length) {
            hasError = false;
        } else {
            hasError = true;
            componentContext.logError(wrapError(new Error('Missing "video_sources" in "video"')));
        }
    }

    $: loop = correctBooleanInt($jsonRepeatable, loop);

    $: autoplay = correctBooleanInt($jsonAutostart, autoplay);

    $: muted = correctBooleanInt($jsonMuted, muted);

    $: preload = correctBooleanInt($jsonPreload, preload);

    $: poster = typeof $jsonPreview === 'string' ? prepareBase64($jsonPreview) : poster;

    $: {
        scale = videoSize($jsonScale) || scale;
    }

    $: {
        const newRatio = $jsonAspect?.ratio;
        if (newRatio && isPositiveNumber(newRatio)) {
            aspectPaddingBottom = (100 / Number(newRatio)).toFixed(2);
            isAbsolute = true;
        } else {
            aspectPaddingBottom = '0';
            isAbsolute = (!$jsonWidth || $jsonWidth.type === 'match_parent') && $jsonHeight?.type === 'match_parent';
        }
    }

    function pause(): void {
        if (customVideoInstance) {
            customVideoInstance.pause();
        } else {
            videoElem?.pause();
        }
    }

    function start(): void {
        if (customVideoInstance) {
            customVideoInstance.play();
            return;
        }

        const res = videoElem?.play();
        if (res) {
            res.catch(err => {
                componentContext.logError(wrapError(new Error('Video playing error'), {
                    level: 'error',
                    additional: {
                        originalText: String(err)
                    }
                }));
            });
        }
    }

    $: if (componentContext.json) {
        if (prevId) {
            rootCtx.unregisterInstance(prevId);
            prevId = undefined;
        }

        if (componentContext.id && !hasError && !componentContext.fakeElement) {
            prevId = componentContext.id;
            rootCtx.registerInstance<VideoElements>(prevId, {
                pause,
                start
            });
        }
    }

    // Video will not start after autoplay set in setData, do it manually
    $: if (componentContext.json && $jsonAutostart && videoElem) {
        start();
    }

    $: mods = {
        absolute: isAbsolute
    };

    $: style = {
        'object-fit': scale
    };

    function onTimeUpdate(): void {
        if (videoElem) {
            isSelfVariableSet = true;
            elapsedVariable.setValue(Math.floor(videoElem.currentTime * 1000));
        }
    }

    function onEnd(): void {
        componentContext.execAnyActions(componentContext.json.end_actions);
    }

    function onPlaying(): void {
        componentContext.execAnyActions(componentContext.json.resume_actions);
    }

    function onPause(): void {
        componentContext.execAnyActions(componentContext.json.pause_actions);
    }

    function onWaiting(): void {
        componentContext.execAnyActions(componentContext.json.buffering_actions);
    }

    function onError(): void {
        componentContext.execAnyActions(componentContext.json.fatal_actions);
    }

    onMount(() => {
        if (videoPlayerProvider && videoParentElem) {
            const data = calcVideoProviderData(componentContext.json);
            if (data) {
                const res = (videoPlayerProvider as VideoPlayerProviderClient).instance(videoParentElem, data);
                if (res) {
                    customVideoInstance = res;
                } else {
                    shouldUseVideoProvider = false;
                }
            }
        }
    });

    onDestroy(() => {
        if (prevId) {
            rootCtx.unregisterInstance(prevId);
            prevId = undefined;
        }

        if (elapsedVariableUnsubscriber) {
            elapsedVariableUnsubscriber();
            elapsedVariableUnsubscriber = undefined;
        }

        if (customVideoInstance) {
            customVideoInstance.destroy();
            customVideoInstance = undefined;
        }
    });
</script>

{#if !hasError}
    <Outer
        cls={genClassName('video', css, mods)}
        customActions="video"
        {componentContext}
        {layoutParams}
        heightByAspect={aspectPaddingBottom !== '0'}
    >
        {#if aspectPaddingBottom !== '0'}
            <div class={css['video__aspect-wrapper']} style:padding-bottom="{aspectPaddingBottom}%">
                {#if shouldUseVideoProvider}
                    <div class={css.video__container} bind:this={videoParentElem}>
                        <!-- eslint-disable-next-line svelte/no-at-html-tags -->
                        {@html providedVideoTemplate}
                    </div>
                {:else}
                    <video
                        bind:this={videoElem}
                        class={css.video__video}
                        style={makeStyle(style)}
                        playsinline
                        {loop}
                        {autoplay}
                        {muted}
                        {poster}
                        preload={preload ? 'metadata' : 'auto'}
                        on:timeupdate={onTimeUpdate}
                        on:ended={onEnd}
                        on:playing={onPlaying}
                        on:pause={onPause}
                        on:waiting={onWaiting}
                        on:error={onError}
                    >
                        {#each sources as source}
                            {#key source}
                                <source src={source.src} type={source.type} on:error={onError}>
                            {/key}
                        {/each}
                    </video>
                {/if}
            </div>
        {:else}
            {#if shouldUseVideoProvider}
                <div class={css.video__container} bind:this={videoParentElem}>
                    <!-- eslint-disable-next-line svelte/no-at-html-tags -->
                    {@html providedVideoTemplate}
                </div>
            {:else}
                <video
                    bind:this={videoElem}
                    class={css.video__video}
                    style={makeStyle(style)}
                    playsinline
                    {loop}
                    {autoplay}
                    {muted}
                    {poster}
                    preload={preload ? 'metadata' : 'auto'}
                    on:timeupdate={onTimeUpdate}
                    on:ended={onEnd}
                    on:playing={onPlaying}
                    on:pause={onPause}
                    on:waiting={onWaiting}
                    on:error={onError}
                >
                    {#each sources as source}
                        {#key source}
                            <source src={source.src} type={source.type} on:error={onError}>
                        {/key}
                    {/each}
                </video>
            {/if}
        {/if}
    </Outer>
{:else if process.env.DEVTOOL}
    <DevtoolHolder
        {componentContext}
    />
{/if}
