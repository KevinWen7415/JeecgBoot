<template>
  <div class="p-2">
    <!--查询区域-->
    <div class="jeecg-basic-table-form-container">
      <a-form ref="formRef" @keyup.enter.native="searchQuery" :model="queryParam" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
    <!--引用表格-->
    <BasicTable   @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" v-auth="'custom:custom_crm_photo_type:add'"  @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
        <a-button  type="primary" v-auth="'custom:custom_crm_photo_type:exportXls'" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
        <j-upload-button  type="primary" v-auth="'custom:custom_crm_photo_type:importExcel'"  preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button v-auth="'custom:custom_crm_photo_type:deleteBatch'">批量操作
            <Icon icon="mdi:chevron-down"></Icon>
          </a-button>
        </a-dropdown>
        <!-- 高级查询 -->
        <super-query :config="superQueryConfig" @search="handleSuperQuery" />
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)"/>
      </template>
      <template v-slot:bodyCell="{ column, record, index, text }">
      </template>
    </BasicTable>
    <!-- 表单区域 -->
    <CustomCrmPhotoTypeModal ref="registerModal" @success="handleSuccess"></CustomCrmPhotoTypeModal>
  </div>
</template>

<script lang="ts" name="custom-customCrmPhotoType" setup>
  import { ref, reactive } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { columns, superQuerySchema } from './CustomCrmPhotoType.data';
  import {
    list,
    deleteOne,
    batchDelete,
    getImportUrl,
    getExportUrl,
    getSubList,
    listTreeAll,
  } from './CustomCrmPhotoType.api';
  import { downloadFile } from '/@/utils/common/renderUtils';
  import CustomCrmPhotoTypeModal from './components/CustomCrmPhotoTypeModal.vue'
  import { useUserStore } from '/@/store/modules/user';
  import { defHttp } from '/@/utils/http/axios';

  const formRef = ref();
  const queryParam = reactive<any>({});
  const toggleSearchStatus = ref<boolean>(false);
  const registerModal = ref();
  const userStore = useUserStore();
  const loading = ref<boolean>(false);
  const dataSource = ref<any[]>([]);

  // 修改后的columns配置（在useListPage中使用）
  const modifiedColumns = columns.map(col => {
    // 隐藏父级ID列
    if (col.dataIndex === 'parentId') {
      return { ...col, ellipsis: true, width: 0, style: { display: 'none' } };
    }

    // 格式化是否叶子节点列
    if (col.dataIndex === 'isLeaf') {
      return {
        ...col,
        customRender: ({ text }) => (text ? '是' : '否')
      };
    }

    // 格式化状态列
    if (col.dataIndex === 'status') {
      return {
        ...col,
        customRender: ({ text }) => (text === '1' ? '启用' : '禁用')
      };
    }

    return col;
  });

  //注册table数据
  const { prefixCls, tableContext, onExportXls, onImportXls } = useListPage({
    tableProps: {
      title: 'custom_crm_photo_type',
      api: listTreeAll,
      columns:modifiedColumns,
      canResize:false,
      // 树表格
      pagination: false,
      isTreeTable: true,
      treeColumnIndex: 1,
      pagination: false,
      striped: true,
      useSearchForm: true,
      showTableSetting: true,
      bordered: true,
      showIndexColumn: false,
      tableSetting: { fullScreen: true },
      useSearchForm: false,
      showIndexColumn: true,
      actionColumn: {
        width: 120,
        fixed: 'right',
      },
      beforeFetch: async (params) => {
        return Object.assign(params, queryParam);
      }
    },
    exportConfig: {
      name: "custom_crm_photo_type",
      url: getExportUrl,
      params: queryParam,
    },
	  importConfig: {
	    url: getImportUrl,
	    success: handleSuccess
	  },
  });
  const [registerTable, { reload, setLoading, expandAll,collapseAll, updateTableDataRecord, findTableDataRecord, getDataSource }, { rowSelection, selectedRowKeys }] = tableContext;
  const labelCol = reactive({
    xs:24,
    sm:4,
    xl:6,
    xxl:4
  });
  const wrapperCol = reactive({
    xs: 24,
    sm: 20,
  });

  // 高级查询配置
  const superQueryConfig = reactive(superQuerySchema);

  /**
   * 高级查询事件
   */
  function handleSuperQuery(params) {
    Object.keys(params).map((k) => {
      queryParam[k] = params[k];
    });
    searchQuery();
  }

  /**
   * 新增事件
   */
  function handleAdd() {
    registerModal.value.disableSubmit = false;
    registerModal.value.add();
  }
  
  /**
   * 编辑事件
   */
  function handleEdit(record: Recordable) {
    registerModal.value.disableSubmit = false;
    registerModal.value.edit(record);
  }
   
  /**
   * 详情
   */
  function handleDetail(record: Recordable) {
    registerModal.value.disableSubmit = true;
    registerModal.value.edit(record);
  }
   
  /**
   * 删除事件
   */
  async function handleDelete(record) {
    await deleteOne({ id: record.id }, handleSuccess);
  }
   
  /**
   * 批量删除事件
   */
  async function batchHandleDelete() {
    await batchDelete({ ids: selectedRowKeys.value }, handleSuccess);
  }
   
  /**
   * 成功回调
   */
  function handleSuccess() {
    (selectedRowKeys.value = []) && reload();
  }
   
  /**
   * 操作栏
   */
  function getTableAction(record) {
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
        auth: 'custom:custom_crm_photo_type:edit'
      },
    ];
  }
   
  /**
   * 下拉操作栏
   */
  function getDropDownAction(record) {
    return [
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      }, {
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft',
        },
        auth: 'custom:custom_crm_photo_type:delete'
      }
    ]
  }

  /**
   * 查询
   */
  function searchQuery() {
    reload();
  }
  
  /**
   * 重置
   */
  function searchReset() {
    formRef.value.resetFields();
    selectedRowKeys.value = [];
    //刷新数据
    reload();
  }

  /*async function loadSubData(params) {
    loading.value = true;
    try {
      // 调用你实际的API接口获取子级数据
      const result = await getSubList(params); // 需要确保这个API存在
      loading.value = false;

      return result.map((item) => {
        return {
          ...item,
          // 确保每个子节点也包含hasChildren字段
          hasChildren: item.hasChildren || false
        };
      });
    } catch (error) {
      loading.value = false;
      throw error;
    }
  }*/

  /**
   * 加载根级数据 - 修改为实际调用API的方法
   */
  /*async function loadRootData() {
    try {
      // 调用你实际的API接口获取根级数据
      const data = await list({ typeLevel: '0' }); // 需要确保这个API存在
      dataSource.value = data.map(item => ({
        ...item,
        // 确保每个节点包含hasChildren字段
        hasChildren: item.hasChildren || false
      }));
    } catch (error) {
      // 错误处理
    }
  }

  loadRootData();*/

  /*async function onExpand(isExpand, rowData) {
    if (isExpand && rowData.hasChildren) {
      // 如果还没有加载过子节点
      if (!rowData.children || rowData.children.length === 0) {
        try {
          // 获取子级数据
          const children = await loadSubData({ pid: rowData.id });
          // 更新数据源 - 这里需要根据你的实际数据结构进行更新
          updateTableDataRecord(rowData, { ...rowData, children });
        } catch (error) {
          // 错误处理
        }
      }
    }
  }
*/



</script>

<style lang="less" scoped>
  .jeecg-basic-table-form-container {
    padding: 0;
    .table-page-search-submitButtons {
      display: block;
      margin-bottom: 24px;
      white-space: nowrap;
    }
    .query-group-cust{
      min-width: 100px !important;
    }
    .query-group-split-cust{
      width: 30px;
      display: inline-block;
      text-align: center
    }
    .ant-form-item:not(.ant-form-item-with-help){
      margin-bottom: 16px;
      height: 32px;
    }
    :deep(.ant-picker),:deep(.ant-input-number){
      width: 100%;
    }
  }
</style>
