<script lang="ts" context="module">
    const MAX_GRADIENT_COLORS = 10;
</script>

<script lang="ts">
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import Select from '../Select.svelte';
    import ColorSelect2 from '../ColorSelect2.svelte';
    import Text from '../controls/Text.svelte';
    import RadioSelector from '../controls/RadioSelector.svelte';
    import gradientStopImage from '../../../assets/gradientStop.svg?raw';
    import fitIconLight from '../../../assets/fitLight.svg?url';
    import fillIconLight from '../../../assets/fillLight.svg?url';
    import noScaleIconLight from '../../../assets/noScaleLight.svg?url';
    import stretchIconLight from '../../../assets/stretchLight.svg?url';
    import fitIconDark from '../../../assets/fitDark.svg?url';
    import fillIconDark from '../../../assets/fillDark.svg?url';
    import noScaleIconDark from '../../../assets/noScaleDark.svg?url';
    import stretchIconDark from '../../../assets/stretchDark.svg?url';
    import Alignment from '../controls/Alignment.svelte';
    import ContextDialog from './ContextDialog.svelte';
    import { colorToCss, isPaletteColor, paletteIdToValue, valueToPaletteId } from '../../data/palette';
    import PaletteSelector from '../PaletteSelector.svelte';
    import ColorPreview from '../ColorPreview.svelte';
    import { APP_CTX, type AppContext, type Background2DialogShowProps } from '../../ctx/appContext';
    import { gradientToList, isEqualDistribution, sortColorMap, type Background, type GradientBackground, type RadialBackground, type SolidBackground } from '../../data/background';
    import { deleteComponent } from '../../utils/keybinder/shortcuts';

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const {
        palette,
        paletteEnabled,
        previewThemeStore,
        highlightMode,
        highlightGradientAngle,
        selectedLeaf
    } = state;

    $: if (isShown && !readOnly && callback) {
        if (value.type === 'gradient' || value.type === 'radial_gradient') {
            if (isEqualDistribution(gradientColors)) {
                delete value.color_map;
                value.colors = gradientColors.map(it => it.color);
            } else {
                delete value.colors;
                value.color_map = gradientColors;
            }
        }

        callback(value);
    }

    // todo init gradientColors on open

    $: if (value && subtype) {
        if (subtype !== value.type) {
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            const val = value as any;
            const prevColor = val.color;
            const prevColors = val.colors;

            delete val.color;
            delete val.colors;
            delete val.image_url;
            delete val.scale;
            delete val.content_alignment_horizontal;
            delete val.content_alignment_vertical;
            delete val.alpha;
            val.type = subtype;

            if (subtype === 'solid') {
                if (Array.isArray(prevColors) && prevColors.length) {
                    val.color = prevColors[0];
                } else {
                    val.color = '#000';
                }
            } else if (subtype === 'gradient' || subtype === 'radial_gradient') {
                if (prevColor) {
                    val.colors = [prevColor, prevColor];
                } else {
                    val.colors = ['#fff', '#000'];
                }
                gradientColors = gradientToList(val);

                if (subtype === 'radial_gradient') {
                    val.radius = {
                        type: 'relative',
                        value: 'farthest_corner'
                    };
                    val.center_x = {
                        type: 'relative',
                        value: 0.5
                    };
                    val.center_y = {
                        type: 'relative',
                        value: 0.5
                    };
                }
            } else if (subtype === 'image') {
                val.image_url = '';
                val.scale = 'fill';
                val.content_alignment_horizontal = 'center';
                val.content_alignment_vertical = 'center';
                val.alpha = 1;
                val.preload_required = true;
            }

            value = val;
        }
    }

    $: if ($previewThemeStore && paletteId) {
        const paletteItem = $palette.find(it => it.id === paletteId);

        if (paletteItem) {
            if (subtype === 'solid' || subtype === 'gradient') {
                colorValue = paletteItem[$previewThemeStore];
            }
        }
    }

    export function show(props: Background2DialogShowProps): void {
        callback = props.callback;
        target = props.target;
        value = props.value;
        readOnly = props.readOnly;
        isShown = true;
        subtype = value.type;
        onSubtypeChange();
    }

    export function hide(): void {
        isShown = false;
        subtype = '';
    }

    let target: HTMLElement;
    let subtype = '';
    let isShown = false;
    let value: Background;
    let callback: ((val: Background) => void) | undefined;
    let selectedColorIndex = 0;
    let readOnly: boolean | undefined;
    let togglePalette = false;
    let paletteId = '';
    let colorValue = '';
    let gradientColors: {
        color: string;
        position: number;
    }[] = [];
    let cancel: (() => void) | undefined;
    let gradientRangeElem: HTMLElement;
    let isGradientColorsHovered = false;
    let gradientColorPosition = 0;

    function onClose(): void {
        isShown = false;

        cancel?.();
        cancel = undefined;
    }

    function onAngleFocus(): void {
        highlightMode.set('gradient');
        highlightGradientAngle.set((value as GradientBackground).angle || 0);
    }

    function onAngleBlur(): void {
        highlightMode.set('');
    }

    function onAngleChange(event: CustomEvent<{
        value: string;
    }>): void {
        const newAngle = Number(event.detail.value);

        if (value.type !== 'gradient') {
            return;
        }

        value.angle = newAngle;
        highlightGradientAngle.set(newAngle);
    }

    function onStopsCountChange(event: CustomEvent<{
        value: string;
    }>): void {
        let newCount = Number(event.detail.value);
        if (newCount < 2 || newCount > 10) {
            return;
        }

        newCount = Math.max(2, Math.min(10, newCount));

        if (value.type !== 'gradient') {
            return;
        }

        // todo return removed colors
        if (newCount > gradientColors.length) {
            gradientColors = gradientColors.slice();
            while (newCount > gradientColors.length) {
                gradientColors.push({
                    color: '#fff',
                    position: 0
                });
            }
        } else if (newCount < gradientColors.length) {
            gradientColors = gradientColors.slice(0, newCount);
        }
        gradientColors.forEach((it, index) => {
            it.position = index / (gradientColors.length - 1);
        });

        // todo remove single color
    }

    function onRadialGradientRadiusTypeChange(event: CustomEvent<string>): void {
        const val = value as RadialBackground;
        const radius = val.radius;

        if (radius) {
            if (event.detail === 'relative') {
                radius.value = 'farthest_corner';
            } else if (event.detail === 'fixed') {
                radius.value = 100;
            }

            value = val;
        }
    }

    function onRadialGradientCenterXTypeChange(event: CustomEvent<string>): void {
        const val = value as RadialBackground;
        const center = val.center_x;

        if (center) {
            const node = $selectedLeaf?.props.node;
            const bbox = node?.getBoundingClientRect();

            if (event.detail === 'relative') {
                center.value = Math.max(0, Math.min(1, center.value / bbox.width));
            } else if (event.detail === 'fixed') {
                center.value = Math.max(0, Math.floor(bbox.width * center.value));
            }

            value = val;
        }
    }

    function onRadialGradientCenterYTypeChange(event: CustomEvent<string>): void {
        const val = value as RadialBackground;
        const center = val.center_y;

        if (center) {
            const node = $selectedLeaf?.props.node;
            const bbox = node?.getBoundingClientRect();

            if (event.detail === 'relative') {
                center.value = Math.max(0, Math.min(1, center.value / bbox.height));
            } else if (event.detail === 'fixed') {
                center.value = Math.max(0, Math.floor(bbox.height * center.value));
            }

            value = val;
        }
    }

    function onPaletteToggle(): void {
        togglePalette = !togglePalette;
    }

    function onSolidColorChange(): void {
        paletteId = '';
        (value as SolidBackground).color = colorValue;
    }

    function onGradientColorChange(): void {
        paletteId = '';
        gradientColors[selectedColorIndex].color = colorValue;
    }

    function onGradientKeyDown(event: KeyboardEvent): void {
        if (deleteComponent.isPressed(event) && gradientColors.length > 2) {
            deleteGradientColorStop(selectedColorIndex);
        }
    }

    function onPaletteChange(event: CustomEvent<string>): void {
        if (subtype === 'solid') {
            (value as SolidBackground).color = paletteIdToValue(event.detail);
        } else if (subtype === 'gradient') {
            gradientColors[selectedColorIndex].color = paletteIdToValue(event.detail);
        }
    }

    function selectGradientIndex(index: number): void {
        selectedColorIndex = index;

        if (isPaletteColor(gradientColors[selectedColorIndex].color)) {
            togglePalette = true;
            paletteId = valueToPaletteId(gradientColors[selectedColorIndex].color);
        } else {
            colorValue = gradientColors[selectedColorIndex].color;
            paletteId = '';
        }
    }

    function deleteGradientColorStop(index: number): void {
        gradientColors = gradientColors.slice();
        gradientColors.splice(index, 1);
        selectGradientIndex(0);
    }

    function gradientColorsMouseMove(event: MouseEvent): void {
        if (gradientColors.length >= MAX_GRADIENT_COLORS) {
            isGradientColorsHovered = false;
            return;
        }
        isGradientColorsHovered = event.target === gradientRangeElem;

        const bbox = gradientRangeElem.getBoundingClientRect();
        gradientColorPosition = Math.max(0, Math.min(1, (event.pageX - bbox.left) / gradientRangeElem.offsetWidth));
    }

    function gradientColorsMouseLeave(): void {
        isGradientColorsHovered = false;
    }

    function createGradientColorStop(event: MouseEvent): void {
        if (gradientColors.length >= MAX_GRADIENT_COLORS || !isGradientColorsHovered) {
            return;
        }

        const bbox = gradientRangeElem.getBoundingClientRect();
        const position = Math.max(0, Math.min(1, (event.pageX - bbox.left) / gradientRangeElem.offsetWidth));

        gradientColors = [
            ...gradientColors,
            { color: '#fff', position }
        ];
        selectGradientIndex(gradientColors.length - 1);
    }

    function onGradientColorPointerDown(event: PointerEvent, index: number): void {
        selectGradientIndex(index);

        const startX = event.pageX;
        const startPosition = gradientColors[index].position;

        event.preventDefault();

        const onPointerMove = (event: PointerEvent) => {
            const diff = event.pageX - startX;
            const pos = Math.max(0, Math.min(1, startPosition + diff / gradientRangeElem.offsetWidth));

            gradientColors[index].position = pos;

            event.preventDefault();
        };
        const onPointerUp = () => {
            cancel?.();
            cancel = undefined;
        };

        window.addEventListener('pointermove', onPointerMove);
        window.addEventListener('pointerup', onPointerUp);
        window.addEventListener('pointercancel', onPointerUp);

        cancel?.();
        cancel = () => {
            window.removeEventListener('pointermove', onPointerMove);
            window.removeEventListener('pointerup', onPointerUp);
            window.removeEventListener('pointercancel', onPointerUp);
        };
    }

    function onSubtypeChange(): void {
        if (subtype === 'solid') {
            const val = (value as SolidBackground);

            if (isPaletteColor(val.color)) {
                togglePalette = true;
                paletteId = valueToPaletteId(val.color);
            } else {
                colorValue = val.color;
                paletteId = '';
            }
        } else if (subtype === 'gradient' || subtype === 'radial_gradient') {
            const val = (value as GradientBackground | RadialBackground);

            selectedColorIndex = 0;
            gradientColors = gradientToList(val);

            togglePalette = gradientColors.some(it => isPaletteColor(it.color));

            if (isPaletteColor(gradientColors[0].color)) {
                paletteId = valueToPaletteId(gradientColors[0].color);
            } else {
                colorValue = gradientColors[0].color;
                paletteId = '';
            }
        } else if (subtype === 'image') {
            togglePalette = false;
        }
    }
</script>

{#if isShown && target}
    <ContextDialog
        {target}
        overflow="visible"
        canMove={true}
        wide={togglePalette}
        on:close={onClose}
    >
        <div class="background2-dialog__panels">
            {#if togglePalette}
                <div class="background2-dialog__panel background2-dialog__panel_left">
                    <PaletteSelector
                        bind:paletteId={paletteId}
                        on:change={onPaletteChange}
                    />
                </div>
            {/if}

            <div class="background2-dialog__panel">
                <div class="background2-dialog__title">
                    <Select
                        mix="background2-dialog__subtype-select"
                        items={[{
                            value: 'solid',
                            text: $l10nString('props.background_solid')
                        }, {
                            value: 'gradient',
                            text: $l10nString('props.background_gradient')
                        }, {
                            value: 'radial_gradient',
                            text: $l10nString('props.background_radial_gradient')
                        }, {
                            value: 'image',
                            text: $l10nString('props.background_image')
                        }]}
                        bind:value={subtype}
                        on:change={onSubtypeChange}
                        theme="normal"
                        disabled={readOnly}
                    />

                    {#if $paletteEnabled && (subtype === 'solid' || subtype === 'gradient' || subtype === 'radial_gradient')}
                        <!-- svelte-ignore a11y_consider_explicit_label -->
                        <button
                            class="background2-dialog__palette-toggle"
                            class:background2-dialog__palette-toggle_toggled={togglePalette}
                            title={$l10nString('palette')}
                            on:click={onPaletteToggle}
                        >
                            <div class="background2-dialog__palette-icon"></div>
                        </button>
                    {/if}
                </div>

                <div class="background2-dialog__content background2-dialog__content_{subtype}">
                    {#if value.type === 'solid'}
                        <ColorSelect2
                            bind:value={colorValue}
                            disabled={readOnly}
                            on:change={onSolidColorChange}
                        />
                    {:else if value.type === 'gradient' || value.type === 'radial_gradient'}
                        {#if Array.isArray(gradientColors)}
                            <div class="background2-dialog__gradient-preview">
                                <!-- svelte-ignore a11y_no_noninteractive_tabindex -->
                                <!-- svelte-ignore a11y_no_static_element_interactions -->
                                <div
                                    class="background2-dialog__gradient-points"
                                    tabindex="0"
                                    bind:this={gradientRangeElem}
                                    on:mousemove={gradientColorsMouseMove}
                                    on:mouseleave={gradientColorsMouseLeave}
                                    on:click={createGradientColorStop}
                                    on:keydown={onGradientKeyDown}
                                >
                                    {#if isGradientColorsHovered}
                                        <div
                                            class="background2-dialog__gradient-point background2-dialog__gradient-point_ghost"
                                            style:left="{gradientColorPosition * 100}%"
                                            title={$l10nString('add_color')}
                                        ></div>
                                    {/if}

                                    <!-- todo keyboard support -->
                                    {#each gradientColors as item, index}
                                        <div
                                            class="background2-dialog__gradient-point"
                                            class:background2-dialog__gradient-point_selected={index === selectedColorIndex}
                                            style:left="{item.position * 100}%"
                                            title="{Math.round(item.position * 100)}%"
                                            on:pointerdown={event => onGradientColorPointerDown(event, index)}
                                        >
                                            <!-- eslint-disable-next-line svelte/no-at-html-tags -->
                                            {@html gradientStopImage}

                                            <ColorPreview color={item.color} mix="background2-dialog__gradient-point-preview" />

                                            {#if gradientColors.length > 2}
                                                <!-- svelte-ignore a11y_click_events_have_key_events -->
                                                <div
                                                    class="background2-dialog__gradient-point-delete"
                                                    title={$l10nString('delete')}
                                                    on:pointerdown|stopPropagation
                                                    on:click={() => deleteGradientColorStop(index)}
                                                ></div>
                                            {/if}
                                        </div>
                                    {/each}
                                </div>

                                <div class="background2-dialog__gradient-range">
                                    <div class="background2-dialog__gradient-range-bg"></div>
                                    <div
                                        class="background2-dialog__gradient-range-inner"
                                        style:background-image="linear-gradient(to right, {sortColorMap(gradientColors).map(item => `${colorToCss(item.color, true, $palette, $previewThemeStore)} ${item.position * 100}%`).join(', ')})"
                                    ></div>
                                </div>
                            </div>

                            <ColorSelect2
                                bind:value={colorValue}
                                disabled={readOnly}
                                on:change={onGradientColorChange}
                            />

                            <div class="background2-dialog__separator"></div>

                            {#if value.type === 'gradient'}
                                <div class="background2-dialog__split">
                                    <div class="background2-dialog__split-part">
                                        <label>
                                            <div class="background2-dialog__label">{$l10nString('props.background_gradient_angle')}</div>
                                            <Text
                                                value={value.angle || 0}
                                                subtype="angle"
                                                min={0}
                                                max={360}
                                                disabled={readOnly}
                                                on:change={onAngleChange}
                                                on:focus={onAngleFocus}
                                                on:blur={onAngleBlur}
                                            />
                                        </label>
                                    </div>
                                    <div class="background2-dialog__split-part">
                                        <label>
                                            <div class="background2-dialog__label">{$l10nString('props.background_gradient_count')}</div>
                                            <Text
                                                value={gradientColors.length}
                                                subtype="integer"
                                                min={2}
                                                max={MAX_GRADIENT_COLORS}
                                                disabled={readOnly}
                                                required
                                                on:change={onStopsCountChange}
                                            />
                                        </label>
                                    </div>
                                </div>
                            {:else if value.type === 'radial_gradient'}
                                {#if value.radius && value.center_x && value.center_y}
                                    <div class="background2-dialog__row">
                                        <div class="background2-dialog__subrow">
                                            <label>
                                                <div class="background2-dialog__label">{$l10nString('props.background_gradient_radius_type')}</div>
                                                <Select
                                                    mix="background2-dialog__subtype-select"
                                                    items={[{
                                                        value: 'fixed',
                                                        text: $l10nString('props.size_fixed')
                                                    }, {
                                                        value: 'relative',
                                                        text: $l10nString('props.size_relative')
                                                    }]}
                                                    bind:value={value.radius.type}
                                                    theme="normal"
                                                    size="medium"
                                                    disabled={readOnly}
                                                    on:change={onRadialGradientRadiusTypeChange}
                                                />
                                            </label>
                                        </div>

                                        <div class="background2-dialog__subrow">
                                            {#if (value.radius?.type === 'fixed')}
                                                <Text
                                                    bind:value={value.radius.value}
                                                    subtype="integer"
                                                    min={0}
                                                    disabled={readOnly}
                                                    required
                                                />
                                            {:else if value.radius?.type === 'relative'}
                                                <Select
                                                    mix="background2-dialog__subtype-select"
                                                    items={[{
                                                        value: 'nearest_corner',
                                                        text: $l10nString('props.background_gradient_nearest_corner')
                                                    }, {
                                                        value: 'farthest_corner',
                                                        text: $l10nString('props.background_gradient_farthest_corner')
                                                    }, {
                                                        value: 'nearest_side',
                                                        text: $l10nString('props.background_gradient_nearest_side')
                                                    }, {
                                                        value: 'farthest_side',
                                                        text: $l10nString('props.background_gradient_farthest_side')
                                                    }]}
                                                    bind:value={value.radius.value}
                                                    theme="normal"
                                                    size="medium"
                                                    disabled={readOnly}
                                                />
                                            {/if}
                                        </div>
                                    </div>

                                    <div class="background2-dialog__split">
                                        <div class="background2-dialog__split-part">
                                            <div class="background2-dialog__subrow">
                                                <label>
                                                    <div class="background2-dialog__label">{$l10nString('props.background_gradient_center_x')}</div>
                                                    <Select
                                                        mix="background2-dialog__subtype-select"
                                                        items={[{
                                                            value: 'fixed',
                                                            text: $l10nString('props.size_fixed')
                                                        }, {
                                                            value: 'relative',
                                                            text: $l10nString('props.size_relative')
                                                        }]}
                                                        bind:value={value.center_x.type}
                                                        theme="normal"
                                                        size="medium"
                                                        disabled={readOnly}
                                                        on:change={onRadialGradientCenterXTypeChange}
                                                    />
                                                </label>
                                            </div>

                                            <div class="background2-dialog__subrow">
                                                <Text
                                                    bind:value={value.center_x.value}
                                                    subtype={value.center_x.type === 'relative' ? 'percent' : 'integer'}
                                                    disabled={readOnly}
                                                />
                                            </div>
                                        </div>
                                        <div class="background2-dialog__split-part">
                                            <div class="background2-dialog__subrow">
                                                <label>
                                                    <div class="background2-dialog__label">{$l10nString('props.background_gradient_center_y')}</div>
                                                    <Select
                                                        mix="background2-dialog__subtype-select"
                                                        items={[{
                                                            value: 'fixed',
                                                            text: $l10nString('props.size_fixed')
                                                        }, {
                                                            value: 'relative',
                                                            text: $l10nString('props.size_relative')
                                                        }]}
                                                        bind:value={value.center_y.type}
                                                        theme="normal"
                                                        size="medium"
                                                        disabled={readOnly}
                                                        on:change={onRadialGradientCenterYTypeChange}
                                                    />
                                                </label>
                                            </div>

                                            <div class="background2-dialog__subrow">
                                                <Text
                                                    bind:value={value.center_y.value}
                                                    subtype={value.center_y.type === 'relative' ? 'percent' : 'integer'}
                                                    disabled={readOnly}
                                                />
                                            </div>
                                        </div>
                                    </div>
                                {/if}
                            {/if}
                        {/if}
                    {:else if value.type === 'image'}
                        <label>
                            <div class="background2-dialog__label">{$l10nString('props.image_url')}</div>
                            <Text
                                bind:value={value.image_url}
                                subtype="file"
                                fileType="image"
                                disabled={readOnly}
                            />
                        </label>

                        <div>
                            <div class="background2-dialog__label">{$l10nString('props.image_scale')}</div>

                            <RadioSelector
                                name="scale"
                                defaultValue="fill"
                                options={[{
                                    text: $l10nString('props.scale_fit'),
                                    value: 'fit',
                                    iconLight: fitIconLight,
                                    iconDark: fitIconDark
                                }, {
                                    text: $l10nString('props.scale_fill'),
                                    value: 'fill',
                                    iconLight: fillIconLight,
                                    iconDark: fillIconDark
                                }, {
                                    text: $l10nString('props.scale_no_scale'),
                                    value: 'no_scale',
                                    iconLight: noScaleIconLight,
                                    iconDark: noScaleIconDark
                                }, {
                                    text: $l10nString('props.scale_stretch'),
                                    value: 'stretch',
                                    iconLight: stretchIconLight,
                                    iconDark: stretchIconDark
                                }]}
                                theme="secondary"
                                customTooltips={false}
                                bind:value={value.scale}
                                disabled={readOnly}
                            />
                        </div>

                        <div>
                            <div class="background2-dialog__label">{$l10nString('props.content_alignment')}</div>

                            <Alignment
                                bind:horizontal={value.content_alignment_horizontal}
                                bind:vertical={value.content_alignment_vertical}
                                disabled={readOnly}
                            />
                        </div>

                        <div>
                            <div class="background2-dialog__label">{$l10nString('props.alpha')}</div>

                            <div class="background2-dialog__alpha">
                                <Text
                                    bind:value={value.alpha}
                                    subtype="percent"
                                    min={0}
                                    max={100}
                                    defaultValue={1}
                                    disabled={readOnly}
                                />
                            </div>
                        </div>
                    {/if}
                </div>
            </div>
        </div>
    </ContextDialog>
{/if}

<style>
    .background2-dialog__panels {
        display: flex;
        pointer-events: none;
    }

    .background2-dialog__panel {
        flex: 1 0 0;
        min-width: 0;
    }

    .background2-dialog__panel_left {
        display: flex;
        flex-direction: column;
        border-right: 1px solid var(--fill-transparent-3);
    }

    .background2-dialog__title {
        display: flex;
        gap: 12px;
        margin: 16px;
        pointer-events: auto;
    }

    :global(.background2-dialog__subtype-select) {
        flex: 1 1 auto;
    }

    .background2-dialog__content {
        margin-bottom: 24px;
        pointer-events: auto;
    }

    .background2-dialog__content_solid {
        margin-bottom: 32px;
    }

    .background2-dialog__content_image {
        display: flex;
        flex-direction: column;
        gap: 24px;
        margin-right: 16px;
        margin-left: 16px;
    }

    .background2-dialog__close {
        position: absolute;
        top: 6px;
        right: 6px;
        width: 44px;
        height: 44px;
        border: none;
        border-radius: 8px;
        background: none;
        appearance: none;
        cursor: pointer;
        transition: background-color .15s ease-in-out;
    }

    .background2-dialog__close:hover {
        background-color: var(--fill-transparent-1);
    }

    .background2-dialog__close::before {
        display: block;
        width: 100%;
        height: 100%;
        filter: var(--icon-filter);
        background: no-repeat 50% 50% url(../../../assets/closeDialog.svg);
        background-size: 15px;
        content: '';
    }

    .background2-dialog__gradient-range {
        position: relative;
        height: 16px;
        margin: 0 20px 20px;
        border-radius: 4px;
        overflow: hidden;
    }

    .background2-dialog__gradient-range-bg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: url(../../../assets/alpha.png);
        background-size: 6px;
        opacity: .3;
    }

    .background2-dialog__gradient-range-inner {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        border-radius: 4px;
        box-shadow: inset 0 0 0 1px var(--fill-transparent-3);
    }

    .background2-dialog__gradient-points {
        position: relative;
        height: 27px;
        margin: 0 24px 2px;
        outline: none;
        cursor: pointer;
    }

    .background2-dialog__gradient-point {
        position: absolute;
        width: 24px;
        height: 27px;
        margin-left: -12px;
        color: var(--fill-transparent-4);
        transition: color .15s ease-in-out;
        cursor: move;
    }

    .background2-dialog__gradient-point:hover {
        color: var(--fill-transparent-3);
    }

    .background2-dialog__gradient-point_selected,
    .background2-dialog__gradient-point_selected:hover {
        color: var(--accent-purple);
    }

    .background2-dialog__gradient-point_ghost {
        background-color: #fff;
        border-radius: 6px;
        opacity: .4;
        pointer-events: none;
    }

    :global(.background2-dialog__gradient-point-preview.background2-dialog__gradient-point-preview) {
        position: absolute;
        top: 2px;
        right: 2px;
        left: 2px;
        width: 20px;
        height: 20px;
        border-radius: 4px;
    }

    .background2-dialog__gradient-point-delete {
        position: absolute;
        top: -8px;
        right: -8px;
        width: 16px;
        height: 16px;
        background: no-repeat 50% 50% url(../../../assets/close.svg);
        background-size: contain;
        cursor: pointer;
        opacity: 0;
        transition: opacity .15s ease-in-out;
        filter: var(--icon-filter);
    }

    .background2-dialog__gradient-points:hover .background2-dialog__gradient-point-delete {
        opacity: .4;
    }

    .background2-dialog__gradient-point-delete.background2-dialog__gradient-point-delete:hover {
        opacity: 1;
    }

    .background2-dialog__separator {
        height: 1px;
        margin: 24px 0 20px;
        background: var(--fill-transparent-3);
    }

    .background2-dialog__split {
        display: flex;
        gap: 16px;
        margin: 0 16px;
    }

    .background2-dialog__split-part {
        flex: 1 0 0;
    }

    .background2-dialog__label {
        margin-bottom: 6px;
        font-size: 14px;
        color: var(--text-secondary);
    }

    .background2-dialog__row {
        margin: 0 16px 20px;
    }

    .background2-dialog__subrow + .background2-dialog__subrow {
        margin-top: 6px;
    }

    .background2-dialog__alpha {
        width: 72px;
    }

    .background2-dialog__palette-toggle {
        width: 32px;
        height: 32px;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 6px;
        background-color: var(--fill-transparent-minus-1);
        appearance: none;
        cursor: pointer;
        transition: border-color .15s ease-in-out;
        pointer-events: auto;
    }

    .background2-dialog__palette-toggle:hover {
        border-color: var(--fill-transparent-4);
    }

    .background2-dialog__palette-toggle:focus-visible {
        outline: 1px solid var(--accent-purple);
        outline-offset: 2px;
    }

    .background2-dialog__palette-toggle_toggled,
    .background2-dialog__palette-toggle_toggled:hover {
        border-color: transparent;
    }

    .background2-dialog__palette-toggle_toggled {
        background-color: var(--accent-purple);
    }

    .background2-dialog__palette-toggle_toggled:hover {
        background-color: var(--accent-purple-hover);
    }

    .background2-dialog__palette-icon {
        width: 100%;
        height: 100%;
        background: no-repeat 50% 50% url(../../../assets/palette.svg);
        background-size: 20px;
        filter: var(--icon-filter);
    }

    .background2-dialog__palette-toggle_toggled .background2-dialog__palette-icon {
        filter: none;
    }
</style>
