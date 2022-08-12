import { DivContainer, DivText, rewriteNames, template } from '../../src';

describe('Rewrite templates', () => {
    it('should shoud preserve home:block', () => {
        const rename = (name: string): string => `${name}/test`;
        let templates = {
            block1: new DivContainer({
                items: [template('block2'), template('block3')],
            }),
            block2: new DivContainer({
                items: [
                    new DivText({
                        text: 'text1',
                    }),
                ],
            }),
            block3: template('block2'),
            block4: template('home:block'),
        };

        templates = rewriteNames(templates, rename).templates;
        expect(templates).toMatchSnapshot();
    });

    it('should validate template dependencies', () => {
        const templates = {
            template1: template('a', {
                items: [template('template2'), template('b')],
            }),
            template2: template('home:block'),
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
