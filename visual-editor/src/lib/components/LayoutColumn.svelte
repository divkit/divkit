<script lang="ts">
    import { getContext, type Component } from 'svelte';
    import type { LayoutItem } from '../../lib';
    import Canvas from './Canvas.svelte';
    import Components from './Components.svelte';
    import NewComponent from './NewComponent.svelte';
    import Palette from './Palette.svelte';
    import Props from './Props.svelte';
    import SplitView from './SplitView.svelte';
    import TextEditor from './TextEditor.svelte';
    import PropsAndCode from './PropsAndCode.svelte';
    import TankerOverview from './TankerOverview.svelte';
    import SourcesOverview from './SourcesOverview.svelte';
    import CustomVariables from './CustomVariables.svelte';
    import Timers from './Timers.svelte';
    import { Truthy } from '../utils/truthy';
    import { APP_CTX, type AppContext } from '../ctx/appContext';

    export let items: LayoutItem[];

    const { state } = getContext<AppContext>(APP_CTX);
    const { paletteEnabled, readOnly } = state;

    $: componentMap = {
        'new-component': $readOnly ? null : NewComponent,
        'component-tree': Components,
        'component-props': Props,
        preview: Canvas,
        palette: $paletteEnabled ? Palette : null,
        code: TextEditor,
        'component-props:code': PropsAndCode,
        'tanker-overview': TankerOverview,
        'sources-overview': SourcesOverview,
        'custom-variables': CustomVariables,
        timers: Timers
    };

    $: components = items.map(item => {
        if (!componentMap[item]) {
            return;
        }
        return {
            component: componentMap[item] as Component,
            weight: 1,
            minSize: 200
        };
    }).filter(Truthy);
</script>

{#if components.length}
    <SplitView
        orientation="vertical"
        {components}
    />
{:else}
    <svelte:component this={components[0].component} />
{/if}
