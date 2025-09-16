<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { VideoSource } from '@divkitframework/divkit/typings/common';
    import type { FileProperty } from '../../data/componentProps';
    import Text from '../controls/Text.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import { getObjectProperty } from '../../utils/objectProperty';

    export let value: string;
    export let item: FileProperty;
    export let processedJson: object | undefined = undefined;

    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    $: generateFromVideo = (item.generateFromVideoProperty && processedJson) ?
        getObjectProperty(processedJson, item.generateFromVideoProperty) as VideoSource[] :
        undefined;

    $: generateFromLottie = (item.generateFromLottieProperty && processedJson) ?
        getObjectProperty(processedJson, item.generateFromLottieProperty) as string :
        undefined;

    function onChange() {
        dispatch('change', {
            value,
            item
        });
    }
</script>

<Text
    subtype="file"
    fileType={item.subtype}
    disabled={$readOnly}
    {generateFromVideo}
    {generateFromLottie}
    bind:value={value}
    on:change={onChange}
/>
