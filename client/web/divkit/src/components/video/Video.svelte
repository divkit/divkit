<script lang="ts">
    import { getContext, onDestroy } from 'svelte';

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

    export let json: Partial<DivVideoData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    let hasError = false;
    let isSelfVariableSet = false;
    let videoElem: HTMLVideoElement;

    const jsonSource = rootCtx.getDerivedFromVars(json.video_sources);
    let sources: PreparedVideoSource[] = [];

    $: {
        sources = correctVideoSource($jsonSource, sources);

        if (sources.length) {
            hasError = false;
        } else {
            hasError = true;
            rootCtx.logError(wrapError(new Error('Missing "video_sources" in "video"')));
        }
    }

    const jsonRepeatable = rootCtx.getDerivedFromVars(json.repeatable);
    let loop = false;
    $: loop = correctBooleanInt($jsonRepeatable, loop);

    const jsonAutostart = rootCtx.getDerivedFromVars(json.autostart);
    let autoplay = false;
    $: autoplay = correctBooleanInt($jsonAutostart, autoplay);

    const jsonMuted = rootCtx.getDerivedFromVars(json.muted);
    let muted = false;
    $: muted = correctBooleanInt($jsonMuted, muted);

    const jsonPreview = rootCtx.getDerivedFromVars(json.preview);
    let poster: string | undefined = undefined;
    $: poster = typeof $jsonPreview === 'string' ? prepareBase64($jsonPreview) : poster;

    const elapsedVariableName = json.elapsed_time_variable;
    let elapsedVariable = elapsedVariableName && rootCtx.getVariable(elapsedVariableName, 'integer') || createVariable('temp', 'integer', 0);

    elapsedVariable.subscribe(val => {
        if (isSelfVariableSet) {
            isSelfVariableSet = false;
            return;
        }
        if (videoElem) {
            videoElem.currentTime = Number(val) / 1000;
        }
    });

    if (json.id && !hasError && !layoutParams?.fakeElement) {
        rootCtx.registerInstance<VideoElements>(json.id, {
            pause() {
                videoElem?.pause();
            },
            start() {
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
        });
    }

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
        if (json.id && !hasError && !layoutParams?.fakeElement) {
            rootCtx.unregisterInstance(json.id);
        }
    });
</script>

{#if !hasError}
    <Outer
        cls={css.video}
        customActions={'video'}
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
    >
        <!-- svelte-ignore a11y-media-has-caption -->
        <video
            bind:this={videoElem}
            class={css.video__video}
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
                <source src={source.src} type={source.type} on:error={onError}>
            {/each}
        </video>
    </Outer>
{/if}
