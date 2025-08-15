import {
    describe,
    expect,
    test,
    vi
} from 'vitest';

import { applyTemplate } from '../../src/utils/applyTemplate';

describe('default_value', () => {
    test('test_invalid_value_presented_directly', () => {
        const json = require('../../../../../test_data/template_test_data/default_value/test_invalid_value_presented_directly.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_invalid_value_referenced_by_link', () => {
        const json = require('../../../../../test_data/template_test_data/default_value/test_invalid_value_referenced_by_link.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });
});
