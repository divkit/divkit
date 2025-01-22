import {
    describe,
    expect,
    test,
    vi
} from 'vitest';

import { applyTemplate } from '../../src/utils/applyTemplate';

describe('array_with_transform', () => {
    test('test_array_with_transform_invalid_items', () => {
        const json = require('../../../../../test_data/template_test_data/array_with_transform/test_array_with_transform_invalid_items.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_with_transform_not_templated_invalid_items', () => {
        const json = require('../../../../../test_data/template_test_data/array_with_transform/test_array_with_transform_not_templated_invalid_items.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_array_with_transform_not_templated_one_invalid_item', () => {
        const json = require('../../../../../test_data/template_test_data/array_with_transform/test_array_with_transform_not_templated_one_invalid_item.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_array_with_transform_one_invalid_item', () => {
        const json = require('../../../../../test_data/template_test_data/array_with_transform/test_array_with_transform_one_invalid_item.json');
        const logError = vi.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });
});
