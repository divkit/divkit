import { useEffect } from 'react';
import { useGetTemplateByIdQuery } from '../../services/api';
import { ITemplate } from '../../types';

interface IProps {
  id: string;
  handleGetTemplate: (item: ITemplate) => void;
}

export const GetTemplateByIdQuery = ({ id, handleGetTemplate }: IProps) => {
  const { data: item } = useGetTemplateByIdQuery(id);

  useEffect(() => {
    if (item) {
      handleGetTemplate(item);
    }
  }, [item, id]);

  return <></>;
};
