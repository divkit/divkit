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
            templateContext: {
                values: {},
                templateBodyContent: expect.any(Set)
            }
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
                values: {
                    text: 'abc'
                },
                templateBodyContent: expect.any(Set)
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
                values: {
                    text2: 'abc'
                },
                templateBodyContent: expect.any(Set)
            }
        });

        expect(onError.mock.calls).toMatchSnapshot();
    });

    test('templateContext', () => {
        const onError = vi.fn();

        expect(applyTemplate({
            type: 'hello'
        }, {
            values: {
                text: 'abc'
            }
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
                values: {
                    text: 'abc'
                },
                templateBodyContent: expect.any(Set)
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
            values: {
                text: 'abc'
            }
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
                values: {
                    text: 'def'
                },
                templateBodyContent: expect.any(Set)
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
            values: {
                text: 'abc'
            }
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
                values: {
                    text: 'def'
                },
                templateBodyContent: expect.any(Set)
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
                values: {
                    val: 'abc'
                },
                templateBodyContent: expect.any(Set)
            }
        });

        expect(onError).not.toHaveBeenCalled();
    });

    test('inner object 2', () => {
        const onError = vi.fn();

        expect(applyTemplate({
            type: 'hello',
        }, {
            values: {
                val: 'def'
            }
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
                values: {
                    val: 'def'
                },
                templateBodyContent: expect.any(Set)
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
            values: {
                val: 'def'
            }
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
                values: {
                    val: 'abc'
                },
                templateBodyContent: expect.any(Set)
            }
        });

        expect(onError).not.toHaveBeenCalled();
    });
});
