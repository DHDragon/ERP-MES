import http from '../request'
import type {
  AuditReq,
  ExportResp,
  ImportResp,
  MdCustomer,
  MdLocation,
  MdMaterial,
  MdMaterialCategory,
  MdSupplier,
  MdWarehouse,
  MasterPage,
} from '../../types/masterdata'

const get = <T>(url: string, params?: Record<string, any>) => http.get<T>(url, { params })
const post = <T>(url: string, data?: any) => http.post<T>(url, data)

export const materialApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<MasterPage<MdMaterial>>('/masterdata/materials', params),
  detail: (id: number) => get<MdMaterial>(`/masterdata/materials/${id}`),
  save: (data: MdMaterial) => post<MdMaterial>('/masterdata/materials', data),
  submit: (id: number) => post<MdMaterial>(`/masterdata/materials/${id}/submit`),
  audit: (id: number, data: AuditReq) => post<MdMaterial>(`/masterdata/materials/${id}/audit`, data),
  enable: (id: number) => post<MdMaterial>(`/masterdata/materials/${id}/enable`),
  disable: (id: number) => post<MdMaterial>(`/masterdata/materials/${id}/disable`),
  import: (data: MdMaterial[]) => post<ImportResp>('/masterdata/materials/import', data),
  export: () => get<ExportResp>('/masterdata/materials/export'),
}

export const materialCategoryApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<MasterPage<MdMaterialCategory>>('/masterdata/material-categories', params),
  detail: (id: number) => get<MdMaterialCategory>(`/masterdata/material-categories/${id}`),
  save: (data: MdMaterialCategory) => post<MdMaterialCategory>('/masterdata/material-categories', data),
  submit: (id: number) => post<MdMaterialCategory>(`/masterdata/material-categories/${id}/submit`),
  audit: (id: number, data: AuditReq) => post<MdMaterialCategory>(`/masterdata/material-categories/${id}/audit`, data),
  enable: (id: number) => post<MdMaterialCategory>(`/masterdata/material-categories/${id}/enable`),
  disable: (id: number) => post<MdMaterialCategory>(`/masterdata/material-categories/${id}/disable`),
  import: (data: MdMaterialCategory[]) => post<ImportResp>('/masterdata/material-categories/import', data),
  export: () => get<ExportResp>('/masterdata/material-categories/export'),
}

export const customerApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<MasterPage<MdCustomer>>('/masterdata/customers', params),
  detail: (id: number) => get<MdCustomer>(`/masterdata/customers/${id}`),
  save: (data: MdCustomer) => post<MdCustomer>('/masterdata/customers', data),
  submit: (id: number) => post<MdCustomer>(`/masterdata/customers/${id}/submit`),
  audit: (id: number, data: AuditReq) => post<MdCustomer>(`/masterdata/customers/${id}/audit`, data),
  enable: (id: number) => post<MdCustomer>(`/masterdata/customers/${id}/enable`),
  disable: (id: number) => post<MdCustomer>(`/masterdata/customers/${id}/disable`),
  import: (data: MdCustomer[]) => post<ImportResp>('/masterdata/customers/import', data),
  export: () => get<ExportResp>('/masterdata/customers/export'),
}

export const supplierApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<MasterPage<MdSupplier>>('/masterdata/suppliers', params),
  detail: (id: number) => get<MdSupplier>(`/masterdata/suppliers/${id}`),
  save: (data: MdSupplier) => post<MdSupplier>('/masterdata/suppliers', data),
  submit: (id: number) => post<MdSupplier>(`/masterdata/suppliers/${id}/submit`),
  audit: (id: number, data: AuditReq) => post<MdSupplier>(`/masterdata/suppliers/${id}/audit`, data),
  enable: (id: number) => post<MdSupplier>(`/masterdata/suppliers/${id}/enable`),
  disable: (id: number) => post<MdSupplier>(`/masterdata/suppliers/${id}/disable`),
  import: (data: MdSupplier[]) => post<ImportResp>('/masterdata/suppliers/import', data),
  export: () => get<ExportResp>('/masterdata/suppliers/export'),
}

export const warehouseApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string }) => get<MasterPage<MdWarehouse>>('/masterdata/warehouses', params),
  detail: (id: number) => get<MdWarehouse>(`/masterdata/warehouses/${id}`),
  save: (data: MdWarehouse) => post<MdWarehouse>('/masterdata/warehouses', data),
  submit: (id: number) => post<MdWarehouse>(`/masterdata/warehouses/${id}/submit`),
  audit: (id: number, data: AuditReq) => post<MdWarehouse>(`/masterdata/warehouses/${id}/audit`, data),
  enable: (id: number) => post<MdWarehouse>(`/masterdata/warehouses/${id}/enable`),
  disable: (id: number) => post<MdWarehouse>(`/masterdata/warehouses/${id}/disable`),
  import: (data: MdWarehouse[]) => post<ImportResp>('/masterdata/warehouses/import', data),
  export: () => get<ExportResp>('/masterdata/warehouses/export'),
}

export const locationApi = {
  list: (params: { pageNo: number; pageSize: number; keyword?: string; warehouseId?: number }) => get<MasterPage<MdLocation>>('/masterdata/locations', params),
  detail: (id: number) => get<MdLocation>(`/masterdata/locations/${id}`),
  save: (data: MdLocation) => post<MdLocation>('/masterdata/locations', data),
  submit: (id: number) => post<MdLocation>(`/masterdata/locations/${id}/submit`),
  audit: (id: number, data: AuditReq) => post<MdLocation>(`/masterdata/locations/${id}/audit`, data),
  enable: (id: number) => post<MdLocation>(`/masterdata/locations/${id}/enable`),
  disable: (id: number) => post<MdLocation>(`/masterdata/locations/${id}/disable`),
  import: (data: MdLocation[]) => post<ImportResp>('/masterdata/locations/import', data),
  export: () => get<ExportResp>('/masterdata/locations/export'),
}
