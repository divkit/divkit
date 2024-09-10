import { describe, test, expect, vi } from 'vitest';
import { convertDictToPalette, convertPaletteToDict } from './convertPalette';

describe('convertPalette', () => {
    test('convertDictToPalette', () => {
        vi.setSystemTime(123);

        expect(convertDictToPalette({
            card: {
                variables: [{
                    name: 'local_palette',
                    type: 'dict',
                    value: {
                        text: {
                            name: 'Text color',
                            light: '#000',
                            dark: '#fff'
                        }
                    }
                }, {
                    name: 'counter',
                    type: 'integer',
                    value: 0
                }],
                states: [{
                    state_id: 0,
                    div: {
                        type: 'text',
                        text: 'Hello',
                        text_color: '@{getDictOptColor(\'#00ffffff\', local_palette, \'text\', theme)}'
                    }
                }]
            }
        })).toEqual({
            card: {
                variables: [{
                    name: 'counter',
                    type: 'integer',
                    value: 0
                }],
                states: [{
                    state_id: 0,
                    div: {
                        type: 'text',
                        text: 'Hello',
                        text_color: '@{local_palette.text.123.u0084u0101u0120u0116u0032u0099u0111u0108u0111u0114}'
                    }
                }]
            },
            palette: {
                light: [{
                    name: 'local_palette.text.123.u0084u0101u0120u0116u0032u0099u0111u0108u0111u0114',
                    color: '#000'
                }],
                dark: [{
                    name: 'local_palette.text.123.u0084u0101u0120u0116u0032u0099u0111u0108u0111u0114',
                    color: '#fff'
                }]
            }
        });
    });

    test('convertPaletteToDict', () => {
        expect(convertPaletteToDict({
            card: {
                variables: [{
                    name: 'counter',
                    type: 'integer',
                    value: 0
                }],
                states: [{
                    state_id: 0,
                    div: {
                        type: 'text',
                        text: 'Hello',
                        text_color: '@{local_palette.text.123.u0084u0101u0120u0116u0032u0099u0111u0108u0111u0114}'
                    }
                }]
            },
            palette: {
                light: [{
                    name: 'local_palette.text.123.u0084u0101u0120u0116u0032u0099u0111u0108u0111u0114',
                    color: '#000'
                }],
                dark: [{
                    name: 'local_palette.text.123.u0084u0101u0120u0116u0032u0099u0111u0108u0111u0114',
                    color: '#fff'
                }]
            }
        })).toEqual({
            card: {
                variables: [{
                    name: 'counter',
                    type: 'integer',
                    value: 0
                }, {
                    name: 'local_palette',
                    type: 'dict',
                    value: {
                        text: {
                            name: 'Text color',
                            light: '#000',
                            dark: '#fff'
                        }
                    }
                }],
                states: [{
                    state_id: 0,
                    div: {
                        type: 'text',
                        text: 'Hello',
                        text_color: '@{getDictOptColor(\'#00ffffff\', local_palette, \'text\', theme)}'
                    }
                }]
            }
        });
    });
});
