<script lang="ts">
    import { getContext } from 'svelte';
    import { slide } from 'svelte/transition';
    import type { Action } from '@divkitframework/divkit/typings/common';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import PanelTitle from './PanelTitle.svelte';
    import Spoiler2 from './controls/Spoiler2.svelte';
    import Text from './controls/Text.svelte';
    import AddButton from './controls/AddButton.svelte';
    import { type Timer } from '../data/timers';
    import { ChangeTimersCommand } from '../data/commands/changeTimers';
    import Actions2Prop from './simple-props/Actions2Prop.svelte';
    import { APP_CTX, type AppContext } from '../ctx/appContext';

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { timers, readOnly, tree } = state;

    function updateList(list: Timer[]): void {
        state.pushCommand(new ChangeTimersCommand(state, list));
        $timers = list;
        $tree = $tree;
    }

    function add(): void {
        const newList = $timers.slice();
        newList.push({
            __id: state.genTimerId(),
            id: ''
        });

        updateList(newList);
    }

    function onIdChange(index: number, event: CustomEvent<{
        value: string | number;
    }>): void {
        const timer = { ...$timers[index] };
        timer.id = String(event.detail.value);
        const newList = $timers.slice();
        newList[index] = timer;

        updateList(newList);
    }

    function onValueVariableChange(index: number, event: CustomEvent<{
        value: string | number;
    }>): void {
        const timer = { ...$timers[index] };
        timer.value_variable = String(event.detail.value);
        const newList = $timers.slice();
        newList[index] = timer;

        updateList(newList);
    }

    function onDurationChange(index: number, event: CustomEvent<{
        value: string | number;
    }>): void {
        const timer = { ...$timers[index] };
        timer.duration = event.detail.value;
        const newList = $timers.slice();
        newList[index] = timer;

        updateList(newList);
    }

    function onIntervalChange(index: number, event: CustomEvent<{
        value: string | number;
    }>): void {
        const timer = { ...$timers[index] };
        timer.tick_interval = event.detail.value;
        const newList = $timers.slice();
        newList[index] = timer;

        updateList(newList);
    }

    function onTickActions(index: number, event: CustomEvent<{
        value: Action[];
    }>): void {
        const timer = { ...$timers[index] };
        timer.tick_actions = event.detail.value;
        const newList = $timers.slice();
        newList[index] = timer;

        updateList(newList);
    }

    function onEndActions(index: number, event: CustomEvent<{
        value: Action[];
    }>): void {
        const timer = { ...$timers[index] };
        timer.end_actions = event.detail.value;
        const newList = $timers.slice();
        newList[index] = timer;

        updateList(newList);
    }

    function deleteTimer(index: number): void {
        const newList = $timers.slice();
        newList.splice(index, 1);

        updateList(newList);
    }
</script>

<PanelTitle title={$l10nString('timersTitle')} />

<div class="timers">
    {#each $timers as timer, index (timer.__id)}
        <div
            class="timers__item"
            class:timers__item_disabled={$readOnly}
            transition:slide|local
        >
            <Spoiler2 open={!timer.id} mix="timers__spoiler">
                <div
                    slot="title"
                    class="timers__title"
                    class:timers__title_empty={!timer.id}
                >
                    <span class="timers__id" title={timer.id}>
                        {timer.id || $l10nString('timersEmptyId')}
                    </span>
                </div>
                <div class="timers__form">
                    <!-- svelte-ignore a11y-label-has-associated-control -->
                    <label class="timers__form-row">
                        <div class="timers__label">
                            {$l10nString('timersId')}
                        </div>
                        <Text
                            value={timer.id}
                            pattern="[a-zA-Z_][a-zA-Z_0-9.]*"
                            placeholder="[a-zA-Z_][a-zA-Z_0-9.]*"
                            disabled={$readOnly}
                            on:change={event => onIdChange(index, event)}
                        />
                    </label>
                    <!-- svelte-ignore a11y-label-has-associated-control -->
                    <label class="timers__form-row">
                        <div class="timers__label">
                            {$l10nString('timersValueVariable')}
                        </div>
                        <Text
                            value={timer.value_variable}
                            disabled={$readOnly}
                            on:change={event => onValueVariableChange(index, event)}
                        />
                    </label>
                    <!-- svelte-ignore a11y-label-has-associated-control -->
                    <label class="timers__form-row">
                        <div class="timers__label">
                            {$l10nString('timersDuration')}
                        </div>
                        <Text
                            value={timer.duration}
                            disabled={$readOnly}
                            on:change={event => onDurationChange(index, event)}
                        />
                    </label>
                    <!-- svelte-ignore a11y-label-has-associated-control -->
                    <label class="timers__form-row">
                        <div class="timers__label">
                            {$l10nString('timersTickInterval')}
                        </div>
                        <Text
                            value={timer.tick_interval}
                            disabled={$readOnly}
                            on:change={event => onIntervalChange(index, event)}
                        />
                    </label>
                    <div class="timers__form-row">
                        <div class="timers__label timers__label_compact">
                            {$l10nString('timersTickActions')}
                        </div>
                        <Actions2Prop
                            item={{
                                type: 'actions2'
                            }}
                            value={timer.tick_actions}
                            on:change={event => onTickActions(index, event)}
                        />
                    </div>
                    <div class="timers__form-row">
                        <div class="timers__label timers__label_compact">
                            {$l10nString('timersEndActions')}
                        </div>
                        <Actions2Prop
                            item={{
                                type: 'actions2'
                            }}
                            value={timer.end_actions}
                            on:change={event => onEndActions(index, event)}
                        />
                    </div>
                </div>
            </Spoiler2>

            {#if !$readOnly}
                <button
                    class="timers__delete"
                    aria-label={$l10nString('delete')}
                    data-custom-tooltip={$l10nString('delete')}
                    on:click|stopPropagation|preventDefault={() => deleteTimer(index)}
                ></button>
            {/if}
        </div>
    {/each}

    {#if !$readOnly}
        <AddButton
            cls="timers__add"
            disabled={$readOnly}
            on:click={add}
        >
            {$l10nString('timersAdd')}
        </AddButton>
    {/if}

    <div class="timers__spacer"></div>
</div>

<style>
    .timers {
        display: flex;
        flex-direction: column;
        height: 100%;
    }

    .timers__item {
        display: flex;
        flex: 0 0 auto;
        gap: 4px;
        align-items: flex-start;
        padding: 0 4px 12px 20px;
    }

    .timers__item_disabled {
        padding-right: 20px;
    }

    :global(.timers__spoiler) {
        flex: 1 1 auto;
    }

    .timers__title {
        display: flex;
        font-size: 14px;
        line-height: 20px;
    }

    .timers__id {
        flex: 0 0 auto;
        max-width: 50%;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .timers__title_empty {
        font-style: italic;
    }

    .timers__form-row {
        display: block;
        margin-bottom: 16px;
    }

    .timers__form-row:last-child {
        margin-bottom: 0;
    }

    .timers__label {
        margin-bottom: 11px;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-secondary);
    }

    .timers__label_compact {
        margin-bottom: 7px;
    }

    :global(.timers__add) {
        flex: 0 0 auto;
        align-self: flex-start;
        margin-right: 20px;
        margin-left: 20px;
    }

    .timers__spacer {
        height: 20px;
        flex: 0 0 auto;
    }

    .timers__delete {
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

    .timers__delete::before {
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

    .timers__delete:hover {
        background-color: var(--fill-transparent-1);
    }

    .timers__delete:active {
        background-color: var(--fill-transparent-2);
    }

    .timers__delete:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }
</style>
