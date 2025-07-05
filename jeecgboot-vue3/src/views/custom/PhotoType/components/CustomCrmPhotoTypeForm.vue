<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol" name="CustomCrmPhotoTypeForm">
          <a-row>
            <!-- 照片类型名称 -->
            <a-col :span="24">
              <a-form-item label="照片类型名称" v-bind="validateInfos.photoTypeName" id="CustomCrmPhotoTypeForm-photoTypeName" name="photoTypeName">
                <a-input v-model:value="formData.photoTypeName" placeholder="请输入照片类型名称" allow-clear />
              </a-form-item>
            </a-col>

            <!-- 父级节点（树形下拉框） -->
            <a-col :span="24">
              <a-form-item label="父级节点" v-bind="validateInfos.parentId" id="CustomCrmPhotoTypeForm-parentId" name="parentId">
<!--                <a-tree-select-->
<!--                  v-model:value="formData.parentId"-->
<!--                  :tree-data="parentTreeData"-->
<!--                  :field-names="{ value: 'id', label: 'photoTypeName', children: 'children' }"-->
<!--                  style="width: 100%"-->
<!--                  placeholder="请选择父级节点"-->
<!--                  tree-default-expand-all-->
<!--                />-->
                <a-select
                  v-model:value="formData.parentId"
                  style="width: 100%"
                  placeholder="请选择父级节点"
                  allowClear
                >
                  <a-select-option v-for="item in parentTreeData" :key="item.id" :value="item.id">
                    {{ item.photoTypeName }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>

            <!-- 状态（启用/禁用） -->
            <a-col :span="24">
              <a-form-item label="状态" v-bind="validateInfos.status" id="CustomCrmPhotoTypeForm-status" name="status">
                <a-select v-model:value="formData.status" style="width: 100%">
                  <a-select-option value="1">启用</a-select-option>
                  <a-select-option value="0">禁用</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>

            <!-- 排序（数字输入框） -->
            <a-col :span="24">
              <a-form-item label="排序" v-bind="validateInfos.sortOrder" id="CustomCrmPhotoTypeForm-sortOrder" name="sortOrder">
                <a-input-number v-model:value="formData.sortOrder" placeholder="请输入排序" style="width: 100%" :min="0" :step="1" />
              </a-form-item>
            </a-col>

            <!-- 描述（文本域） -->
            <a-col :span="24">
              <a-form-item label="描述" v-bind="validateInfos.description" id="CustomCrmPhotoTypeForm-description" name="description">
                <a-textarea v-model:value="formData.description" placeholder="请输入描述" :rows="3" allow-clear />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </template>
    </JFormContainer>
  </a-spin>
</template>

<script lang="ts" setup>
  import { ref, reactive, defineExpose, nextTick, defineProps, computed, onMounted } from 'vue';
  import { defHttp } from '/@/utils/http/axios';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getValueType } from '/@/utils';
  import {listRoot, listTreeAll, saveOrUpdate} from '../CustomCrmPhotoType.api';
  import { Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
  import { TreeSelect } from 'ant-design-vue';
  import _ from 'lodash-es';

  const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    formData: { type: Object, default: () => ({}) },
    formBpm: { type: Boolean, default: true },
  });

  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);

  // 表单数据定义（已移除typeLevel和isLeaf）
  const formData = reactive<Record<string, any>>({
    id: '',
    photoTypeName: '',
    parentId: '', // 父级ID（树形结构）
    status: '1', // 状态（1:启用，0:禁用）
    sortOrder: 0, // 排序字段
    description: '', // 描述信息
  });

  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);

  // 表单验证规则（已移除typeLevel相关验证）
  const validatorRules = reactive({
    photoTypeName: [{ required: true, message: '请输入照片类型名称!' }],
    parentId: [{ type: 'string', message: '请选择父级节点' }],
  });

  const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, { immediate: false });

  // 表单禁用状态
  const disabled = computed(() => {
    if (props.formBpm === true) {
      return props.formData.disabled === true;
    }
    return props.formDisabled;
  });

  // 父级树数据（需要从API获取）
  const parentTreeData = ref([{ id: '0', photoTypeName: '顶级节点', children: [] }]);

  /**
   * 新增操作
   */
  function add() {
    edit({});
  }

  /**
   * 编辑操作
   */
  function edit(record) {
    nextTick(() => {
      resetFields();
      const tmpData = {
        id: record.id || '',
        photoTypeName: record.photoTypeName || '',
        parentId: record.parentId || '',
        status: (record.status ?? 1).toString(), // 转换为字符串
        sortOrder: record.sortOrder ?? 0,
        description: record.description || '',
      };
      loadParentTreeData();
      Object.assign(formData, tmpData);
    });
  }

  /**
   * 提交表单
   */
  async function submitForm() {
    try {
      await validate();
    } catch ({ errorFields }) {
      if (errorFields) {
        const firstField = errorFields[0];
        if (firstField) {
          formRef.value.scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
        }
      }
      return Promise.reject(errorFields);
    }

    confirmLoading.value = true;
    const isUpdate = ref<boolean>(false);

    let model = { ...formData };

    // 处理状态字段转换
    model.status = parseInt(model.status);

    // 处理空值情况
    if (!model.parentId) {
      model.parentId = '0'; // 默认顶级节点
    }

    if (model.id) {
      isUpdate.value = true;
    }

    await saveOrUpdate(model, isUpdate.value)
      .then((res) => {
        if (res.success) {
          createMessage.success(res.message);
          emit('ok');
        } else {
          createMessage.warning(res.message);
        }
      })
      .finally(() => {
        confirmLoading.value = false;
      });
  }

  // 需要补充父级树数据的加载方法
  async function loadParentTreeData() {
    try {
      // 调用实际的API获取树形数据
      const data = await listRoot(); // 需要实现这个API方法

      // 格式化树数据（根据实际数据结构调整）
      const formatTreeData = (data) => {
        return data.map(item => ({
          ...item,
          key: item.id,
          title: item.photoTypeName,
          value: item.id,
          children: !_.isEmpty(item.children) ? formatTreeData(item.children) : []
        }));
      };

      parentTreeData.value = formatTreeData(data);
    } catch (error) {
      console.error('加载父级树失败:', error);
    }
  }

  // 在onMounted中调用
  onMounted(() => {
    loadParentTreeData();
  });

  // 暴露方法给父组件
  defineExpose({
    add,
    edit,
    submitForm,
  });
</script>

<style lang="less" scoped>
  .antd-modal-form {
    padding: 14px;
  }
</style>
