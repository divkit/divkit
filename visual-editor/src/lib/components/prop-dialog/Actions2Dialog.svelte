<script lang="ts" context="module">
    const TYPED_TO_SCHEMA_MAP = {
        focus_element: 'div-action-focus-element',
        clear_focus: 'div-action-clear-focus',
        set_variable: 'div-action-set-variable',
        array_insert_value: 'div-action-array-insert-value',
        array_remove_value: 'div-action-array-remove-value',
        dict_set_value: 'div-action-dict-set-value',
        array_set_value: 'div-action-array-set-value',
        copy_to_clipboard: 'div-action-copy-to-clipboard',
        show_tooltip: 'div-action-show-tooltip',
        hide_tooltip: 'div-action-hide-tooltip',
        timer: 'div-action-timer',
        video: 'div-action-video',
        download: 'div-action-download',
        set_state: 'div-action-set-state',
        scroll_by: 'div-action-scroll-by',
        scroll_to: 'div-action-scroll-to',
        set_stored_value: 'div-action-set-stored-value',
        submit: 'div-action-submit'
    };
</script>

<script lang="ts">
    import { getContext } from 'svelte';
    import type { Action, TypedAction } from '@divkitframework/divkit/typings/common';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import Select from '../Select.svelte';
    import Text from '../controls/Text.svelte';
    import ContextDialog from './ContextDialog.svelte';
    import { parseAction, type ArgResult } from '../../data/actions';
    import { APP_CTX, type Actions2DialogShowProps, type AppContext } from '../../ctx/appContext';
    import type { ActionDesc } from '../../../lib';
    import { parseControls, type Control } from '../../data/schemaTypedActions';
    import ControlsList from './actions-controls/ControlsList.svelte';

    const { l10n, lang } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { customActions, valueFilters } = state;

    $: if (isShown && !readOnly && callback) {
        if (!value.log_url) {
            delete value.log_url;
        }
        callback(value);
    }

    $: if (subtype.startsWith('typed:')) {
        const typedType = subtype.split(':')[1];
        if (typedType in TYPED_TO_SCHEMA_MAP) {
            typedControls = parseControls(TYPED_TO_SCHEMA_MAP[typedType as keyof typeof TYPED_TO_SCHEMA_MAP]);
        } else {
            typedControls = [];
        }
    } else {
        typedControls = null;
    }

    export function show(props: Actions2DialogShowProps): void {
        callback = props.callback;
        target = props.target;
        value = props.value;
        readOnly = props.readOnly;
        isShown = true;
        const parsed = parseAction(props.value, $customActions);
        subtype = parsed.type;
        customDesc = parsed.desc;
        actionArgs = parsed.args || [];
    }

    export function hide(): void {
        isShown = false;
    }

    let target: HTMLElement;
    let subtype = 'url';
    let actionArgs: ArgResult[] = [];
    let isShown = false;
    let value: Action;
    let callback: ((val: Action) => void) | undefined;
    let readOnly: boolean | undefined;
    let customDesc: ActionDesc | undefined;
    let typedControls: Control[] | null = null;

    function onClose(): void {
        isShown = false;
    }

    function customActionToUrl(desc: ActionDesc, args: ArgResult[]): string {
        const searchParams = new URLSearchParams();
        for (const arg of args) {
            const value = arg.value;
            if (value) {
                searchParams.set(arg.desc.name, value);
            }
        }
        return desc.baseUrl + (searchParams.size ? '?' + searchParams.toString() : '');
    }

    function onSubtypeChange(): void {
        customDesc = subtype.startsWith('custom:') ? $customActions[Number(subtype.split(':')[1])] : undefined;
        if (customDesc || !subtype.startsWith('typed:')) {
            for (const key in value) {
                if (key !== 'url' && key !== 'log_id' && key !== 'log_url') {
                    delete value[key as keyof typeof value];
                }
            }
        }

        if (customDesc) {
            value.url = customActionToUrl(customDesc, []);
            actionArgs = customDesc.args?.map(desc => {
                return {
                    value: '',
                    desc
                };
            }) || [];
        } else if (subtype.startsWith('typed:')) {
            value.typed = {
                type: subtype.split(':')[1]
            } as TypedAction;
        } else {
            actionArgs = [];
        }
    }

    function onArgChange(): void {
        if (!customDesc) {
            return;
        }

        value.url = customActionToUrl(customDesc, actionArgs);
    }

    $: types = [{
        value: 'url',
        text: $l10n('actions-url')
    }, {
        value: 'typed:set_variable',
        text: $l10n('actions.set_variable')
    }, {
        value: 'typed:focus_element',
        text: $l10n('actions.focus_element')
    }, {
        value: 'typed:clear_focus',
        text: $l10n('actions.clear_focus')
    }, {
        value: 'typed:array_insert_value',
        text: $l10n('actions.array_insert_value')
    }, {
        value: 'typed:array_remove_value',
        text: $l10n('actions.array_remove_value')
    }, {
        value: 'typed:array_set_value',
        text: $l10n('actions.array_set_value')
    }, {
        value: 'typed:dict_set_value',
        text: $l10n('actions.dict_set_value')
    }, {
        value: 'typed:copy_to_clipboard',
        text: $l10n('actions.copy_to_clipboard')
    }, {
        value: 'typed:show_tooltip',
        text: $l10n('actions.show_tooltip')
    }, {
        value: 'typed:hide_tooltip',
        text: $l10n('actions.hide_tooltip')
    }, {
        value: 'typed:timer',
        text: $l10n('actions.timer')
    }, {
        value: 'typed:video',
        text: $l10n('actions.video')
    }, {
        value: 'typed:download',
        text: $l10n('actions.download')
    }, {
        value: 'typed:set_state',
        text: $l10n('actions.set_state')
    }, {
        value: 'typed:scroll_by',
        text: $l10n('actions.scroll_by')
    }, {
        value: 'typed:scroll_to',
        text: $l10n('actions.scroll_to')
    }, {
        value: 'typed:set_stored_value',
        text: $l10n('actions.set_stored_value')
    }, {
        value: 'typed:submit',
        text: $l10n('actions.submit')
    }].concat($customActions.map((actionDesc, i) => {
        return {
            value: `custom:${i}`,
            text: actionDesc.text[$lang] || actionDesc.baseUrl
        };
    }));
</script>

{#if isShown && target}
    <ContextDialog
        {target}
        canMove={true}
        overflow="visible"
        on:close={onClose}
    >
        <div class="actions2-dialog__content">
            <Select
                items={types}
                bind:value={subtype}
                theme="normal"
                size="medium"
                disabled={readOnly}
                on:change={onSubtypeChange}
            />

            {#if subtype === 'url'}
                <div>
                    <!-- svelte-ignore a11y-label-has-associated-control -->
                    <label>
                        <div class="actions2-dialog__label">
                            {$l10n('actions-url')}
                        </div>
                        <Text
                            bind:value={value.url}
                            disabled={readOnly}
                            filter={valueFilters?.actionUrl}
                        />
                    </label>
                </div>
            {:else if typedControls?.length && value.typed}
                <ControlsList
                    {readOnly}
                    bind:value={value.typed}
                    controls={typedControls}
                />
            {:else if actionArgs.length}
                {#each actionArgs as arg}
                    <div>
                        <!-- svelte-ignore a11y-label-has-associated-control -->
                        <label>
                            <div class="actions2-dialog__label">
                                {arg.desc.text[$lang] || arg.desc.name}
                            </div>
                            <Text
                                bind:value={arg.value}
                                disabled={readOnly}
                                on:change={onArgChange}
                            />
                        </label>
                    </div>
                {/each}
            {/if}

            <div>
                <!-- svelte-ignore a11y-label-has-associated-control -->
                <label>
                    <div class="actions2-dialog__label">
                        {$l10n('actions-log-id')}
                    </div>
                    <Text
                        bind:value={value.log_id}
                        disabled={readOnly}
                    />
                </label>
            </div>

            <div>
                <!-- svelte-ignore a11y-label-has-associated-control -->
                <label>
                    <div class="actions2-dialog__label">
                        {$l10n('actions-log-url')}
                    </div>
                    <Text
                        bind:value={value.log_url}
                        disabled={readOnly}
                    />
                </label>
            </div>
        </div>
    </ContextDialog>
{/if}

<style>
    .actions2-dialog__content {
        display: flex;
        flex-direction: column;
        gap: 24px;
        margin: 16px;
    }

    .actions2-dialog__label {
        margin-bottom: 6px;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-secondary);
    }
</style>
