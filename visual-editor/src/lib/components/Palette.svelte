<script lang="ts">
    import { writable, type Writable } from 'svelte/store';
    import { getContext, setContext } from 'svelte';
    import { type PaletteItem } from '../data/palette';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import RadioSelector from './controls/RadioSelector.svelte';
    import lightThemeIcon from '../../assets/lightTheme.svg?url';
    import darkThemeIcon from '../../assets/darkTheme.svg?url';
    import Spoiler2 from './controls/Spoiler2.svelte';
    import MoveList2 from './controls/MoveList2.svelte';
    import PaletteItemView from './PaletteItem.svelte';
    import { PALETTE_ERRORS_CTX, type PaletteErrorsContext } from '../ctx/paletteErrorsContext';
    import { AddPaletteItemCommand } from '../data/commands/addPaletteItem';
    import { DeletePaletteItemCommand } from '../data/commands/deletePaletteItem';
    import { ReorderPaletteItemCommand } from '../data/commands/reorderPaletteItem';
    import AddButton from './controls/AddButton.svelte';
    import { APP_CTX, type AppContext } from '../ctx/appContext';

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { palette, previewThemeStore, readOnly } = state;

    let errors: Writable<Map<string, string>> = writable(new Map());

    setContext<PaletteErrorsContext>(PALETTE_ERRORS_CTX, {
        errors
    });

    function add(): void {
        const id = state.genPaletteId();

        state.pushCommand(new AddPaletteItemCommand(id));
    }

    $: if ($palette) {
        revalidate();
    }

    function revalidate(): void {
        $errors.clear();
        const namesFound = new Map<string, string>();
        $palette.forEach(item => {
            let id;
            if (!item.name) {
                $errors.set(item.id, 'Name is empty');
            } else if ((id = namesFound.get(item.name))) {
                $errors.set(item.id, 'Duplicate color name');
                $errors.set(id, 'Duplicate color name');
            } else {
                namesFound.set(item.name, item.id);
                $errors.set(item.id, '');
            }
        });
    }

    function onReorder(event: CustomEvent<{
        values: PaletteItem[];
    }>): void {
        state.pushCommand(new ReorderPaletteItemCommand(event.detail.values.map(it => it.id)));
    }

    function onDelete(event: CustomEvent<{
        item: PaletteItem;
    }>): void {
        state.pushCommand(new DeletePaletteItemCommand(event.detail.item));
    }
</script>

<div class="palette">
    <div class="palette__title">
        <RadioSelector
            bind:value={$previewThemeStore}
            name="previewTheme"
            options={[{
                text: $l10n('lightTheme'),
                value: 'light',
                icon: lightThemeIcon,
                tooltipAlign: 'left'
            }, {
                text: $l10n('darkTheme'),
                value: 'dark',
                icon: darkThemeIcon
            }]}
            theme="normal"
            iconSize="20"
        />
    </div>

    <Spoiler2 theme="props">
        <div slot="title">
            {$l10n('localPalette')}
        </div>

        <div class="palette__list">
            <MoveList2
                values={$palette}
                itemView={PaletteItemView}
                readOnly={$readOnly}
                on:reorder={onReorder}
                on:delete={onDelete}
            />
        </div>

        {#if !$readOnly}
            <AddButton
                cls="palette__add"
                on:click={add}
                disabled={$readOnly}
            >
                {$l10n('addColor')}
            </AddButton>
        {/if}
    </Spoiler2>
</div>

<style>
    .palette__title {
        padding: 20px 20px 8px;
    }

    :global(.palette__add) {
        margin-right: 20px;
        margin-left: 20px;
    }
</style>
