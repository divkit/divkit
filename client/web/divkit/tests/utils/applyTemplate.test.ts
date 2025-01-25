import {
    describe,
    expect,
    test,
    vi
} from 'vitest';

import { applyTemplate } from '../../src/utils/applyTemplate';

describe('applyTemplate', () => {
    test('simple', () => {
        const onError = vi.fn();

        expect(applyTemplate({
            type: 'hello'
        }, {}, {
            hello: {
                type: 'text',
                text: 'hello'
            }
        }, onError)).toEqual({
            json: {
                type: 'text',
                text: 'hello'
            },
            templateContext: {}
        });

        expect(onError).not.toHaveBeenCalled();
    });

    test('param', () => {
        const onError = vi.fn();

        expect(applyTemplate({
            type: 'hello',
            text: 'abc'
        }, {}, {
            hello: {
                type: 'text',
                $text: 'text'
            }
        }, onError)).toEqual({
            json: {
                type: 'text',
                text: 'abc'
            },
            templateContext: {
                text: 'abc'
            }
        });

        expect(onError).not.toHaveBeenCalled();
    });

    test('not found', () => {
        const onError = vi.fn();

        expect(applyTemplate({
            type: 'hello',
            text2: 'abc'
        }, {}, {
            hello: {
                type: 'text',
                $text: 'text'
            }
        }, onError)).toEqual({
            json: {
                type: 'text',
                text2: 'abc'
            },
            templateContext: {
                text2: 'abc'
            }
        });

        expect(onError.mock.calls).toMatchSnapshot();
    });

    test('templateContext', () => {
        const onError = vi.fn();

        expect(applyTemplate({
            type: 'hello'
        }, {
            text: 'abc'
        }, {
            hello: {
                type: 'text',
                $text: 'text'
            }
        }, onError)).toEqual({
            json: {
                type: 'text',
                text: 'abc'
            },
            templateContext: {
                text: 'abc'
            }
        });

        expect(onError).not.toHaveBeenCalled();
    });

    test('override', () => {
        const onError = vi.fn();

        expect(applyTemplate({
            type: 'hello',
            text: 'def'
        }, {
            text: 'abc'
        }, {
            hello: {
                type: 'text',
                $text: 'text'
            }
        }, onError)).toEqual({
            json: {
                type: 'text',
                text: 'def'
            },
            templateContext: {
                text: 'def'
            }
        });

        expect(onError).not.toHaveBeenCalled();
    });

    test('override 2', () => {
        const onError = vi.fn();

        expect(applyTemplate({
            type: 'hello',
            text: 'def'
        }, {
            text: 'abc'
        }, {
            hello: {
                type: 'text',
                text: 'text'
            }
        }, onError)).toEqual({
            json: {
                type: 'text',
                text: 'def'
            },
            templateContext: {
                text: 'def'
            }
        });

        expect(onError).not.toHaveBeenCalled();
    });

    test('inner object', () => {
        const onError = vi.fn();

        expect(applyTemplate({
            type: 'hello',
            val: 'abc',
        }, {}, {
            hello: {
                type: 'text',
                text: 'text',
                smth: {
                    $val: 'val'
                }
            }
        }, onError)).toEqual({
            json: {
                type: 'text',
                text: 'text',
                smth: {
                    val: 'abc'
                },
                val: 'abc'
            },
            templateContext: {
                val: 'abc'
            }
        });

        expect(onError).not.toHaveBeenCalled();
    });

    test('inner object 2', () => {
        const onError = vi.fn();

        expect(applyTemplate({
            type: 'hello',
        }, {
            val: 'def'
        }, {
            hello: {
                type: 'text',
                text: 'text',
                smth: {
                    $val: 'val'
                }
            }
        }, onError)).toEqual({
            json: {
                type: 'text',
                text: 'text',
                smth: {
                    val: 'def'
                }
            },
            templateContext: {
                val: 'def'
            }
        });

        expect(onError).not.toHaveBeenCalled();
    });

    test('inner object 3', () => {
        const onError = vi.fn();

        expect(applyTemplate({
            type: 'hello',
            val: 'abc'
        }, {
            val: 'def'
        }, {
            hello: {
                type: 'text',
                text: 'text',
                smth: {
                    $val: 'val'
                }
            }
        }, onError)).toEqual({
            json: {
                type: 'text',
                text: 'text',
                smth: {
                    val: 'abc'
                },
                val: 'abc'
            },
            templateContext: {
                val: 'abc'
            }
        });

        expect(onError).not.toHaveBeenCalled();
    });
});
