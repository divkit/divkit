<script lang="ts">
    import { getContext } from 'svelte';
    import { slide } from 'svelte/transition';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import PanelTitle from './PanelTitle.svelte';
    import Spoiler2 from './controls/Spoiler2.svelte';
    import { type Variable } from '../data/customVariables';
    import Text from './controls/Text.svelte';
    import Select from './Select.svelte';
    import { ChangeCustomVariablesCommand } from '../data/commands/changeCustomVariables';
    import AddButton from './controls/AddButton.svelte';
    import Checkbox from './controls/Checkbox.svelte';
    import ColorInput from './controls/ColorInput.svelte';
    import { APP_CTX, type AppContext } from '../ctx/appContext';

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly, tree } = state;

    const { customVariables } = state;

    function updateList(list: Variable[]): void {
        state.pushCommand(new ChangeCustomVariablesCommand(state, list));
        $customVariables = list;
        $tree = $tree;
    }

    function add(): void {
        const newList = $customVariables.slice();
        newList.push({
            id: state.genVariableId(),
            name: '',
            type: 'string',
            value: ''
        });

        updateList(newList);
    }

    function onTypeChange(index: number, event: CustomEvent<string>): void {
        const type = event.detail;
        const variable = { ...$customVariables[index] };

        switch (type) {
            case 'string':
            case 'url':
            case 'color':
                variable.value = '';
                break;
            case 'integer':
            case 'number':
                variable.value = '0';
                break;
            case 'boolean':
                variable.value = 'false';
                break;
            case 'dict':
                variable.value = '{}';
                break;
            case 'array':
                variable.value = '[]';
                break;
        }

        const newList = $customVariables.slice();
        newList[index] = variable;

        updateList(newList);
    }

    function valueToString(variable: Variable): string {
        return variable.value;
    }

    function inputSubtype(variable: Variable): 'integer' | 'number' | '' {
        if (variable.type === 'integer' || variable.type === 'number') {
            return variable.type;
        }
        return '';
    }

    function valueError(variable: Variable): string {
        if (variable.type === 'boolean') {
            if (variable.value !== 'true' && variable.value !== 'false') {
                return $l10nString('customVariablesBooleanValidation');
            }
        } else if (variable.type === 'dict' || variable.type === 'array') {
            try {
                JSON.parse(variable.value);
            } catch (err) {
                return $l10nString('customVariablesJsonValidation');
            }
        }

        return '';
    }

    function onNameChange(index: number, event: CustomEvent<{
        value: string | number;
    }>): void {
        const variable = { ...$customVariables[index] };
        variable.name = String(event.detail.value);
        const newList = $customVariables.slice();
        newList[index] = variable;

        updateList(newList);
    }

    function onValueChange(index: number, event: CustomEvent<{
        value: string | number;
    }>): void {
        const variable = { ...$customVariables[index] };
        variable.value = String(event.detail.value);
        const newList = $customVariables.slice();
        newList[index] = variable;

        updateList(newList);
    }

    function onBooleanChange(index: number, event: CustomEvent<{
        value: boolean;
    }>): void {
        const variable = { ...$customVariables[index] };
        variable.value = String(event.detail);
        const newList = $customVariables.slice();
        newList[index] = variable;

        updateList(newList);
    }

    function deleteVariable(index: number): void {
        const newList = $customVariables.slice();
        newList.splice(index, 1);

        updateList(newList);
    }
</script>

<PanelTitle title={$l10nString('customVariablesTitle')} />

<div class="custom-variables">
    {#each $customVariables as variable, index (variable.id)}
        {@const strVal = valueToString(variable)}

        <div
            class="custom-variables__item"
            class:custom-variables__item_disabled={$readOnly}
            transition:slide|local
        >
            <Spoiler2 open={!variable.name} mix="custom-variables__spoiler">
                <div
                    slot="title"
                    class="custom-variables__title"
                    class:custom-variables__title_empty={!variable.name}
                >
                    <span class="custom-variables__name" title={variable.name}>
                        {variable.name || $l10nString('customVariablesEmptyName')}
                    </span>

                    <span class="custom-variables__value" title={strVal}>
                        {strVal}
                    </span>
                </div>
                <div class="custom-variables__form">
                    <label class="custom-variables__form-row">
                        <div class="custom-variables__label">
                            {$l10nString('customVariablesName')}
                        </div>
                        <Text
                            value={variable.name}
                            pattern="[a-zA-Z_][a-zA-Z_0-9.]*"
                            placeholder="[a-zA-Z_][a-zA-Z_0-9.]*"
                            disabled={$readOnly}
                            on:change={event => onNameChange(index, event)}
                        />
                    </label>
                    <label class="custom-variables__form-row">
                        <div class="custom-variables__label">
                            {$l10nString('customVariablesType')}
                        </div>
                        <Select
                            bind:value={variable.type}
                            on:change={event => onTypeChange(index, event)}
                            theme="normal"
                            size="medium"
                            disabled={$readOnly}
                            items={[{
                                value: 'string',
                                text: $l10nString('customVariablesTypeString')
                            }, {
                                value: 'number',
                                text: $l10nString('customVariablesTypeNumber')
                            }, {
                                value: 'integer',
                                text: $l10nString('customVariablesTypeInteger')
                            }, {
                                value: 'boolean',
                                text: $l10nString('customVariablesTypeBoolean')
                            }, {
                                value: 'url',
                                text: $l10nString('customVariablesTypeUrl')
                            }, {
                                value: 'color',
                                text: $l10nString('customVariablesTypeColor')
                            }, {
                                value: 'dict',
                                text: $l10nString('customVariablesTypeDict')
                            }, {
                                value: 'array',
                                text: $l10nString('customVariablesTypeArray')
                            }]}
                        />
                    </label>
                    <label class="custom-variables__form-row">
                        <div class="custom-variables__label">
                            {$l10nString('customVariablesValue')}
                        </div>
                        {#if variable.type === 'boolean'}
                            <Checkbox
                                value={variable.value === 'true'}
                                disabled={$readOnly}
                                on:change={event => onBooleanChange(index, event)}
                            />
                        {:else if variable.type === 'color'}
                            <ColorInput
                                value={variable.value}
                                readOnly={$readOnly}
                                hasDialog
                                on:change={event => onValueChange(index, event)}
                            />
                        {:else}
                            <Text
                                value={valueToString(variable)}
                                subtype={inputSubtype(variable)}
                                anyStep={true}
                                error={valueError(variable)}
                                disabled={$readOnly}
                                on:change={event => onValueChange(index, event)}
                            />
                        {/if}
                    </label>
                </div>
            </Spoiler2>

            {#if !$readOnly}
                <button
                    class="custom-variables__delete"
                    aria-label={$l10nString('delete')}
                    data-custom-tooltip={$l10nString('delete')}
                    on:click|stopPropagation|preventDefault={() => deleteVariable(index)}
                ></button>
            {/if}
        </div>
    {/each}

    {#if !$readOnly}
        <AddButton
            cls="custom-variables__add"
            disabled={$readOnly}
            on:click={add}
        >
            {$l10nString('customVariablesAdd')}
        </AddButton>
    {/if}

    <div class="custom-variables__spacer"></div>
</div>

<style>
    .custom-variables {
        display: flex;
        flex-direction: column;
        height: 100%;
    }

    .custom-variables__item {
        display: flex;
        flex: 0 0 auto;
        gap: 4px;
        align-items: flex-start;
        padding: 0 4px 12px 20px;
    }

    .custom-variables__item_disabled {
        padding-right: 20px;
    }

    :global(.custom-variables__spoiler) {
        flex: 1 1 auto;
        min-width: 0;
    }

    .custom-variables__title {
        display: flex;
        font-size: 14px;
        line-height: 20px;
    }

    .custom-variables__name {
        flex: 0 2 auto;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .custom-variables__value {
        flex: 0 4 auto;
        margin-left: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        color: var(--accent-purple);
    }

    .custom-variables__title_empty {
        font-style: italic;
    }

    .custom-variables__form-row {
        display: block;
        margin-bottom: 12px;
    }

    .custom-variables__form-row:last-child {
        margin-bottom: 0;
    }

    .custom-variables__label {
        margin-bottom: 6px;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-secondary);
    }

    :global(.custom-variables__add) {
        flex: 0 0 auto;
        align-self: flex-start;
        margin-right: 20px;
        margin-left: 20px;
    }

    .custom-variables__spacer {
        height: 20px;
        flex: 0 0 auto;
    }

    .custom-variables__delete {
        position: relative;
        flex: 0 0 auto;
        margin: 0;
        margin-top: 4px;
        padding: 0;

        width: 32px;
        height: 32px;
        cursor: pointer;
        background: none;
        border: 1px solid transparent;
        border-radius: 6px;
        appearance: none;
        transition: .15s ease-in-out;
        transition-property: background-color, border-color, opacity;
    }

    .custom-variables__delete::before {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: no-repeat 50% 50% url(../../assets/minus.svg);
        background-size: 20px;;
        filter: var(--icon-filter);
        content: '';
    }

    .custom-variables__delete:hover {
        background-color: var(--fill-transparent-1);
    }

    .custom-variables__delete:active {
        background-color: var(--fill-transparent-2);
    }

    .custom-variables__delete:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }
</style>
