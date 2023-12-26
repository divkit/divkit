<script lang="ts">
    import { getContext, onDestroy } from 'svelte';
    import type { Unsubscriber } from 'svelte/store';

    import css from './Video.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { DivVideoData, VideoElements } from '../../types/video';
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

    export let json: Partial<DivVideoData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
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
    let poster: string | undefined = undefined;
    let scale = 'fit';
    let aspectPaddingBottom = '0';
    let elapsedVariableUnsubscriber: Unsubscriber | undefined;

    $: if (json) {
        loop = false;
        autoplay = false;
        muted = false;
        poster = undefined;
        scale = 'fit';
    }

    $: elapsedVariableName = json.elapsed_time_variable;
    $: elapsedVariable = elapsedVariableName && rootCtx.getVariable(elapsedVariableName, 'integer') || createVariable('temp', 'integer', 0);

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

    $: jsonSource = rootCtx.getDerivedFromVars(json.video_sources);
    $: jsonRepeatable = rootCtx.getDerivedFromVars(json.repeatable);
    $: jsonAutostart = rootCtx.getDerivedFromVars(json.autostart);
    $: jsonMuted = rootCtx.getDerivedFromVars(json.muted);
    $: jsonPreview = rootCtx.getDerivedFromVars(json.preview);
    $: jsonScale = rootCtx.getDerivedFromVars(json.scale);
    $: jsonAspect = rootCtx.getDerivedFromVars(json.aspect);

    $: {
        sources = correctVideoSource($jsonSource, sources);

        if (sources.length) {
            hasError = false;
        } else {
            hasError = true;
            rootCtx.logError(wrapError(new Error('Missing "video_sources" in "video"')));
        }
    }

    $: loop = correctBooleanInt($jsonRepeatable, loop);

    $: autoplay = correctBooleanInt($jsonAutostart, autoplay);

    $: muted = correctBooleanInt($jsonMuted, muted);

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
                rootCtx.logError(wrapError(new Error('Video playing error'), {
                    level: 'error',
                    additional: {
                        originalText: String(err)
                    }
                }));
            });
        }
    }

    $: if (json) {
        if (prevId) {
            rootCtx.unregisterInstance(prevId);
            prevId = undefined;
        }

        if (json.id && !hasError && !layoutParams?.fakeElement) {
            prevId = json.id;
            rootCtx.registerInstance<VideoElements>(json.id, {
                pause,
                start
            });
        }
    }

    // Video will not start after autoplay set in setData, do it manually
    $: if (json && $jsonAutostart && videoElem) {
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
        const actions = rootCtx.getJsonWithVars(json.end_actions);
        rootCtx.execAnyActions(actions);
    }

    function onPlaying(): void {
        const actions = rootCtx.getJsonWithVars(json.resume_actions);
        rootCtx.execAnyActions(actions);
    }

    function onPause(): void {
        const actions = rootCtx.getJsonWithVars(json.pause_actions);
        rootCtx.execAnyActions(actions);
    }

    function onWaiting(): void {
        const actions = rootCtx.getJsonWithVars(json.buffering_actions);
        rootCtx.execAnyActions(actions);
    }

    function onError(): void {
        const actions = rootCtx.getJsonWithVars(json.fatal_actions);
        rootCtx.execAnyActions(actions);
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
        {json}
        {origJson}
        {templateContext}
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
