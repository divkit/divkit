<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import TextProp from './TextProp.svelte';
    import SelectProp from './SelectProp.svelte';
    import CheckboxProp from './CheckboxProp.svelte';
    import { capitalize } from '../../utils/capitalize';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import Spoiler2 from '../controls/Spoiler2.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    const { l10n, lang } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    export let group: string;
    export let list;

    function onCustomNameChange(event: Event, prop: string, key: string): void {
        const target = event.target;
        if (!(target instanceof HTMLElement)) {
            return;
        }

        const name = target.textContent;

        dispatch('rename', {
            prop,
            key,
            newName: name
        });
    }

    function warnLabel(_lang: string, desc: {
        platforms?: string[];
        deprecated?: boolean;
    }): string {
        const list: string[] = [];

        if (desc.platforms && !(
            desc.platforms.includes('android') &&
            desc.platforms.includes('ios') &&
            desc.platforms.includes('web')
        )) {
            if (desc.platforms.length) {
                list.push($l10n('props.supports') + desc.platforms.map(capitalize).join(', '));
            } else {
                list.push($l10n('props.not_supported'));
            }
        }
        if (desc.deprecated) {
            list.push($l10n('props.deprecated'));
        }

        return list.join('. ');
    }
</script>

{#each list as item}
    {@const id = `${group}_${item.prop}`}
    {@const value = item.value}
    {@const filled = value !== undefined}
    {@const deprecated = item.desc.deprecated}

    <li
        class="props-group__item"
        title="{item.name}: {item.description?.ru}"
        class:props-group__filled={filled}
        class:props-group__deprecated={deprecated && item.type !== 'group' && item.type !== 'list'}
    >
        {#if item.type === 'group'}
            <div class="props-group__span-cell">
                <Spoiler2
                    theme="straight-small"
                    mix="props-group__group-summary {filled ? 'props-group__filled' : ''} {deprecated ? 'props-group__deprecated' : ''}"
                    open={false}
                >
                    <div slot="title">
                        {#if item.desc.platforms || item.desc.deprecated}
                            {@const warn = warnLabel($lang, item.desc)}
                            {#if warn}
                                <span
                                    class="props-group__warning"
                                    title={warn}
                                ></span>
                            {/if}
                        {/if}
                        {item.name} {'{'}{value?.type && ` ${value.type} ` || ' '}{'}'}
                    </div>
                    <ul class="props-group__content">
                        {#if item.options}
                            <li class="props-group__item" title="{item.name}: {item.description?.ru}">
                                <label for={id} class="props-group__name">
                                    type
                                </label>
                                <div class="props-group__value">
                                    <SelectProp
                                        value={value?.type}
                                        {id}
                                        prop={`${item.prop}.type`}
                                        options={item.options}
                                        on:change
                                    />
                                </div>
                            </li>
                        {/if}

                        {#if item.subprops}
                            <svelte:self
                                {group}
                                list={item.subprops}
                                on:change
                                on:add
                                on:delete
                                on:rename
                            />
                        {/if}

                        {#if item.additionalProperties}
                            {#each Object.keys(value || {}) as key}
                                {@const id = `${group}_${item.prop}_${key}`}

                                <li
                                    class="props-group__item"
                                    title="{key}"
                                >
                                    <div
                                        {...{ autocomplete: 'off' }}
                                        class="props-group__name"
                                        contenteditable="true"
                                        autocorrect="off"
                                        autocapitalize="off"
                                        spellcheck="false"
                                        on:input={event => onCustomNameChange(event, item.prop, key)}
                                    >
                                        {key}
                                    </div>
                                    <div class="props-group__value">
                                        <TextProp
                                            value={value[key]}
                                            {id}
                                            prop={item.prop + '.' + key}
                                            flags={{}}
                                            on:change
                                        />
                                    </div>
                                    {#if !$readOnly}
                                        <button
                                            class="props-group__button props-group__delete props-group__delete_additional"
                                            on:click|preventDefault={() => dispatch('delete', {
                                                prop: item.prop,
                                                key,
                                                subtype: 'custom'
                                            })}
                                        >
                                            {$l10n('props.remove')}
                                        </button>
                                    {/if}
                                </li>
                            {/each}

                            {#if !$readOnly}
                                <button
                                    class="props-group__button props-group__add props-group__add_custom"
                                    on:click={() => dispatch('add', { prop: item.prop, subtype: 'custom' })}
                                >
                                    {$l10n('props.add')}
                                </button>
                            {/if}
                        {/if}
                    </ul>
                </Spoiler2>
            </div>
        {:else if item.type === 'list'}
            <div class="props-group__span-cell">
                <Spoiler2
                    theme="straight-small"
                    mix="props-group__group-summary {filled ? 'props-group__filled' : ''} {deprecated ? 'props-group__deprecated' : ''}"
                    open={false}
                >
                    <div
                        slot="title"
                    >
                        {#if item.desc.platforms || item.desc.deprecated}
                            {@const warn = warnLabel($lang, item.desc)}
                            {#if warn}
                                <span
                                    class="props-group__warning"
                                    title={warn}
                                ></span>
                            {/if}
                        {/if}
                        {item.name} {'[ '}{item.list?.length || ''}{' ]'}
                    </div>

                    <ul class="props-group__content">
                        {#if item.list}
                            {#each item.list as item2, index}
                                <li class="props-group__item" title="{item2.name}: {item2.description?.ru}">
                                    <div class="props-group__span-cell">
                                        <Spoiler2
                                            theme="straight-small"
                                            mix="props-group__group-details"
                                            open
                                        >
                                            <div slot="title" class="props-group__group-summary">
                                                {index}
                                                {#if !$readOnly}
                                                    <button
                                                        class="props-group__button props-group__delete"
                                                        on:click|preventDefault={() => dispatch('delete', { prop: item.prop, key: index })}
                                                    >
                                                        {$l10n('props.remove')}
                                                    </button>
                                                {/if}
                                            </div>

                                            <ul class="props-group__content">
                                                <svelte:self
                                                    {group}
                                                    list={item2}
                                                    on:change
                                                    on:add
                                                    on:delete
                                                    on:rename
                                                />
                                            </ul>
                                        </Spoiler2>
                                    </div>
                                </li>
                            {/each}
                        {/if}
                    </ul>

                    {#if !$readOnly}
                        <button
                            class="props-group__button props-group__add"
                            on:click={() => dispatch('add', { prop: item.prop, subtype: item.subtype })}
                        >
                            {$l10n('props.add')}
                        </button>
                    {/if}
                </Spoiler2>
            </div>
        {:else}
            <label for={id} class="props-group__name">
                {#if item.desc.platforms || item.desc.deprecated}
                    {@const warn = warnLabel($lang, item.desc)}
                    {#if warn}
                        <span
                            class="props-group__warning"
                            title={warn}
                        ></span>
                    {/if}
                {/if}
                {item.name}
            </label>
            <div class="props-group__value">
                {#if item.type === 'select'}
                    <SelectProp
                        {value}
                        {id}
                        prop={item.prop}
                        options={item.options}
                        on:change
                    />
                {:else if item.type === 'text' || item.type === 'color'}
                    <TextProp
                        {value}
                        {id}
                        prop={item.prop}
                        flags={item.flags || {}}
                        on:change
                    />
                {:else if item.type === 'checkbox'}
                    <CheckboxProp
                        {value}
                        {id}
                        prop={item.prop}
                        on:change
                    />
                {/if}
            </div>
        {/if}
    </li>
{/each}

<style>
    .props-group__content {
        box-sizing: border-box;
        table-layout: fixed;
        list-style: none;
        margin: 0;
        padding: 0;
        padding-left: 9px;
        width: 100%;
    }

    .props-group__item {
        display: flex;
        flex-direction: row;
        transition: background-color .15s ease-in-out;
    }

    .props-group__name {
        width: 100%;
        flex: 1 1 70px;
    }

    .props-group__value {
        flex: 5 1 0px;
    }

    .props-group__name {
        padding: 6px 12px;
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
    }

    .props-group__value {
        border: 1px solid transparent;
        transition: background-color .15s ease-in-out;
    }

    .props-group__item:hover {
        background-color: var(--fill-transparent-2);
    }

    .props-group__group-summary {
        cursor: pointer;
        user-select: none;
    }

    .props-group__button {
        padding: 0 6px;
        font: inherit;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 4px;
        background: var(--background-primary);
        color: inherit;
        transition: .15s ease-in-out;
        transition-property: background-color, border-color;
    }

    .props-group__button:hover {
        border-color: var(--fill-transparent-3);
        background-color: var(--fill-transparent-2);
    }

    .props-group__button:active {
        border-color: var(--fill-transparent-3);
        background-color: var(--fill-transparent-3);
    }

    .props-group__add {
        margin-left: 20px;
        margin-bottom: 4px;
    }

    .props-group__add_custom {
        margin-left: 12px;
    }

    .props-group__delete {
        margin-left: 4px;
    }

    .props-group__delete_additional {
        align-self: center;
    }

    .props-group__filled {
        background: var(--fill-accent-3);
    }

    .props-group__filled:hover {
        background-color: var(--fill-accent-4);
    }

    .props-group__deprecated {
        opacity: .4;
    }

    .props-group__warning {
        display: inline-block;
        width: 20px;
        height: 20px;
        margin-right: 4px;
        vertical-align: -4px;
        background: no-repeat 50% 50% url(../../../assets/warnings.svg);
        background-size: contain;
    }

    .props-group__span-cell {
        width: 100%;
    }
</style>
