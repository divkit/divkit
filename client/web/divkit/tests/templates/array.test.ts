import { applyTemplate } from '../../src/utils/applyTemplate';

describe('array', () => {
    test('test_array_empty', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_empty.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_invalid_items', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_invalid_items.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_invalid_items_in_data', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_invalid_items_in_data.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_missing_data', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_missing_data.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_array_missing_one_link', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_missing_one_link.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_array_nested', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_nested.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_nested_with_internal_link', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_nested_with_internal_link.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_not_templated', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_not_templated.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_array_one_invalid_item', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_one_invalid_item.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_with_complex_items_with_internal_links', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_with_complex_items_with_internal_links.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_with_heterogeneous_items', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_with_heterogeneous_items.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_with_heterogeneous_items_with_internal_links', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_with_heterogeneous_items_with_internal_links.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_with_internal_links', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_with_internal_links.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_with_link', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_with_link.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_with_nested_templated_array', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_with_nested_templated_array.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_with_override_in_items', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_with_override_in_items.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_with_override_in_template', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_with_override_in_template.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_with_templated_items', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_with_templated_items.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_array_without_link', () => {
        const json = require('../../../../../test_data/template_test_data/array/test_array_without_link.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });
});
