import {
    DivContainer,
    DivGallery,
    DivImage,
    DivImageBackground,
    DivLinearGradient,
    DivSolidBackground,
    DivText,
    fixed,
    reference,
    template,
    templateHelper,
    expression,
} from '../../src';

describe('templateHelper', () => {
    it('should create TemplateBlock with parameters', () => {
        const templates = {
            template1: new DivContainer({
                paddings: reference('vPaddings'),
                items: [
                    new DivText({
                        text: 'text',
                        max_lines: reference('vMaxlines'),
                    }),
                    new DivText({
                        text: reference('vText1'),
                    }),
                    new DivText({
                        text: reference('vText2'),
                    }),
                    new DivImage({
                        image_url: expression('@{image}'),
                        margins: {
                            left: expression('@{left}'),
                        },
                    }),
                ],
            }),
            template2: new DivGallery({
                items: [
                    new DivText({
                        text: 'string',
                        action: reference('action1'),
                    }),
                    new DivContainer({
                        items: [
                            new DivText({
                                text: reference('text2'),
                                action: { log_id: 'test', url: 'test' },
                            }),
                        ],
                    }),
                ],
            }),
            template3: new DivContainer({
                items: [
                    new DivImage({
                        height: fixed(50),
                        width: fixed(50),
                        image_url: 'imageSrc',
                    }),
                ],
                background: [
                    new DivSolidBackground({ color: '#0000000d' }),
                    new DivImageBackground({
                        image_url: reference('img'),
                        scale: reference('imgScale'),
                    }),
                    new DivLinearGradient({
                        angle: reference('bgAngle'),
                        colors: ['#99000000', '#66000000', '#33000000', '#00000000'],
                    }),
                ],
            }),
            template4: template('template3', {
                items: [
                    template('template1', {
                        vPaddings: {},
                    }),
                    template('template2'),
                ],
            }),
            template6: new DivContainer({
                items: [template('template5')],
            }),
            template5: new DivImage({
                image_url: reference('imgUrl'),
            }),
        };
        const helper = templateHelper(templates);
        const block = helper.template1({
            vText1: 'test',
            vText2: 'test',
            vMaxlines: 2,
            vPaddings: { left: 4 },
        });
        expect(block).toMatchSnapshot();

        const block2 = helper.template2({
            action1: { log_id: 'action', url: 'url' },
            text2: 'text2',
        });
        expect(block2).toMatchSnapshot();

        const block3 = helper.template3({
            img: 'img',
            imgScale: 'fill',
            bgAngle: 90,
        });
        expect(block3).toMatchSnapshot();

        const block4 = helper.template4({
            img: 'img',
            imgScale: 'fill',
            vText1: '1',
            vText2: '2',
            text2: '2',
            action1: { log_id: 'action', url: 'url' },
            vMaxlines: 1,
            bgAngle: 90,
        });
        expect(block4).toMatchSnapshot();

        helper.template6({
            imgUrl: 'imgUrl',
        });

        // expression into the reference
        helper.template2({
            action1: {
                log_id: '123',
                log_url: expression('//ya.ru'),
            },
            text2: expression('1'),
        });
    });
});
