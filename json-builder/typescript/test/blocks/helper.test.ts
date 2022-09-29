import { DivContainer, DivText, copyTemplates, reference, template } from '../../src';
import { expression } from '../../src';

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

    it('should copy booleans', () => {
        const templates = {
            template1: new DivText({
                border: {
                    // todo remove after true/false would be in schema
                    has_shadow: true as any,
                },
                text: 'test string',
            }),
        };

        const copy = copyTemplates(templates);
        expect(JSON.stringify(copy)).toEqual(JSON.stringify(templates));
    });

    it('should copy expressions', () => {
        const templates = {
            template1: new DivText({
                text: expression('test string'),
            }),
        };

        const copy = copyTemplates(templates);
        expect(JSON.stringify(copy)).toEqual(JSON.stringify(templates));
    });
});
