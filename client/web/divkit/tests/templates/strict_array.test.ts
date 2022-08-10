import { applyTemplate } from '../../src/utils/applyTemplate';

describe('strict_array', () => {
    test('test_referenced_strict_array_with_non_object_item', () => {
        const json = require('../../../../../test_data/template_test_data/strict_array/test_referenced_strict_array_with_non_object_item.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_referenced_strict_array_with_non_resolvable_item', () => {
        const json = require('../../../../../test_data/template_test_data/strict_array/test_referenced_strict_array_with_non_resolvable_item.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_strict_array_happy_case', () => {
        const json = require('../../../../../test_data/template_test_data/strict_array/test_strict_array_happy_case.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates || {}, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_strict_array_happy_case_referenced', () => {
        const json = require('../../../../../test_data/template_test_data/strict_array/test_strict_array_happy_case_referenced.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError).not.toBeCalled();
    });

    test('test_strict_array_with_non_object_item', () => {
        const json = require('../../../../../test_data/template_test_data/strict_array/test_strict_array_with_non_object_item.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates || {}, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_strict_array_with_non_object_item_ref', () => {
        const json = require('../../../../../test_data/template_test_data/strict_array/test_strict_array_with_non_object_item_ref.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_strict_array_with_non_resolvable_item', () => {
        const json = require('../../../../../test_data/template_test_data/strict_array/test_strict_array_with_non_resolvable_item.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates || {}, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });

    test('test_strict_array_with_non_resolvable_item_ref', () => {
        const json = require('../../../../../test_data/template_test_data/strict_array/test_strict_array_with_non_resolvable_item_ref.json');
        const logError = jest.fn();

        expect(applyTemplate(json.entity, {}, json.templates, logError)).toMatchSnapshot();
        expect(logError.mock.calls).toMatchSnapshot();
    });
});
