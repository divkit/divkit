<script lang="ts">
    import { getContext } from 'svelte';
    import type { Action } from '@divkitframework/divkit/typings/common';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import Select from '../Select.svelte';
    import Text from '../controls/Text.svelte';
    import ContextDialog from './ContextDialog.svelte';
    import { parseAction, type ArgResult } from '../../data/actions';
    import { APP_CTX, type Actions2DialogShowProps, type AppContext } from '../../ctx/appContext';
    import type { ActionDesc } from '../../../lib';

    const { l10n, lang } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { customActions } = state;

    $: if (isShown && !readOnly && callback) {
        if (!value.log_url) {
            delete value.log_url;
        }
        callback(value);
    }

    export function show(props: Actions2DialogShowProps): void {
        callback = props.callback;
        target = props.target;
        value = props.value;
        readOnly = props.readOnly;
        isShown = true;
        const parsed = parseAction(props.value?.url, $customActions);
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
        if (customDesc) {
            value.url = customActionToUrl(customDesc, []);
            actionArgs = customDesc.args?.map(desc => {
                return {
                    value: '',
                    desc
                };
            }) || [];
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
    }].concat($customActions.map((actionDesc, i) => {
        return {
            value: `custom:${i}`,
            text: actionDesc.text[$lang] || actionDesc.baseUrl
        };
    }));
</script>

{#if isShown && target}
    <ContextDialog {target} on:close={onClose} canMove={true}>
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
                        />
                    </label>
                </div>
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
