import { reactive, ref } from 'vue';
import SearchForm from '../components/common/SearchForm.vue';
import DataTable from '../components/common/DataTable.vue';
const query = reactive({ username: '' });
const loading = ref(false);
const total = ref(1);
const rows = ref([{ username: 'admin', status: 'ENABLED' }]);
const columns = [{ prop: 'username', label: '用户名' }, { prop: 'status', label: '状态' }];
const load = () => { };
const reset = () => { query.username = ''; };
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {};
let __VLS_components;
let __VLS_directives;
__VLS_asFunctionalElement(__VLS_intrinsicElements.div, __VLS_intrinsicElements.div)({});
/** @type {[typeof SearchForm, typeof SearchForm, ]} */ ;
// @ts-ignore
const __VLS_0 = __VLS_asFunctionalComponent(SearchForm, new SearchForm({
    ...{ 'onSearch': {} },
    ...{ 'onReset': {} },
    model: (__VLS_ctx.query),
}));
const __VLS_1 = __VLS_0({
    ...{ 'onSearch': {} },
    ...{ 'onReset': {} },
    model: (__VLS_ctx.query),
}, ...__VLS_functionalComponentArgsRest(__VLS_0));
let __VLS_3;
let __VLS_4;
let __VLS_5;
const __VLS_6 = {
    onSearch: (__VLS_ctx.load)
};
const __VLS_7 = {
    onReset: (__VLS_ctx.reset)
};
__VLS_2.slots.default;
const __VLS_8 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
const __VLS_9 = __VLS_asFunctionalComponent(__VLS_8, new __VLS_8({
    label: "用户名",
}));
const __VLS_10 = __VLS_9({
    label: "用户名",
}, ...__VLS_functionalComponentArgsRest(__VLS_9));
__VLS_11.slots.default;
const __VLS_12 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
const __VLS_13 = __VLS_asFunctionalComponent(__VLS_12, new __VLS_12({
    modelValue: (__VLS_ctx.query.username),
    placeholder: "请输入用户名",
    clearable: true,
}));
const __VLS_14 = __VLS_13({
    modelValue: (__VLS_ctx.query.username),
    placeholder: "请输入用户名",
    clearable: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_13));
var __VLS_11;
var __VLS_2;
/** @type {[typeof DataTable, ]} */ ;
// @ts-ignore
const __VLS_16 = __VLS_asFunctionalComponent(DataTable, new DataTable({
    ...{ 'onPageChange': {} },
    columns: (__VLS_ctx.columns),
    data: (__VLS_ctx.rows),
    loading: (__VLS_ctx.loading),
    total: (__VLS_ctx.total),
    pageNo: (1),
    pageSize: (10),
}));
const __VLS_17 = __VLS_16({
    ...{ 'onPageChange': {} },
    columns: (__VLS_ctx.columns),
    data: (__VLS_ctx.rows),
    loading: (__VLS_ctx.loading),
    total: (__VLS_ctx.total),
    pageNo: (1),
    pageSize: (10),
}, ...__VLS_functionalComponentArgsRest(__VLS_16));
let __VLS_19;
let __VLS_20;
let __VLS_21;
const __VLS_22 = {
    onPageChange: (__VLS_ctx.load)
};
var __VLS_18;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            SearchForm: SearchForm,
            DataTable: DataTable,
            query: query,
            loading: loading,
            total: total,
            rows: rows,
            columns: columns,
            load: load,
            reset: reset,
        };
    },
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
});
; /* PartiallyEnd: #4569/main.vue */
