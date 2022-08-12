import { DivContainer, DivText, copyTemplates, reference, template } from '../../src';

describe('helper functions', () => {
    it('should copy templates', () => {
        const templates = {
            template1: new DivContainer({
                items: [
                    new DivText({
                        paddings: {
                            right: 4,
                            left: reference('leftPad'),
                        },
                        text: 'test string',
                    }),
                ],
            }),
            template2: template('template1', {
                margins: { left: 7 },
            }),
        };

        const copy = copyTemplates(templates);
        expect(JSON.stringify(copy)).toEqual(JSON.stringify(templates));
    });
});
