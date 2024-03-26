<script lang="ts">
    import { getContext, onDestroy } from 'svelte';
    import type { Unsubscriber } from 'svelte/store';

    import css from './Video.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivVideoData, VideoElements } from '../../types/video';
    import type { ComponentContext } from '../../types/componentContext';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { wrapError } from '../../utils/wrapError';
    import { createVariable } from '../../expressions/variable';
    import { PreparedVideoSource, correctVideoSource } from '../../utils/correctVideoSource';
    import { correctBooleanInt } from '../../utils/correctBooleanInt';
    import Outer from '../utilities/Outer.svelte';
    import { prepareBase64 } from '../../utils/prepareBase64';
    import { videoSize } from '../../utils/video';
    import { makeStyle } from '../../utils/makeStyle';
    import { isPositiveNumber } from '../../utils/isPositiveNumber';
    import { genClassName } from '../../utils/genClassName';

    export let componentContext: ComponentContext<DivVideoData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let prevId: string | undefined;
    let hasError = false;
    let isSelfVariableSet = false;
    let videoElem: HTMLVideoElement;
    let sources: PreparedVideoSource[] = [];
    let loop = false;
    let autoplay = false;
    let muted = false;
    let preload = false;
    let poster: string | undefined = undefined;
    let scale = 'fit';
    let aspectPaddingBottom = '0';
    let elapsedVariableUnsubscriber: Unsubscriber | undefined;

    $: if (componentContext.json) {
        loop = false;
        autoplay = false;
        muted = false;
        preload = false;
        poster = undefined;
        scale = 'fit';
    }

    $: elapsedVariableName = componentContext.json.elapsed_time_variable;
    $: elapsedVariable = elapsedVariableName && componentContext.getVariable(elapsedVariableName, 'integer') || createVariable('temp', 'integer', 0);

    function variableListener(val: number): void {
        if (isSelfVariableSet) {
            isSelfVariableSet = false;
            return;
        }
        if (videoElem) {
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
        } else {
            aspectPaddingBottom = '0';
        }
    }

    function pause(): void {
        videoElem?.pause();
    }

    function start(): void {
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

        if (componentContext.json.id && !hasError && !componentContext.fakeElement) {
            prevId = componentContext.json.id;
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
        aspect: aspectPaddingBottom !== '0'
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
        const actions = componentContext.getJsonWithVars(componentContext.json.end_actions);
        componentContext.execAnyActions(actions);
    }

    function onPlaying(): void {
        const actions = componentContext.getJsonWithVars(componentContext.json.resume_actions);
        componentContext.execAnyActions(actions);
    }

    function onPause(): void {
        const actions = componentContext.getJsonWithVars(componentContext.json.pause_actions);
        componentContext.execAnyActions(actions);
    }

    function onWaiting(): void {
        const actions = componentContext.getJsonWithVars(componentContext.json.buffering_actions);
        componentContext.execAnyActions(actions);
    }

    function onError(): void {
        const actions = componentContext.getJsonWithVars(componentContext.json.fatal_actions);
        componentContext.execAnyActions(actions);
    }

    onDestroy(() => {
        if (prevId) {
            rootCtx.unregisterInstance(prevId);
            prevId = undefined;
        }

        if (elapsedVariableUnsubscriber) {
            elapsedVariableUnsubscriber();
            elapsedVariableUnsubscriber = undefined;
        }
    });
</script>

{#if !hasError}
    <Outer
        cls={genClassName('video', css, mods)}
        customActions={'video'}
        {componentContext}
        {layoutParams}
    >
        {#if mods.aspect}
            <div class={css['video__aspect-wrapper']} style:padding-bottom="{aspectPaddingBottom}%">
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
    </Outer>
{/if}
