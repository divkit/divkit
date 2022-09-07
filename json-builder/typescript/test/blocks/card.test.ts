import {
    divCard,
    DivContainer,
    DivGallery,
    DivImage,
    DivImageBackground,
    DivLinearGradient,
    DivSolidBackground,
    DivTabs,
    DivText,
    expression,
    fixed,
    IDivData,
    matchParent,
    reference,
    rewriteRefs,
    StringVariable,
    template,
    weighted,
    wrapContent,
} from '../../src';

describe('DivCard tests', (): void => {
    it('should create POI card', (): void => {
        const templates = {
            footer: new DivText({
                text: reference('footer_text'),
                font_size: 14,
                line_height: 16,
                text_color: '#999',
                margins: {
                    left: 16,
                    right: 16,
                },
                paddings: {
                    bottom: 12,
                    top: 12,
                },
                action: reference('footer_action_link'),
            }),
            poi_card: new DivContainer({
                orientation: 'vertical',
                background: [
                    new DivSolidBackground({
                        color: '#FFFFFF',
                    }),
                ],
                items: [
                    new DivContainer({
                        orientation: 'horizontal',
                        items: reference('title_items'),
                    }),
                    new DivTabs({
                        switch_tabs_by_content_swipe_enabled: 0,
                        title_paddings: {
                            left: 12,
                            right: 12,
                            bottom: 8,
                        },
                        tab_title_style: {
                            font_weight: 'medium',
                        },
                        items: reference('tab_items_link'),
                        dynamic_height: 1,
                    }),
                    template('footer'),
                ],
            }),
            star: new DivImage({
                width: fixed(14),
                height: fixed(14),
                image_url: reference('image_url'),
            }),
            star_full: template('star', {
                image_url:
                    'https://avatars.mds.yandex.net/get-bass/787408/poi_48x48_ee9550bc195fdc5d7c1d281ea5d8d776320345e0a67b0663c4fdde14e194393b.png/orig',
            }),
            star_half: template('star', {
                image_url:
                    'https://avatars.mds.yandex.net/get-bass/469429/poi_48x48_188933e7030027690ed55b5614b60fa77e0e4b50b86dde48d166714096ed0b0e.png/orig',
            }),
            star_empty: template('star', {
                image_url:
                    'https://avatars.mds.yandex.net/get-bass/397492/poi_48x48_4ce4cec5ea8f8336bc3792a4899c1e9958531fcf9f8aabc4dd319ddaf5deafa0.png/orig',
            }),
            poi_gallery_item: new DivContainer({
                orientation: 'vertical',
                width: fixed(244),
                height: fixed(240, 'sp'),
                border: {
                    corner_radius: 6,
                },
                paddings: {
                    left: 12,
                    right: 12,
                    top: 16,
                    bottom: 14,
                },
                background: [
                    new DivImage({
                        content_alignment_vertical: 'top',
                        image_url: reference('background_url'),
                    }),
                    new DivLinearGradient({
                        angle: 270,
                        colors: ['#00293445', '#293445'],
                    }),
                ],
                action: reference('poi_gallery_item_action_link'),

                items: [
                    new DivText({
                        text: reference('badge_text'),
                        font_size: 13,
                        text_color: '#fff',
                        line_height: 16,
                        width: wrapContent(),
                        max_lines: 1,
                        alignment_horizontal: 'right',
                        background: [new DivSolidBackground({ color: '#3A4F71' })],
                        border: {
                            corner_radius: 4,
                        },
                        paddings: {
                            bottom: 4,
                            top: 4,
                            left: 6,
                            right: 6,
                        },
                    }),
                    new DivText({
                        text: reference('place_category'),
                        font_size: 11,
                        text_color: '#FFFFFF',
                        line_height: 12,
                        max_lines: 6,
                        letter_spacing: 0.75,
                        text_alignment_vertical: 'bottom',
                        height: weighted(1),
                        paddings: {
                            top: 8,
                        },
                    }),
                    new DivText({
                        text: reference('place_title'),
                        font_size: 16,
                        text_color: '#FFFFFF',
                        line_height: 20,
                        max_lines: 6,
                        paddings: {
                            top: 8,
                        },
                    }),
                    new DivText({
                        text: reference('address'),
                        font_size: 13,
                        text_color: '#99FFFFFF',
                        line_height: 16,
                        max_lines: 6,
                        paddings: {
                            top: 8,
                        },
                    }),
                    new DivContainer({
                        items: reference('poi_stars'),
                        orientation: 'horizontal',
                        content_alignment_vertical: 'center',
                        paddings: {
                            top: 8,
                        },
                    }),
                    new DivContainer({
                        orientation: 'horizontal',
                        content_alignment_vertical: 'center',
                        paddings: {
                            top: 11,
                        },
                        items: [
                            new DivText({
                                text: reference('time'),
                                font_size: 11,
                                text_color: '#99FFFFFF',
                                line_height: 12,
                                max_lines: 1,
                                letter_spacing: 0.75,
                                width: weighted(1),
                            }),
                            new DivImage({
                                image_url: 'http://imgur.com/7y1xr5j.png',
                                width: fixed(16),
                                height: fixed(16),
                            }),
                            new DivText({
                                text: reference('distance'),
                                font_size: 13,
                                text_color: '#99FFFFFF',
                                max_lines: 1,
                                width: wrapContent(),
                            }),
                        ],
                    }),
                ],
            }),
            gallery_tail_light: new DivContainer({
                width: fixed(104),
                height: weighted(0),
                action: reference('gallery_tail_action_link'),
                content_alignment_horizontal: 'center',
                content_alignment_vertical: 'center',
                items: [
                    new DivImage({
                        width: fixed(40),
                        height: fixed(40),
                        border: {
                            corner_radius: 20,
                            stroke: {
                                color: '#DCDEE0',
                            },
                        },
                        background: [new DivSolidBackground({ color: '#ffffff' })],
                        placeholder_color: '#00ffffff',
                        image_url: 'https://i.imgur.com/CPmGi24.png',
                    }),
                    new DivText({
                        text: reference('tail_text_link'),
                        font_size: 14,
                        text_color: '#6b7a80',
                        line_height: 16,
                        text_alignment_horizontal: 'center',
                        height: wrapContent(),
                        paddings: {
                            left: 0,
                            right: 0,
                            top: 10,
                            bottom: 0,
                        },
                    }),
                ],
            }),
            title_text: new DivText({
                text: reference('text'),
                paddings: {
                    left: 16,
                    top: 12,
                    right: 16,
                },
                font_size: 18,
                font_weight: 'medium',
                line_height: 24,
                text_color: '#CC000000',
            }),
            title_menu: new DivImage({
                width: fixed(44),
                height: fixed(44),
                image_url: 'https://i.imgur.com/qNJQKU8.png',
            }),
        };

        const divData: IDivData = {
            log_id: 'poi_card',
            states: [
                {
                    state_id: 1,
                    div: template('poi_card', {
                        title_items: [
                            template('title_text', {
                                text: 'Рядом с вами',
                            }),
                            template('title_menu', {
                                action: {
                                    log_id: 'menu',
                                    menu_items: [
                                        {
                                            text: 'Настройки ленты',
                                            action: {
                                                url: 'http://ya.ru',
                                                log_id: 'settings',
                                            },
                                        },
                                        {
                                            text: 'Скрыть карточку',
                                            action: {
                                                url: 'http://ya.ru',
                                                log_id: 'hide',
                                            },
                                        },
                                    ],
                                },
                            }),
                        ],
                        footer_text: 'ОТКРЫТЬ КАРТЫ',
                        tab_items_link: [
                            {
                                title: 'ПОПУЛЯРНОЕ',
                                div: new DivGallery({
                                    width: weighted(40),
                                    height: fixed(240, 'sp'),
                                    paddings: {
                                        left: 16,
                                        right: 16,
                                    },
                                    items: [
                                        template('poi_gallery_item', {
                                            badge_text: 'Лучшее',
                                            background_url:
                                                'https://avatars.mds.yandex.net/get-pdb/1340633/88a085e7-7254-43ff-805a-660b96f0e6ce/s1200?webp=false',
                                            place_category: 'РЕСТОРАН',
                                            place_title: 'Кулинарная лавка',
                                            address: 'улица Тимура Фрунзе, 11, корп. 8',
                                            time: 'ДО 23:00',
                                            distance: '150м',
                                            poi_stars: [
                                                {
                                                    type: 'star_full',
                                                },
                                                {
                                                    type: 'star_full',
                                                },
                                                {
                                                    type: 'star_full',
                                                },
                                                {
                                                    type: 'star_half',
                                                },
                                                {
                                                    type: 'star_empty',
                                                },
                                            ],
                                            poi_gallery_item_action_link: 'ya.ru',
                                        }),
                                        template('poi_gallery_item', {
                                            badge_text: 'Лучшее',
                                            background_url:
                                                'https://avatars.mds.yandex.net/get-pdb/1340633/88a085e7-7254-43ff-805a-660b96f0e6ce/s1200?webp=false',
                                            place_category: 'КОНЦЕРТ',
                                            place_title: 'Black Label Society',
                                            address: 'чт 15 февраля',
                                            time: 'Главclub Green Concert',
                                            distance: '150м',
                                            poi_stars: [],
                                            poi_gallery_item_action_link: 'ya.ru',
                                        }),
                                        template('gallery_tail_light', {
                                            tail_text_link: 'Ещё на картах',
                                            visibility_action: {
                                                log_id: 'a66',
                                                visibility_duration: 10000,
                                            },
                                            gallery_tail_action_link: 'ya.ru',
                                        }),
                                    ],
                                }),
                            },
                        ],
                        footer_action_link: 'ya.ru',
                    }),
                },
            ],
        };

        expect(divCard(rewriteRefs(templates), divData)).toMatchSnapshot();
    });

    it('should create Znatoki card', (): void => {
        const templates = {
            znatoki_question: new DivText({
                text: reference('question_title'),
                alignment_vertical: 'center',
                font_size: 16,
                line_height: 20,
                text_color: '#ffffff',
                height: wrapContent(),
                paddings: {
                    left: 12,
                    right: 12,
                    top: 8,
                    bottom: 8,
                },
                max_lines: 4,
                font_weight: 'bold',
            }),
            znatoki_card: new DivContainer({
                orientation: 'vertical',
                height: wrapContent(),
                width: fixed(272),
                border: {
                    corner_radius: 6,
                    stroke: {
                        color: '#DCDEE0',
                        width: 1,
                    },
                },
                background: [new DivSolidBackground({ color: '#ffffff' })],
                items: [
                    new DivContainer({
                        background: [
                            new DivSolidBackground({ color: '#e9edf2' }),
                            new DivImageBackground({ image_url: reference('question_cover_url') }),
                            new DivSolidBackground({ color: '#80000000' }),
                        ],
                        height: fixed(96, 'sp'),
                        width: matchParent(),
                        content_alignment_vertical: 'center',
                        items: [template('znatoki_question')],
                    }),
                    new DivContainer({
                        paddings: {
                            left: 12,
                            right: 12,
                            top: 12,
                            bottom: 12,
                        },
                        items: [
                            new DivContainer({
                                orientation: 'horizontal',
                                content_alignment_vertical: 'center',
                                paddings: {
                                    bottom: 4,
                                },
                                items: [
                                    new DivImage({
                                        image_url: reference('author_avater_url'),
                                        width: fixed(32),
                                        height: fixed(32),
                                        border: {
                                            corner_radius: 16,
                                        },
                                        alignment_vertical: 'center',
                                    }),
                                    new DivContainer({
                                        paddings: {
                                            left: 8,
                                            right: 8,
                                        },
                                        items: [
                                            new DivText({
                                                text: reference('author_name'),
                                            }),
                                        ],
                                    }),
                                ],
                            }),
                            new DivText({
                                text: reference('answer_text'),
                            }),
                            new DivContainer({
                                orientation: 'horizontal',
                                content_alignment_vertical: 'center',
                                items: [
                                    new DivImage({
                                        image_url:
                                            'https://avatars.mds.yandex.net/get-znatoki/1649112/2a0000016c29fff42cb526006c69e437cd43/orig',
                                        width: fixed(16),
                                        height: fixed(16),
                                        alignment_vertical: 'center',
                                        placeholder_color: '#00ffffff',
                                        alpha: 0.24,
                                    }),
                                    new DivText({
                                        text: reference('answers_more'),
                                    }),
                                ],
                            }),
                        ],
                    }),
                ],
            }),
        };

        const divData: IDivData = {
            log_id: 'znatoki_card',
            states: [
                {
                    state_id: 1,
                    div: template('znatoki_card', {
                        author_name: 'name',
                        question_cover_url: 'ya.ru',
                        question_title: 'title',
                        author_avater_url: 'ya.ru',
                        answer_text: 'text',
                        answers_more: 'more',
                    }),
                },
            ],
        };

        expect(divCard(rewriteRefs(templates), divData)).toMatchSnapshot();
    });

    it('should create Market card', (): void => {
        const title = new DivText({
            text: reference('text'),
            paddings: {
                left: 22,
                top: 14,
                right: 22,
                bottom: 4,
            },
            font_size: 18,
            font_weight: 'bold',
            text_color: '#000000',
        });

        const templates = {
            title_text: title,
            market_card_container: new DivContainer({
                alignment_vertical: reference('align'),
                orientation: 'vertical',
                background: [
                    new DivSolidBackground({
                        color: reference('bg_color'),
                    }),
                ],
                items: reference('content'),
            }),
            market_card: template('market_card_container', {
                align: 'center',
                content: [
                    new DivContainer({
                        orientation: 'horizontal',
                        items: reference('title_items'),
                    }),
                    new DivGallery({
                        items: reference('cards'),
                    }),
                ],
            }),
            market_item: new DivContainer({
                orientation: 'vertical',
                width: fixed(148),
                height: fixed(222, 'sp'),
                border: {
                    corner_radius: 6,
                    has_shadow: 1,
                },
                margins: {
                    top: 10,
                    bottom: 15,
                },
                action: reference('action_link'),

                items: [
                    new DivImage({
                        image_url: reference('logo'),
                        width: matchParent(),
                        height: fixed(108),
                        scale: 'fit',
                        margins: {
                            top: reference('logo_margin_top'),
                            bottom: 6,
                        },
                    }),
                    new DivText({
                        text: reference('price'),
                        font_size: 14,
                        text_color: '#000000',
                        line_height: 18,
                        margins: {
                            top: 6,
                            left: 12,
                        },
                        font_weight: 'medium',
                        ranges: [
                            {
                                start: reference('custom_range_start'),
                                end: reference('custom_range_end'),
                                text_color: reference('custom_range_color'),
                            },
                        ],
                    }),
                    new DivText({
                        text: reference('desc'),
                        font_size: 14,
                        text_color: '#000000',
                        line_height: 18,
                        max_lines: 2,
                        margins: {
                            top: 2,
                            left: 12,
                            right: 12,
                        },
                    }),
                    new DivText({
                        text: reference('host'),
                        font_size: 14,
                        text_color: '#000000',
                        alpha: 0.4,
                        line_height: 16,
                        max_lines: 1,
                        margins: {
                            top: 3,
                            left: 12,
                            right: 12,
                        },
                    }),
                ],
            }),
        };

        const divData: IDivData = {
            log_id: 'market_card',
            states: [
                {
                    state_id: 1,
                    div: template('market_card', {
                        bg_color: 'ya.ru',
                        title_items: [
                            template('title_text', {
                                text: 'Маркет',
                                action: {
                                    log_id: 'market.title',
                                    url: 'https://m.market.yandex.ru?pp=110&clid=903&distr_type=7',
                                },
                            }),
                        ],
                        cards: [
                            template('market_item', {
                                action_link: {
                                    log_id: 'link',
                                    url: 'ya.ru',
                                },
                                price: '1 000 000$',
                                desc: 'Дрова',
                                host: 'Село',
                                logo: 'Дерево',
                                logo_margin_top: 6,
                                custom_range_start: 1,
                                custom_range_end: 3,
                                custom_range_color: 'red',
                            }),
                        ],
                        margins: {
                            bottom: 10,
                        },
                    }),
                },
            ],
        };

        expect(divCard(rewriteRefs(templates), divData)).toMatchSnapshot();
    });

    it('should escape unsafe chars', (): void => {
        const divData: IDivData = {
            log_id: 'test',
            variables: [
                new StringVariable({
                    name: 'variable',
                    value: 'value',
                }),
            ],
            states: [
                {
                    state_id: 1,
                    div: new DivContainer({
                        items: [
                            new DivText({
                                text: 'Hello \\\\ unsafe @{variable}',
                            }),
                            new DivText({
                                text: expression('Hello \\\\ safe @{variable}'),
                            }),
                        ],
                    }),
                },
            ],
        };

        expect(divCard(rewriteRefs({}), divData)).toMatchSnapshot();
    });
});
