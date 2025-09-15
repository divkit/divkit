<script lang="ts">
    import { createEventDispatcher, getContext, onDestroy } from 'svelte';
    import type {
        Action,
        ActionArrayInsertValue,
        ActionArrayRemoveValue,
        ActionFocusElement,
        ActionSetVariable,
        ActionDictSetValue,
        ActionArraySetValue,
        ActionShowTooltip,
        ActionHideTooltip,
        ActionTimer,
        ActionVideo,
        ActionDownload,
        ActionSetState,
        ActionStore,
        ActionSubmit,
        ActionScrollBy,
        ActionScrollTo
    } from '@divkitframework/divkit/typings/common';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import { parseAction } from '../../data/actions';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import { checkStringValue } from '../../utils/checkValue';

    export let value: Action;

    const { l10n, lang } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state, actions2Dialog } = getContext<AppContext>(APP_CTX);
    const { customActions, readOnly, valueFilters } = state;

    const dispatch = createEventDispatcher();

    let elem: HTMLElement;
    let type = '';
    let text = '';
    let hasError = false;

    function updateVal(value: Action): void {
        const parsed = parseAction(value, $customActions);

        type = '';
        hasError = false;

        if (parsed.type === 'url') {
            type = $l10n('actions-url');
            text = parsed.url || '';
            hasError = !checkStringValue(parsed.url, valueFilters?.actionUrl);
        } else if (parsed.type.startsWith('typed:')) {
            if (parsed.type === 'typed:focus_element') {
                type = $l10n('actions.focus_element');
                text = (parsed.typedParams as ActionFocusElement)?.element_id || '';
            } else if (parsed.type === 'typed:clear_focus') {
                type = $l10n('actions.clear_focus');
                text = '';
            } else if (parsed.type === 'typed:set_variable') {
                type = $l10n('actions.set_variable');
                text = (parsed.typedParams as ActionSetVariable)?.variable_name || '';
            } else if (parsed.type === 'typed:array_insert_value') {
                type = $l10n('actions.array_insert_value');
                text = (parsed.typedParams as ActionArrayInsertValue)?.variable_name || '';
            } else if (parsed.type === 'typed:array_remove_value') {
                type = $l10n('actions.array_remove_value');
                text = (parsed.typedParams as ActionArrayRemoveValue)?.variable_name || '';
            } else if (parsed.type === 'typed:dict_set_value') {
                type = $l10n('actions.dict_set_value');
                text = (parsed.typedParams as ActionDictSetValue)?.variable_name || '';
            } else if (parsed.type === 'typed:array_set_value') {
                type = $l10n('actions.array_set_value');
                text = (parsed.typedParams as ActionArraySetValue)?.variable_name || '';
            } else if (parsed.type === 'typed:copy_to_clipboard') {
                type = $l10n('actions.copy_to_clipboard');
                text = '';
            } else if (parsed.type === 'typed:show_tooltip') {
                type = $l10n('actions.show_tooltip');
                text = (parsed.typedParams as ActionShowTooltip)?.id || '';
            } else if (parsed.type === 'typed:hide_tooltip') {
                type = $l10n('actions.hide_tooltip');
                text = (parsed.typedParams as ActionHideTooltip)?.id || '';
            } else if (parsed.type === 'typed:timer') {
                type = $l10n('actions.timer');
                text = (parsed.typedParams as ActionTimer)?.action || '';
            } else if (parsed.type === 'typed:video') {
                type = $l10n('actions.video');
                text = (parsed.typedParams as ActionVideo)?.action || '';
            } else if (parsed.type === 'typed:download') {
                type = $l10n('actions.download');
                text = (parsed.typedParams as ActionDownload)?.url || '';
            } else if (parsed.type === 'typed:set_state') {
                type = $l10n('actions.set_state');
                text = (parsed.typedParams as ActionSetState)?.state_id || '';
            } else if (parsed.type === 'typed:scroll_by') {
                type = $l10n('actions.scroll_by');
                text = (parsed.typedParams as ActionScrollBy)?.id || '';
            } else if (parsed.type === 'typed:scroll_to') {
                type = $l10n('actions.scroll_to');
                text = (parsed.typedParams as ActionScrollTo)?.id || '';
            } else if (parsed.type === 'typed:set_stored_value') {
                type = $l10n('actions.set_stored_value');
                text = (parsed.typedParams as ActionStore)?.name || '';
            } else if (parsed.type === 'typed:submit') {
                type = $l10n('actions.submit');
                text = (parsed.typedParams as ActionSubmit)?.container_id || '';
            } else {
                type = $l10n('actions-unknown');
                text = parsed.url || '';
            }
        } else if (parsed.desc) {
            type = parsed.desc.text[$lang] || parsed.desc.baseUrl;
            text = '';
        } else {
            type = $l10n('actions-unknown');
            text = parsed.url || '';
        }
    }

    $: updateVal(value);

    function onClick(): void {
        actions2Dialog().show({
            value,
            target: elem,
            readOnly: $readOnly,
            callback(newValue) {
                value = newValue;
                dispatch('change');
            }
        });
    }

    onDestroy(() => {
        actions2Dialog().hide();
    });
</script>

<button
    class="actions2-item"
    class:actions2-item_readonly={$readOnly}
    class:actions2-item_error={hasError}
    bind:this={elem}
    on:click={onClick}
>
    <div class="actions2-item__type">
        {type}
    </div>
    <div class="actions2-item__text">
        {text}
    </div>
</button>

<style>
    .actions2-item {
        position: relative;
        box-sizing: border-box;
        display: flex;
        gap: 8px;
        width: 100%;
        padding: 9px 15px;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background-color: var(--background-primary);
        transition: border-color .15s ease-in-out;
        cursor: pointer;
    }

    .actions2-item_readonly {
        border: none;
        background: var(--fill-transparent-1);
    }

    .actions2-item_error {
        border-color: var(--accent-red);
    }

    .actions2-item:not(.actions2-item_readonly):not(.actions2-item_error):hover {
        border-color: var(--fill-transparent-4);
    }

    .actions2-item:not(.actions2-item_readonly):not(.actions2-item_error):focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }

    .actions2-item__type {
        font-size: 14px;
        line-height: 20px;
        color: var(--text-secondary);
        white-space: nowrap;
    }

    .actions2-item__text {
        font-size: 14px;
        line-height: 20px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
</style>
