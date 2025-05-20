<script lang="ts" context="module">
    const CONTROL_TYPE_TO_SUBTYPE = {
        string: undefined,
        integer: 'integer',
        number: 'number',
        color: undefined,
        url: undefined,
        boolean: undefined,
        array: 'array',
        object: 'dict'
    } as const;
</script>

<script lang="ts">
    import type { Control } from '../../../data/schemaTypedActions';
    import Checkbox from '../../controls/Checkbox.svelte';
    import ColorInput from '../../controls/ColorInput.svelte';
    import Text from '../../controls/Text.svelte';
    import Select from '../../Select.svelte';

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    export let value: any;
    export let controls: Control[];
    export let readOnly: boolean | undefined;

    $: controls.forEach(control => {
        if (control.type === 'select' && control.subcontrols) {
            value[control.name] = value[control.name] || {};
        }
    });

    function onTypeChange(name: string): void {
        value[name] = {
            type: value[name].type
        };
    }
</script>

<div class="controls-list">
    {#each controls as control}
        <div>
            <!-- svelte-ignore a11y-label-has-associated-control -->
            <label class="controls-list__row">
                <div class="controls-list__label">
                    {control.name}
                </div>

                {#if control.type === 'select'}
                    {#if control.subcontrols}
                        <Select
                            bind:value={value[control.name].type}
                            theme="normal"
                            size="medium"
                            items={control.options}
                            disabled={readOnly}
                            on:change={() => onTypeChange(control.name)}
                            on:change
                        />
                    {:else}
                        <Select
                            bind:value={value[control.name]}
                            theme="normal"
                            size="medium"
                            items={control.options}
                            disabled={readOnly}
                            on:change
                        />
                    {/if}
                {:else if control.type === 'boolean'}
                    <Checkbox
                        disabled={readOnly}
                        bind:value={value[control.name]}
                        on:change
                    />
                {:else if control.type === 'color'}
                    <ColorInput
                        bind:value={value[control.name]}
                        on:change
                        hasDialog={true}
                        readOnly={readOnly}
                    />
                {:else}
                    <Text
                        bind:value={value[control.name]}
                        subtype={CONTROL_TYPE_TO_SUBTYPE[control.type]}
                        disabled={readOnly}
                        on:change
                    />
                {/if}
            </label>

            {#if control.type === 'select' && control.subcontrols?.[value[control.name].type]}
                <div class="controls-list__sublist">
                    {#key value[control.name].type}
                        <svelte:self
                            bind:value={value[control.name]}
                            controls={control.subcontrols[value[control.name].type]}
                            {readOnly}
                            on:change
                        />
                    {/key}
                </div>
            {/if}
        </div>
    {/each}
</div>

<style>
    .controls-list {
        display: flex;
        flex-direction: column;
        gap: 24px;
    }

    .controls-list__label {
        margin-bottom: 6px;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-secondary);
    }

    .controls-list__sublist {
        margin-top: 24px;
    }
</style>
