import { describe, test, expect } from 'vitest';
import { templatesCheck } from './checkDivjson';

describe('checkDivjson', () => {
    test('incorrect data', () => {
        expect(templatesCheck(undefined)).toEqual([]);
        expect(templatesCheck(null)).toEqual([]);
        expect(templatesCheck(2)).toEqual([]);
        expect(templatesCheck(true)).toEqual([]);
        expect(templatesCheck('a')).toEqual([]);
        expect(templatesCheck({})).toEqual([]);
    });

    test('simple', () => {
        expect(templatesCheck({})).toEqual([]);
        expect(templatesCheck({
            templates: {}
        })).toEqual([]);
        expect(templatesCheck({
            templates: {
                item: {
                    type: 'text',
                    text: 'hello'
                }
            }
        })).toEqual([]);
        expect(templatesCheck({
            templates: {
                item: {
                    type: 'text',
                    text: 'hello'
                }
            }
        })).toEqual([]);
        expect(templatesCheck({
            templates: {
                item: {
                    type: 'text',
                    $text: 'title'
                }
            }
        })).toEqual([]);
    });

    test('errors', () => {
        expect(templatesCheck({
            templates: {
                item: {
                    $text: 'title'
                }
            }
        })).toMatchObject([{
            level: 'error',
            message: 'Cannot process template without "type"'
        }]);

        expect(templatesCheck({
            templates: {
                item: {
                    type: 'text',
                    $text: null
                }
            }
        })).toMatchObject([{
            level: 'error',
            message: 'Template field should be used as string'
        }]);
    });

    test('transitive', () => {
        expect(templatesCheck({
            templates: {
                base: {
                    type: 'text',
                    $text: 'title'
                },
                super: {
                    type: 'base',
                    title: 'Hello'
                }
            }
        })).toMatchObject([{
            level: 'error',
            message: 'Template "super" -> property "title": Transitive dependencies is not supported in DivKit for Android and iOS'
        }]);

        expect(templatesCheck({
            templates: {
                base: {
                    type: 'text',
                    $text: 'title'
                },
                super: {
                    type: 'base',
                    $title: 'Hello'
                }
            }
        })).toMatchObject([{
            level: 'error',
            message: 'Template "super" -> property "title": Transitive dependencies is not supported in DivKit for Android and iOS'
        }]);
    });
});
