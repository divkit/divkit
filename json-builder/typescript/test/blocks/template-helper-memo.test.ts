import { thelperWithMemo } from '../../src';

describe('TemplateHelperMemo', () => {
    it('should track used templates', () => {
        const { used, thelper } = thelperWithMemo<any>();
        thelper.template1({});
        thelper.template2({});
        thelper.template3({});
        expect([...used] as string[]).toEqual(['template1', 'template2', 'template3']);
    });

    it('should use custom templates naming scheme', () => {
        const { used, thelper } = thelperWithMemo<any>({
            customName: (name) => `${name}/test`,
        });
        const blocks = [thelper.template1({}), thelper.template2({}), thelper.template3({})];
        expect(blocks.map((b) => b.type)).toEqual(['template1/test', 'template2/test', 'template3/test']);
        expect([...used] as string[]).toEqual(['template1', 'template2', 'template3']);
    });
});
