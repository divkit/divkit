import { DivContainer, DivText, rewriteNames, template } from '../../src';

describe('Rewrite templates', () => {
    it('should validate template dependencies', () => {
        const templates = {
            template1: template('a', {
                items: [template('template2'), template('b')],
            }),
            template2: new DivText({
                text: 'template2',
            }),
        };
        expect(() => rewriteNames(templates, (x) => x)).toThrowError(
            `template 'template1' unsolvable dependencies: 'a','b'`,
        );
    });

    it('should catch cyclic dependecies', () => {
        const templates = {
            template1: new DivContainer({
                items: [template('template2')],
            }),
            template2: new DivContainer({
                items: [template('template3')],
            }),
            template3: new DivContainer({
                items: [template('template1')],
            }),
        };
        expect(() => rewriteNames(templates, (x) => x)).toThrowError('cyclic depdendencies');
    });
});
