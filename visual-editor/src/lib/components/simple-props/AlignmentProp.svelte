<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { AlignmentProperty } from '../../data/componentProps';
    import { getProp } from '../../data/props';
    import Alignment from '../controls/Alignment.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let item: AlignmentProperty;
    export let processedJson;
    export let parentEvalJson;

    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    $: horizontalValue = getProp(processedJson, item.horizontalProp);
    $: verticalValue = getProp(processedJson, item.verticalProp);
    $: widthValue = getProp(processedJson, 'width');
    $: heightValue = getProp(processedJson, 'height');
    $: parentType = getProp(parentEvalJson, 'type');
    $: parentOrientation = getProp(parentEvalJson, 'orientation');
    // todo get from schema
    $: orientationValue = item.orientationProp &&
        (getProp(processedJson, item.orientationProp) || (processedJson.type === 'container' ? 'vertical' : 'horizontal'));

    function onChange(event: CustomEvent<{
        horizontal: string;
        vertical: string;
    }>): void {
        horizontalValue = event.detail.horizontal;
        verticalValue = event.detail.vertical;
        dispatch('change', {
            values: [event.detail.horizontal && {
                prop: item.horizontalProp,
                value: event.detail.horizontal
            }, event.detail.vertical && {
                prop: item.verticalProp,
                value: event.detail.vertical
            }].filter(Boolean),
            item
        });
    }
</script>

<Alignment
    horizontal={horizontalValue}
    vertical={verticalValue}
    orientation={orientationValue}
    isContent={item.isContent}
    isSelfAlign={item.isSelfAlign}
    width={widthValue}
    height={heightValue}
    {parentType}
    {parentOrientation}
    disabled={$readOnly}
    on:change={onChange}
/>
