import { describe, test, expect } from 'vitest';
import { getObjectProperty, setObjectProperty } from './objectProperty';

describe('objectProperty', () => {
    test('getObjectProperty', () => {
        expect(getObjectProperty({}, 'unknown')).toEqual(undefined);
        expect(getObjectProperty({}, '')).toEqual(undefined);
        expect(getObjectProperty({ name: 'test' }, '')).toEqual(undefined);

        expect(getObjectProperty({ name: 'test' }, 'name')).toEqual('test');

        expect(getObjectProperty({ name: 'test' }, 'name.title')).toEqual(undefined);
        expect(getObjectProperty({ name: 'test' }, 'texts.title')).toEqual(undefined);

        expect(getObjectProperty({ name: [0, 1, 2] }, 'texts[1]')).toEqual(undefined);
        expect(getObjectProperty({ name: [0, 1, 2] }, 'name[1]')).toEqual(1);

        expect(getObjectProperty({ name: [0, 1, 2] }, 'name[4]')).toEqual(undefined);

        expect(getObjectProperty({ name: [{ title: 'hello' }] }, 'name[0].title')).toEqual('hello');
    });

    describe('setObjectProperty', () => {
        test('empty', () => {
            const obj = {};
            setObjectProperty(obj, 'title', 'hello');
            expect(obj).toEqual({ title: 'hello' });

            const obj2 = {};
            setObjectProperty(obj2, 'texts.title', 'hello');
            expect(obj2).toEqual({ texts: { title: 'hello' } });

            const obj3 = {};
            setObjectProperty(obj3, 'texts[0].title', 'hello');
            expect(obj3).toEqual({ texts: [{ title: 'hello' }] });
        });

        test('filled', () => {
            const obj = {
                title: 'start'
            };
            setObjectProperty(obj, 'title', 'hello');
            expect(obj).toEqual({ title: 'hello' });

            const obj2 = {
                texts: {
                    title: 'start',
                    subtitle: 'subtitle'
                }
            };
            setObjectProperty(obj2, 'texts.title', 'hello');
            expect(obj2).toEqual({ texts: { title: 'hello', subtitle: 'subtitle' } });

            const obj3 = {
                texts: [{
                    title: '0'
                }, {
                    title: '1'
                }, {
                    title: '2'
                }]
            };
            setObjectProperty(obj3, 'texts[0].title', 'hello');
            expect(obj3).toEqual({ texts: [{
                title: 'hello'
            }, {
                title: '1'
            }, {
                title: '2'
            }] });
        });

        test('setEmpty', () => {
            const obj = {
                title: 'start'
            };
            setObjectProperty(obj, 'title', false);
            expect(obj).toEqual({ title: false });
        });

        test('setEmpty2', () => {
            const obj = {
                title: 'start'
            };
            setObjectProperty(obj, 'title', undefined);
            expect(obj).toEqual({ });
        });

        test('empty string value is deleted, not preserved', () => {
            const obj = { title: 'start' };
            setObjectProperty(obj, 'title', '');
            expect(obj).toEqual({});
        });

        test('empty string value in nested path is deleted', () => {
            const obj: Record<string, unknown> = {};
            setObjectProperty(obj, 'a.b', '');
            expect(obj).toEqual({});
        });
    });

    describe('setObjectProperty mergeWith', () => {
        test('merges sibling keys into intermediate node', () => {
            const obj = {};
            setObjectProperty(obj, 'a.b', 1, { a: { c: 2 } });
            expect(obj).toEqual({ a: { b: 1, c: 2 } });
        });

        test('merges into existing intermediate node', () => {
            const obj = { a: { b: 1 } };
            setObjectProperty(obj, 'a.b', 2, { a: { c: 3 } });
            expect(obj).toEqual({ a: { b: 2, c: 3 } });
        });

        test('does not merge when mergeWith path does not match', () => {
            const obj = {};
            setObjectProperty(obj, 'a.b', 1, { x: { c: 2 } });
            expect(obj).toEqual({ a: { b: 1 } });
        });

        test('merges deeply nested sibling keys', () => {
            const obj = {};
            setObjectProperty(obj, 'a.b.c', 1, { a: { b: { d: 2 } } });
            expect(obj).toEqual({ a: { b: { c: 1, d: 2 } } });
        });

        test('does not override set value with merge', () => {
            const obj = {};
            setObjectProperty(obj, 'a.b', 1, { a: { b: 99, c: 2 } });
            expect(obj).toEqual({ a: { b: 1, c: 2 } });
        });

        test('mergeWith object is not mutated (shallow path)', () => {
            const mergeWith = { a: { c: 2 } };
            const obj: Record<string, unknown> = {};
            setObjectProperty(obj, 'a.b', 1, mergeWith);
            expect(mergeWith).toEqual({ a: { c: 2 } });
        });

        test('mergeWith object is not mutated (deep path)', () => {
            const mergeWith = { a: { b: { d: 2 } } };
            const obj: Record<string, unknown> = {};
            setObjectProperty(obj, 'a.b.c', 1, mergeWith);
            expect(obj).toEqual({ a: { b: { c: 1, d: 2 } } });
            expect(mergeWith).toEqual({ a: { b: { d: 2 } } });
        });
    });
});
