import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layout/MainLayout.vue'
import { useUserStore } from '../stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('../views/LoginView.vue'), meta: { title: '登录', public: true } },
    {
      path: '/',
      component: MainLayout,
      children: [
        { path: '', component: () => import('../views/HomeView.vue'), meta: { title: '首页' } },
        { path: '/base/users', component: () => import('../views/UsersView.vue'), meta: { title: '用户管理' } },
        { path: '/base/roles', component: () => import('../views/RolesView.vue'), meta: { title: '角色管理' } },
        { path: '/base/menus', component: () => import('../views/MenusView.vue'), meta: { title: '菜单管理' } },
        { path: '/base/dicts', component: () => import('../views/DictsView.vue'), meta: { title: '字典管理' } },
        { path: '/base/params', component: () => import('../views/ParamsView.vue'), meta: { title: '参数中心' } },
        { path: '/base/data-scope', component: () => import('../views/DataScopeView.vue'), meta: { title: '数据权限' } },
        { path: '/profile', component: () => import('../views/ProfileView.vue'), meta: { title: '个人信息' } },
        { path: '/auth/permissions', component: () => import('../views/PermissionsView.vue'), meta: { title: '我的权限点' } },

        { path: '/masterdata/materials', component: () => import('../views/masterdata/MaterialListView.vue'), meta: { title: '物料列表' } },
        { path: '/masterdata/materials/form', component: () => import('../views/masterdata/MaterialFormView.vue'), meta: { title: '物料新增' } },
        { path: '/masterdata/materials/form/:id', component: () => import('../views/masterdata/MaterialFormView.vue'), meta: { title: '物料编辑' } },
        { path: '/masterdata/materials/detail/:id', component: () => import('../views/masterdata/MaterialDetailView.vue'), meta: { title: '物料详情' } },
        { path: '/masterdata/material-categories', component: () => import('../views/masterdata/MaterialCategoryView.vue'), meta: { title: '物料分类' } },

        { path: '/masterdata/customers', component: () => import('../views/masterdata/CustomerListView.vue'), meta: { title: '客户列表' } },
        { path: '/masterdata/customers/form', component: () => import('../views/masterdata/CustomerFormView.vue'), meta: { title: '客户新增' } },
        { path: '/masterdata/customers/form/:id', component: () => import('../views/masterdata/CustomerFormView.vue'), meta: { title: '客户编辑' } },
        { path: '/masterdata/customers/detail/:id', component: () => import('../views/masterdata/CustomerDetailView.vue'), meta: { title: '客户详情' } },

        { path: '/masterdata/suppliers', component: () => import('../views/masterdata/SupplierListView.vue'), meta: { title: '供应商列表' } },
        { path: '/masterdata/suppliers/form', component: () => import('../views/masterdata/SupplierFormView.vue'), meta: { title: '供应商新增' } },
        { path: '/masterdata/suppliers/form/:id', component: () => import('../views/masterdata/SupplierFormView.vue'), meta: { title: '供应商编辑' } },
        { path: '/masterdata/suppliers/detail/:id', component: () => import('../views/masterdata/SupplierDetailView.vue'), meta: { title: '供应商详情' } },

        { path: '/masterdata/warehouses', component: () => import('../views/masterdata/WarehouseListView.vue'), meta: { title: '仓库列表' } },
        { path: '/masterdata/warehouses/form', component: () => import('../views/masterdata/WarehouseFormView.vue'), meta: { title: '仓库新增' } },
        { path: '/masterdata/warehouses/form/:id', component: () => import('../views/masterdata/WarehouseFormView.vue'), meta: { title: '仓库编辑' } },

        { path: '/masterdata/locations', component: () => import('../views/masterdata/LocationListView.vue'), meta: { title: '库位列表' } },
        { path: '/masterdata/locations/form', component: () => import('../views/masterdata/LocationFormView.vue'), meta: { title: '库位新增' } },
        { path: '/masterdata/locations/form/:id', component: () => import('../views/masterdata/LocationFormView.vue'), meta: { title: '库位编辑' } },

        { path: '/sales/orders', component: () => import('../views/sales/SalesOrderListView.vue'), meta: { title: '销售订单列表' } },
        { path: '/sales/orders/form', component: () => import('../views/sales/SalesOrderFormView.vue'), meta: { title: '销售订单新增' } },
        { path: '/sales/orders/form/:id', component: () => import('../views/sales/SalesOrderFormView.vue'), meta: { title: '销售订单编辑' } },
        { path: '/sales/orders/detail/:id', component: () => import('../views/sales/SalesOrderDetailView.vue'), meta: { title: '销售订单详情' } },

        { path: '/sales/delivery-notices', component: () => import('../views/sales/DeliveryNoticeListView.vue'), meta: { title: '发货通知列表' } },
        { path: '/sales/delivery-notices/form', component: () => import('../views/sales/DeliveryNoticeFormView.vue'), meta: { title: '发货通知新增' } },
        { path: '/sales/delivery-notices/form/:id', component: () => import('../views/sales/DeliveryNoticeFormView.vue'), meta: { title: '发货通知编辑' } },
        { path: '/sales/delivery-notices/detail/:id', component: () => import('../views/sales/DeliveryNoticeDetailView.vue'), meta: { title: '发货通知详情' } },

        { path: '/sales/outbounds', component: () => import('../views/sales/SalesOutboundListView.vue'), meta: { title: '销售出库列表' } },
        { path: '/sales/outbounds/detail/:id', component: () => import('../views/sales/SalesOutboundDetailView.vue'), meta: { title: '销售出库详情' } },

        { path: '/sales/returns', component: () => import('../views/sales/SalesReturnListView.vue'), meta: { title: '销售退货列表' } },
        { path: '/sales/returns/form', component: () => import('../views/sales/SalesReturnFormView.vue'), meta: { title: '销售退货新增' } },
        { path: '/sales/returns/form/:id', component: () => import('../views/sales/SalesReturnFormView.vue'), meta: { title: '销售退货编辑' } },

        { path: '/purchase/reqs', component: () => import('../views/purchase/PurchaseReqListView.vue'), meta: { title: '采购申请列表' } },
        { path: '/purchase/reqs/form', component: () => import('../views/purchase/PurchaseReqFormView.vue'), meta: { title: '采购申请新增' } },
        { path: '/purchase/reqs/form/:id', component: () => import('../views/purchase/PurchaseReqFormView.vue'), meta: { title: '采购申请编辑' } },
        { path: '/purchase/reqs/detail/:id', component: () => import('../views/purchase/PurchaseReqDetailView.vue'), meta: { title: '采购申请详情' } },

        { path: '/purchase/orders', component: () => import('../views/purchase/PurchaseOrderListView.vue'), meta: { title: '采购订单列表' } },
        { path: '/purchase/orders/form', component: () => import('../views/purchase/PurchaseOrderFormView.vue'), meta: { title: '采购订单新增' } },
        { path: '/purchase/orders/form/:id', component: () => import('../views/purchase/PurchaseOrderFormView.vue'), meta: { title: '采购订单编辑' } },
        { path: '/purchase/orders/detail/:id', component: () => import('../views/purchase/PurchaseOrderDetailView.vue'), meta: { title: '采购订单详情' } },

        { path: '/purchase/receipts', component: () => import('../views/purchase/PurchaseReceiptListView.vue'), meta: { title: '收货列表' } },
        { path: '/purchase/receipts/detail/:id', component: () => import('../views/purchase/PurchaseReceiptDetailView.vue'), meta: { title: '收货详情' } },

        { path: '/purchase/inbounds', component: () => import('../views/purchase/PurchaseInboundListView.vue'), meta: { title: '采购入库列表' } },
        { path: '/purchase/inbounds/detail/:id', component: () => import('../views/purchase/PurchaseInboundDetailView.vue'), meta: { title: '采购入库详情' } },

        { path: '/purchase/returns', component: () => import('../views/purchase/PurchaseReturnListView.vue'), meta: { title: '采购退货列表' } },
        { path: '/purchase/returns/form', component: () => import('../views/purchase/PurchaseReturnFormView.vue'), meta: { title: '采购退货新增' } },
        { path: '/purchase/returns/form/:id', component: () => import('../views/purchase/PurchaseReturnFormView.vue'), meta: { title: '采购退货编辑' } },

        { path: '/inventory/stock', component: () => import('../views/inventory/StockQueryView.vue'), meta: { title: '即时库存' } },
        { path: '/inventory/ledger', component: () => import('../views/inventory/LedgerQueryView.vue'), meta: { title: '库存台账' } },
        { path: '/inventory/transfers', component: () => import('../views/inventory/TransferListView.vue'), meta: { title: '调拨单列表' } },
        { path: '/inventory/transfers/detail/:id', component: () => import('../views/inventory/TransferDetailView.vue'), meta: { title: '调拨单详情' } },
        { path: '/inventory/counts', component: () => import('../views/inventory/CountListView.vue'), meta: { title: '盘点单列表' } },
        { path: '/inventory/counts/detail/:id', component: () => import('../views/inventory/CountDetailView.vue'), meta: { title: '盘点单详情' } },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  const isPublic = Boolean(to.meta.public)
  const token = localStorage.getItem('erp_token')
  if (!isPublic && !token) {
    return '/login'
  }

  if (!isPublic && token) {
    const userStore = useUserStore()
    if (!userStore.profile) {
      try {
        await userStore.loadProfile()
      } catch {
        userStore.logout()
        return '/login'
      }
    }
  }

  return true
})

export default router
