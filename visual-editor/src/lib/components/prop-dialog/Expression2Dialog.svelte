<script lang="ts">
    import { getContext } from 'svelte';
    import ContextDialog from './ContextDialog.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import { APP_CTX, type AppContext, type Expression2ShowProps } from '../../ctx/appContext';
    import type { DivEditorInstance } from '../../data/editor';

    const { state, shadowRoot } = getContext<AppContext>(APP_CTX);
    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { themeStore } = state;

    $: if (isShown && currentProps && !currentProps.disabled && currentProps.callback) {
        currentProps.callback(currentProps.value);
    }

    export function show(props: Expression2ShowProps): void {
        currentProps = props;
        isShown = true;

        if (!editorPromise) {
            editorPromise = import('../../data/editor');
        }

        editorPromise.then(res => {
            if (currentProps !== props) {
                return;
            }

            res.createDivEditor({
                node: editorRoot,
                shadowRoot,
                readOnly: props.disabled,
                value: props.value,
                theme: $themeStore,
                onChange(value) {
                    if (props.callback) {
                        props.callback(value);
                    }
                },
                onEnter() {
                    dialog?.hide();
                },
            });
        });
    }

    export function hide(): void {
        divEditor?.destroy();

        isShown = false;
        currentProps = null;
    }

    let currentProps: Expression2ShowProps | null;
    let dialog: ContextDialog;
    let isShown = false;
    let editorRoot: HTMLDivElement;
    let editorPromise: Promise<typeof import('../../data/editor')> | undefined;
    let divEditor: DivEditorInstance | undefined;

    function onClose(): void {
        isShown = false;
    }
</script>

{#if isShown && currentProps}
    <ContextDialog
        bind:this={dialog}
        direction="down"
        overflow="visible"
        offsetY={-50}
        wide
        target={currentProps.target}
        on:close={onClose}
    >
        <div class="expression2-dialog__content">
            <div class="expression2-dialog__title">
                {$l10nString('expressionTitle')}
            </div>
            <div class="expression2-dialog__input-box" bind:this={editorRoot}>
            </div>
        </div>
    </ContextDialog>
{/if}

<style>
    .expression2-dialog__content {
        display: flex;
        flex-direction: column;
        gap: 8px;
        padding: 16px 20px 20px;
    }

    .expression2-dialog__title {
        font-size: 14px;
        font-weight: 500;
        line-height: 20px;
    }

    .expression2-dialog__input-box {
        position: relative;
    }
</style>
