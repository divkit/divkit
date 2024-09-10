import { describe, test, expect } from 'vitest';
import { addTemplatesSuffix, removeTemplatesSuffix } from './renameTemplates';

describe('renameTemplates', () => {
    test('addTemplatesSuffix', () => {
        expect(addTemplatesSuffix({
            templates: {
                item: {
                    type: 'text',
                    text: 'Hello'
                }
            },
            card: {
                states: [{
                    state_id: 0,
                    div: {
                        type: 'item'
                    }
                }]
            }
        }, '_test')).toEqual({
            templates: {
                item_test: {
                    type: 'text',
                    text: 'Hello'
                }
            },
            card: {
                states: [{
                    state_id: 0,
                    div: {
                        type: 'item_test'
                    }
                }]
            }
        });
    });

    test('removeTemplatesSuffix', () => {
        expect(removeTemplatesSuffix({
            templates: {
                item_test: {
                    type: 'text',
                    text: 'Hello'
                }
            },
            card: {
                states: [{
                    state_id: 0,
                    div: {
                        type: 'item_test'
                    }
                }]
            }
        }, name => name.replace(/_test$/, ''))).toEqual({
            templates: {
                item: {
                    type: 'text',
                    text: 'Hello'
                }
            },
            card: {
                states: [{
                    state_id: 0,
                    div: {
                        type: 'item'
                    }
                }]
            }
        });
    });
});
