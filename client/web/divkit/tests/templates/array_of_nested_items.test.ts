import { applyTemplate } from '../../src/utils/applyTemplate';

describe('array_of_nested_items', () => {
    test('test_array_of_nested_items_with_item_template', () => {
        const json = require('../../../../../test_data/template_test_data/array_of_nested_items/test_array_of_nested_items_with_item_template.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_array_of_nested_items_with_item_template_without_data', () => {
        const json = require('../../../../../test_data/template_test_data/array_of_nested_items/test_array_of_nested_items_with_item_template_without_data.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_array_of_nested_items_with_item_template_without_item_required_property', () => {
        const json = require('../../../../../test_data/template_test_data/array_of_nested_items/test_array_of_nested_items_with_item_template_without_item_required_property.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_array_of_nested_items_with_link_for_items', () => {
        const json = require('../../../../../test_data/template_test_data/array_of_nested_items/test_array_of_nested_items_with_link_for_items.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_array_of_nested_items_with_link_for_items_with_item_template', () => {
        const json = require('../../../../../test_data/template_test_data/array_of_nested_items/test_array_of_nested_items_with_link_for_items_with_item_template.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_of_nested_items_with_link_for_items_without_item_required_property', () => {
        const json = require('../../../../../test_data/template_test_data/array_of_nested_items/test_array_of_nested_items_with_link_for_items_without_item_required_property.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_array_of_nested_items_without_item_required_property', () => {
        const json = require('../../../../../test_data/template_test_data/array_of_nested_items/test_array_of_nested_items_without_item_required_property.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_array_of_nested_items_without_templates', () => {
        const json = require('../../../../../test_data/template_test_data/array_of_nested_items/test_array_of_nested_items_without_templates.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });
});
