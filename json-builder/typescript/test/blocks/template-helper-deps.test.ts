import { DivContainer, DivText, template, templatesDepsMap } from '../../src';

describe('template deps', () => {
    it('should find templates deps', () => {
        const templates = {
            template1: template('template2', {
                items: [template('template2'), template('template5')],
            }),
            template2: new DivContainer({
                items: [template('template3')],
            }),
            template3: new DivContainer({
                items: [
                    template('template5'),
                    new DivContainer({
                        items: [template('template4')],
                    }),
                ],
            }),
            template4: new DivText({
                text: 'test',
            }),
            template5: new DivText({
                text: 'test',
            }),
        };

        const depsMap = templatesDepsMap(templates);

        expect(new Set(depsMap.template1)).toEqual(
            new Set(['template1', 'template2', 'template3', 'template4', 'template5']),
        );
        expect(new Set(depsMap.template2)).toEqual(new Set(['template2', 'template3', 'template4', 'template5']));
        expect(new Set(depsMap.template3)).toEqual(new Set(['template3', 'template4', 'template5']));
        expect(new Set(depsMap.template4)).toEqual(new Set(['template4']));
    });

    it('should find common templates dependencies', () => {
        const templatesCommon = {
            commonTemplate1: new DivContainer({
                items: [template('commonTemplate2')],
            }),
            commonTemplate2: template('commonTemplate3'),
            commonTemplate3: new DivText({ text: 'text' }),
            commonTemplate4: new DivText({ text: 'text' }),
        };

        const templates = {
            template: template('commonTemplate1'),
        };

        const commonDeps = templatesDepsMap(templatesCommon);
        const depsMap = templatesDepsMap(templates, commonDeps);
        expect(new Set(depsMap.template)).toEqual(
            new Set(['template', 'commonTemplate1', 'commonTemplate2', 'commonTemplate3']),
        );
    });
});
