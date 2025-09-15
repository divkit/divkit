<script lang="ts">
  import { afterUpdate, getContext, onDestroy, onMount } from 'svelte';
    import type { DivBaseData } from '../../types/base';
    import type { ComponentContext } from '../../types/componentContext';
    import { devtool, type DevtoolResult } from '../../use/devtool';
    import { ROOT_CTX, type RootCtxValue } from '../../context/root';

    export let componentContext: ComponentContext<DivBaseData>;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let dev: DevtoolResult | null = null;

    function updateDevtool(): void {
        if (dev) {
            dev.update(componentContext);
        }
    }

    onMount(() => {
        if (devtool && !componentContext.fakeElement) {
            dev = devtool(null, rootCtx, componentContext,);
        }
    });

    afterUpdate(updateDevtool);

    onDestroy(() => {
        if (dev) {
            dev.destroy();
        }
    });
</script>
