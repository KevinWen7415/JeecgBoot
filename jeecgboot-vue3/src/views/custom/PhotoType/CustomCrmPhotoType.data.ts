import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '照片类型名称',
    align: "center",
    dataIndex: 'photoTypeName'
  },
  {
    title: '类型级别',
    align: "center",
    dataIndex: 'typeLevel'
  },
  {
    title: '父级id',
    align: "center",
    dataIndex: 'parentId'
  },
  {
    title: '是否叶子节点',
    align: "center",
    dataIndex: 'isLeaf'
  },
  {
    title: '排序',
    align: "center",
    dataIndex: 'sortOrder'
  },
  {
    title: '状态',
    align: "center",
    dataIndex: 'status'
  },
  {
    title: '描述',
    align: "center",
    dataIndex: 'description'
  },
  {
    title: '租户ID',
    align: "center",
    dataIndex: 'tenantId'
  },
];

// 高级查询数据
export const superQuerySchema = {
  photoTypeName: {title: '照片类型名称',order: 0,view: 'text', type: 'string',},
  typeLevel: {title: '类型级别',order: 1,view: 'number', type: 'number',},
  parentId: {title: '父级id',order: 2,view: 'text', type: 'string',},
  isLeaf: {title: '是否叶子节点',order: 3,view: 'number', type: 'number',},
  sortOrder: {title: '排序',order: 4,view: 'number', type: 'number',},
  status: {title: '状态',order: 5,view: 'text', type: 'string',},
  description: {title: '描述',order: 6,view: 'text', type: 'string',},
  tenantId: {title: '租户ID',order: 7,view: 'number', type: 'number',},
};
