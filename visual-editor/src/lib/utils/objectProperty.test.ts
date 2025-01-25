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
    });
});
