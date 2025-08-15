import {
    DivContainer,
    DivText,
    getTemplateHash,
    rewriteTemplateVersions,
    template,
    thelperVersion,
    thelperWithMemo,
} from '../../src';

describe('template helper version', () => {
    it('should add versions', () => {
        const templatesV1 = {
            template1: new DivText({
                text: 'text1',
            }),
        };

        const templatesV2 = {
            template1: new DivText({
                text: 'text2',
            }),
        };

        const version1 = thelperVersion(templatesV1);
        const version2 = thelperVersion(templatesV2);

        const { used: usedV1, thelper: thelperV1 } = thelperWithMemo<typeof templatesV1>({
            customName: version1,
        });

        const { used: usedV2, thelper: thelperV2 } = thelperWithMemo<typeof templatesV1>({
            customName: version2,
        });

        const block1 = thelperV1.template1({});
        const block2 = thelperV2.template1({});

        expect(block1.type).toMatch('template1/');
        expect(block1.type).toMatch('template1/');
        expect([...usedV1]).toEqual([...usedV2]);
        expect(block1.type).not.toEqual(block2.type);
    });

    it('should rewrite templates', () => {
        let templates = {
            template1: new DivText({
                text: 'text1',
            }),
            template2: new DivContainer({
                items: [template('template3')],
            }),
            template3: new DivContainer({
                items: [template('template4'), template('template1')],
            }),
            template4: new DivText({
                text: 'template4',
            }),
        };

        templates = rewriteTemplateVersions(templates).templates;

        expect(templates.template2.items?.[0].type).toEqual(`template3/${getTemplateHash(templates.template3)}`);
        expect(templates.template3.items?.[0].type).toEqual(`template4/${getTemplateHash(templates.template4)}`);
    });

    it('should work with common templates', () => {
        const templatesCommon = {
            commonTemplate1: new DivContainer({
                items: [template('commonTemplate2')],
            }),
            commonTemplate2: template('commonTemplate3'),
            commonTemplate3: new DivText({ text: 'text' }),
            commonTemplate4: new DivText({ text: 'text' }),
        };

        const { resolvedNames: commonNames, templates: templatesCommonPatched } =
            rewriteTemplateVersions(templatesCommon);

        let templates = {
            template: template('commonTemplate1'),
        };
        templates = rewriteTemplateVersions(templates, commonNames).templates;

        expect(templates.template.type).toEqual(
            `commonTemplate1/${getTemplateHash(templatesCommonPatched.commonTemplate1)}`,
        );
    });
});
