import { divCard, DivContainer, DivImage, DivText, fixed, IDivData, reference, rewriteRefs, template } from '../../src';

describe('Div templates test', (): void => {
    it('should create simple template', (): void => {
        const templates = {
            title_text: new DivText({
                text: reference('title_text'),
            }),
        };

        expect(rewriteRefs(templates)).toMatchSnapshot();
    });

    it('should create template with title menu and footer', (): void => {
        const templates = {
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
            footer: new DivText({
                text: reference('footer_text'),
                font_size: 12,
                line_height: 16,
                text_color: '#80000000',
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
        };

        expect({ templates: rewriteRefs(templates) }).toMatchSnapshot();
    });

    it('should create card with template which has more than one base template block', (): void => {
        const templates = {
            card: template('A', {
                alpha: reference('opacity'),
            }),
            A: template('B'),
            B: template('C', {
                orientation: reference('orient'),
            }),
            C: new DivContainer({
                items: reference('content'),
            }),
        };

        const divData: IDivData = {
            log_id: 'id',
            states: [
                {
                    state_id: 1,
                    div: template('card', {
                        opacity: 0.5,
                        orient: 'horizontal',
                        content: [new DivText({ text: 'text' })],
                        margins: {
                            top: 1,
                        },
                    }),
                },
            ],
        };

        expect(divCard(rewriteRefs(templates), divData)).toMatchSnapshot();
    });

    it('should create nested template and card with new property', (): void => {
        const templates = {
            district2: new DivContainer({
                items: [
                    template('district2_foot_text', {
                        text: reference('comments_cnt'),
                        my_end: reference('my_first_end'),
                    }),
                    template('district2_foot_text', {
                        text: reference('likes_cnt'),
                        my_end: 5,
                    }),
                ],
                column_span: 1,
            }),
            district2_foot_text: new DivText({
                text: reference('text'),
                font_size: 13,
                line_height: 16,
                text_color: '#999',
                width: {
                    type: 'wrap_content',
                },
                paddings: {
                    left: 5,
                },
                alignment_horizontal: 'right',
                ranges: [
                    {
                        start: 0,
                        end: reference('my_end'),
                        text_color: '#333',
                    },
                ],
            }),
        };

        const divData: IDivData = {
            log_id: 'id',
            states: [
                {
                    state_id: 1,
                    div: template('district2', {
                        comments_cnt: 'some.comments.cnt',
                        likes_cnt: 'some.likes.cnt',
                        my_first_end: 5,
                    }),
                },
            ],
        };

        expect(divCard(rewriteRefs(templates), divData)).toMatchSnapshot();
    });
});
