<script lang="ts">
    import type { ComponentProperty } from '../../data/componentProps';
    import ColorProp from './ColorProp.svelte';
    import SelectProp from './SelectProp.svelte';
    import TextProp from './TextProp.svelte';
    import RadioProp from './RadioProp.svelte';
    import FileProp from './FileProp.svelte';
    import NumberProp from './NumberProp.svelte';
    import BooleanProp from './BooleanProp.svelte';
    import RotationGroupProp from './RotationGroupProp.svelte';
    import AlignmentProp from './AlignmentProp.svelte';
    import MarginsPaddingsProp from './MarginsPaddingsProp.svelte';
    import Background2Prop from './Background2Prop.svelte';
    import TextAlignProp from './TextAlignProp.svelte';
    import TextStylesProp from './TextStylesProp.svelte';
    import Actions2Prop from './Actions2Prop.svelte';
    import VideoSourcesProp from './VideoSourcesProp.svelte';
    import VariableNameProp from './VariableNameProp.svelte';
    import SelectOptionsProp from './SelectOptionsProp.svelte';
    import SelectFontFamilyProp from './SelectFontFamilyProp.svelte';
    import ItemsProp from './ItemsProp.svelte';
    import StateDefaultIdProp from './StateDefaultIdProp.svelte';

    export let item: ComponentProperty;
    export let value;
    export let id = undefined;
    export let processedJson;
    export let parentProcessedJson;
    export let parentEvalJson;
    export let tankerToggled = false;
</script>

{#if item.type === 'string'}
    <TextProp
        {id}
        {item}
        value={value || ''}
        {tankerToggled}
        on:change
    />
{:else if item.type === 'radio'}
    <RadioProp
        {item}
        value={value === undefined ? item.default : value}
        {processedJson}
        {parentProcessedJson}
        on:change
        options={item.options}
    />
{:else if item.type === 'color'}
    <ColorProp
        {item}
        {value}
        on:change
        on:showMore
    />
{:else if item.type === 'select'}
    <SelectProp
        {item}
        value={value === undefined ? item.default : value}
        {processedJson}
        {parentProcessedJson}
        hasEmpty={!item.default}
        on:change
        options={item.options}
    />
{:else if item.type === 'file'}
    <FileProp
        {item}
        {value}
        {processedJson}
        on:change
        on:showMore
    />
{:else if item.type === 'number'}
    <NumberProp
        {item}
        {id}
        value={value === undefined ? item.default : value}
        subtype="number"
        constraint={item.constraint}
        on:change
    />
{:else if item.type === 'integer'}
    <NumberProp
        {item}
        {id}
        value={value === undefined ? item.default : value}
        subtype="integer"
        min={item.min}
        max={item.max}
        constraint={item.constraint}
        inlineLabel={item.inlineLabel}
        on:change
    />
{:else if item.type === 'percent'}
    <NumberProp
        {item}
        {id}
        value={value === undefined ? item.default : value}
        subtype="percent"
        min={0}
        max={100}
        on:change
    />
{:else if item.type === 'background2'}
    <Background2Prop
        {item}
        {value}
        on:change
    />
{:else if item.type === 'boolean'}
    <BooleanProp
        {item}
        {id}
        {value}
        on:change
    />
{:else if item.type === 'rotation'}
    <RotationGroupProp
        {item}
        {id}
        value={value === undefined ? item.default : value}
        on:change
    />
{:else if item.type === 'alignment'}
    <AlignmentProp
        {item}
        {processedJson}
        {parentEvalJson}
        on:change
    />
{:else if item.type === 'margins-paddings'}
    <MarginsPaddingsProp
        {item}
        {processedJson}
        on:change
    />
{:else if item.type === 'text-align'}
    <TextAlignProp
        {value}
        {item}
        on:change
    />
{:else if item.type === 'text-styles'}
    <TextStylesProp
        {item}
        {processedJson}
        on:change
    />
{:else if item.type === 'actions2'}
    <Actions2Prop
        {item}
        {value}
        {processedJson}
        on:change
    />
{:else if item.type === 'video_sources'}
    <VideoSourcesProp
        {item}
        {value}
        on:change
    />
{:else if item.type === 'variable-name'}
    <VariableNameProp
        {item}
        {value}
        {processedJson}
        {parentProcessedJson}
        on:change
    />
{:else if item.type === 'select-options'}
    <SelectOptionsProp
        {item}
        {value}
        on:change
    />
{:else if item.type === 'select-font-family'}
    <SelectFontFamilyProp
        {item}
        {value}
        on:change
    />
{:else if item.type === 'state-default-id'}
    <StateDefaultIdProp
        {item}
        {value}
        {processedJson}
        on:change
    />
{:else if item.type === 'items-list'}
    <ItemsProp
        {item}
        {value}
        on:change
    />
{/if}
