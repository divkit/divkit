<script lang="ts">
    import { getContext } from 'svelte';
    import capitalize from '../utils/capitalize';
    import { compareVersions } from '../utils/compareVersions';
    import FeaturesText from './FeaturesText.svelte';
    import Spoiler from './Spoiler.svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';

    interface FeatureCompat {
        version_added?: string;
        version_removed?: string;
        notes?: string;
        partial_implementation?: boolean;
    }

    interface Feature {
        id: string;
        name: string;
        compat: Record<string, FeatureCompat | FeatureCompat[]>;
        status?: {
            deprecated?: boolean;
            notes?: string;
        };
    }

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);

    const compatData = require.context('../../../../compat_data/', false, /\.json$/);
    const features: Feature[] = [];
    compatData.keys().forEach(key => {
        const json = compatData(key);
        features.push(...json.features);
    });

    const searchParams = new URLSearchParams(location.search);
    let search = searchParams.get('query') || '';
    let feat = searchParams.get('feat') || '';

    const version = process.env.VERSION;
    const PLATFORMS = ['android', 'ios', 'web', 'flutter'] as const;
    const platformsWithoutFlutter = PLATFORMS.filter(it => it !== 'flutter');

    type Support = 'no-support' | 'partial' | 'full' | 'deprecated' | 'removed' | 'removed-partial';

    const SUPPORT_TO_INDEX: Record<Support, number> = {
        deprecated: -1,
        'no-support': 0,
        'removed-partial': 1,
        removed: 2,
        partial: 3,
        full: 4
    };

    const findCurrentSupport = (platformSupport: FeatureCompat | FeatureCompat[] | undefined): Support => {
        let best: Support = 'no-support';

        if (!platformSupport) {
            return best;
        }

        if (!Array.isArray(platformSupport)) {
            platformSupport = [platformSupport];
        }

        platformSupport.forEach(item => {
            let actual = true;

            if (item.version_added && compareVersions(item.version_added, version) > 0) {
                actual = false;
            } else if (item.version_removed && compareVersions(item.version_removed, version) < 0) {
                actual = false;
            } else if (!item.version_added && !item.version_removed) {
                actual = false;
            }

            if (actual) {
                const status: Support = item.partial_implementation ? 'partial' : 'full';

                if (SUPPORT_TO_INDEX[status] > SUPPORT_TO_INDEX[best]) {
                    best = status;
                }
            }
        });

        return best;
    };

    $: filtered = version ? Array.from(features.filter(it => {
        return feat ? it.id === feat : it.name.toLowerCase().indexOf(search.toLowerCase()) > -1;
    }).map(it => {
        const statuses = PLATFORMS.reduce((acc, platform) => {
            acc[platform] = findCurrentSupport(it.compat[platform]);
            return acc;
        }, {} as Record<string, Support>);
        let finalStatus: Support = 'no-support';

        if (it.status?.deprecated) {
            finalStatus = 'deprecated';
        } else if (platformsWithoutFlutter.every(it => statuses[it] === 'full')) {
            finalStatus = 'full';
        } else if (PLATFORMS.some(it => statuses[it] === 'full' || statuses[it] === 'partial')) {
            finalStatus = 'partial';
        }

        const perPlatformStatus: Record<string, {
            items: ({
                status: Exclude<Support, 'removed' | 'removed-partial'>,
                text: string;
                notes?: string;
            } | {
                status: 'removed' | 'removed-partial',
                texts: string[];
                notes?: string;
            })[];
        }> = {};
        const notesList: string[] = [];
        PLATFORMS.forEach(platform => {
            let list = it.compat[platform];
            if (!list) {
                if (platform !== 'flutter') {
                    perPlatformStatus[platform] = {
                        items: [{
                            status: 'no-support',
                            text: 'no support'
                        }]
                    };
                }
                return;
            }
            if (!Array.isArray(list)) {
                list = [list];
            }

            perPlatformStatus[platform] = {
                items: list.map(it => {
                    if (it.notes) {
                        notesList.push(it.notes);
                    }

                    if (it.version_removed) {
                        return {
                            status: it.partial_implementation ? 'removed-partial' : 'removed',
                            texts: [`${it.version_added || '…'}`, `${it.version_removed || '…'}`],
                            notes: it.notes
                        };
                    }

                    if (!it.version_added && !it.version_removed) {
                        return {
                            status: 'no-support',
                            text: 'no support',
                            notes: it.notes
                        };
                    }

                    return {
                        status: it.partial_implementation ? 'partial' : 'full',
                        text: `${it.version_added || '…'} — ${it.version_removed || '…'}`,
                        notes: it.notes
                    };
                })
            };
        });

        return {
            perPlatformStatus,
            finalStatus,
            feature: it,
            notes: it.status?.notes,
            notesList
        };
    }).reduce((acc, item) => {
        const id = item.feature.id;

        if (acc.has(id)) {
            console.warn('Duplicate feature: ' + id);
        } else {
            acc.set(id, item);
        }

        return acc;
    }, new Map()).values()) : [];

    function onInput(): void {
        feat = '';
        history.pushState(null, document.title, `/features?&query=${encodeURIComponent(search)}`);
    }
</script>

<div class="features">
    <input
        bind:value={search}
        class="features__search"
        type="search"
        placeholder={$l10n('search')}
        on:input={onInput}
    >

    {#if filtered.length}
        <ul class="features__list">
            {#each filtered as item (item.feature.id)}
                <li
                    class="features__item features__item_status_{item.finalStatus}"
                >
                    <a
                        class="features__link"
                        href="/features?feat={item.feature.id}"
                    >
                        #
                    </a>
                    <Spoiler open={Boolean(feat)}>
                        <div slot="title" class="features__title">
                            <FeaturesText text={item.feature.name} />
                        </div>

                        <div class="features__content">
                            {#if item.notes}
                                <div class="features__notes">
                                    <FeaturesText text={item.notes} />
                                </div>
                            {/if}

                            <div class="features__support">
                                {#each PLATFORMS as platform}
                                    {#if item.perPlatformStatus[platform]}
                                        <div class="features__platform-group">
                                            <div
                                                class="features__platform features__platform_type_{platform}"
                                                title={capitalize(platform)}
                                            ></div>

                                            {#each item.perPlatformStatus[platform].items as info}
                                                <div
                                                    class="features__platform-info features__platform-info_status_{info.status}"
                                                    title={info.notes}
                                                >
                                                    {#if 'text' in info}
                                                        {info.text}
                                                    {:else if 'texts' in info}
                                                        {#each info.texts as text, index}
                                                            <div class="features__platform-info-removed-part">
                                                                {text}

                                                                {#if index === 0}
                                                                    —
                                                                {/if}

                                                                {#if info.notes && index + 1 === info.texts.length}
                                                                    *
                                                                {/if}
                                                            </div>
                                                        {/each}
                                                    {/if}
                                                </div>
                                            {/each}
                                        </div>
                                    {/if}
                                {/each}
                            </div>

                            {#if item.notesList?.length}
                                <ul class="features__notes-list">
                                    {#each item.notesList as note}
                                        <li class="features__notes-item">
                                            <FeaturesText text={note} />
                                        </li>
                                    {/each}
                                </ul>
                            {/if}
                        </div>
                    </Spoiler>
                </li>
            {/each}
        </ul>
    {/if}
</div>

<style>
    .features {
        width: 100%;
        max-width: 1120px;
        margin: 30px auto;
        /* background: #333; */

        --caniuse-no-support: #f8886f;
        --caniuse-partial: #c6d74f;
        --caniuse-full: #5fd172;
        --caniuse-deprecated: #ababab;
    }

    .features__search {
        box-sizing: border-box;
        width: 100%;
        margin: 0;
        padding: 10px 24px;
        font-size: 20px;
        font-family: inherit;
        border-radius: 1024px;
        border: 2px solid #ccc;
        transition: border-color .15s ease-in-out;
    }

    .features__search:hover {
        border-color: #aaa;
    }

    .features__search:focus-visible {
        outline: none;
        border-color: #444;
    }

    .features__search::placeholder {
        color: #ccc;
    }

    .features__list {
        margin: 40px 0;
        padding: 20px 20px 20px 60px;
        list-style: none;
        background: var(--bg-secondary);
        border-radius: 24px;
    }

    .features__item {
        position: relative;
        border-left: 6px solid transparent;
        margin-bottom: 12px;
    }

    .features__item_status_no-support {
        border-left-color: var(--caniuse-no-support);
    }

    .features__item_status_partial {
        border-left-color: var(--caniuse-partial);
    }

    .features__item_status_full {
        border-left-color: var(--caniuse-full);
    }

    .features__item_status_deprecated {
        border-left-color: var(--caniuse-deprecated);
    }

    .features__link {
        position: absolute;
        left: -56px;
        width: 50px;
        height: 44px;
        font-size: 32px;
        line-height: 44px;
        border-radius: 12px 0 0 12px;
        color: inherit;
        text-align: center;
        text-decoration: none;
        transition: background-color .15s ease-in-out;
    }

    .features__link:hover {
        background: #ddd;
    }

    .features__title {
        font-size: 20px;
    }

    .features__content {
        display: flex;
        flex-direction: column;
    }

    .features__notes {
        margin: 20px 20px 0;
        padding: 16px 12px;
        background: #782b0d;
    }

    .features__support {
        display: flex;
        flex-wrap: wrap;
        gap: 16px;
        padding: 20px;
    }

    .features__platform {
        width: 32px;
        height: 32px;
        background: no-repeat 50% 50%;
        background-size: contain;
    }

    .features__platform_type_android {
        background-image: url(../assets/android.svg);
    }

    .features__platform_type_ios {
        background-image: url(../assets/ios.svg);
    }

    .features__platform_type_web {
        background-image: url(../assets/web.svg);
        background-size: 80%;
    }

    .features__platform_type_flutter {
        background-image: url(../assets/flutter.svg);
        background-size: 80%;
    }

    .features__platform-group {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        align-items: center;
    }

    .features__platform-info {
        display: flex;
        flex-direction: row;
        align-items: baseline;
        padding: 8px 12px;
        border-radius: 1024px;
    }

    .features__platform-info_status_no-support {
        background: var(--caniuse-no-support);
    }

    .features__platform-info_status_partial {
        background: var(--caniuse-partial);
    }

    .features__platform-info_status_full {
        background: var(--caniuse-full);
    }

    .features__platform-info_status_removed,
    .features__platform-info_status_removed-partial {
        padding: 0;
    }

    .features__platform-info-removed-part {
        padding: 8px 12px;
    }

    .features__platform-info-removed-part:first-child {
        border-radius: 8px 0 0 8px;
        background: var(--caniuse-full);
    }

    .features__platform-info_status_removed-partial .features__platform-info-removed-part:first-child {
        background: var(--caniuse-partial);
    }

    .features__platform-info-removed-part:last-child {
        border-radius: 0 8px 8px 0;
        background: var(--caniuse-no-support);
    }

    .features__notes-list {
        margin: 16px 0;
        list-style: disc;
    }
</style>
