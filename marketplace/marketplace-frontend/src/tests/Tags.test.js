import { render, screen } from '@testing-library/react'
import { MockData } from '../mocks/mock';
import { Tags } from '../components/Tags/Tags';

describe('Header', () => {
  let tags
  beforeAll(() => {
    tags = new MockData().tags
  })
  test('Render tags', async () => {
    render(<Tags tags={tags} />)
    const renderedTags = await screen.findAllByTestId('tag')
    expect(renderedTags.length).toBe(3)
  })
  test('Can\'t remove tag with no delete function', async () => {
    render(<Tags tags={tags} />)
    const renderedTags = await screen.findAllByTestId('tag')
    const firstTag = renderedTags[0]
    const removeButton = firstTag.querySelector('svg')
    expect(removeButton).toBeFalsy()
  })
  test('Can remove tag with delete function', async () => {
    render(<Tags tags={tags} deleteTag={() => console.log('deleted')} />)
    const renderedTags = await screen.findAllByTestId('tag')
    const firstTag = renderedTags[0]
    const removeButton = firstTag.querySelector('svg')
    expect(removeButton).toBeTruthy()
  })
})
