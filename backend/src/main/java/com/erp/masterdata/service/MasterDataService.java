package com.erp.masterdata.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.erp.common.dto.PageResult;
import com.erp.common.exception.BizException;
import com.erp.masterdata.dto.AuditReq;
import com.erp.masterdata.entity.*;
import com.erp.masterdata.mapper.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MasterDataService {

    private static final long ORG_ID = 1L;

    private final MdMaterialMapper materialMapper;
    private final MdMaterialCategoryMapper categoryMapper;
    private final MdCustomerMapper customerMapper;
    private final MdSupplierMapper supplierMapper;
    private final MdWarehouseMapper warehouseMapper;
    private final MdLocationMapper locationMapper;

    public MasterDataService(MdMaterialMapper materialMapper,
                             MdMaterialCategoryMapper categoryMapper,
                             MdCustomerMapper customerMapper,
                             MdSupplierMapper supplierMapper,
                             MdWarehouseMapper warehouseMapper,
                             MdLocationMapper locationMapper) {
        this.materialMapper = materialMapper;
        this.categoryMapper = categoryMapper;
        this.customerMapper = customerMapper;
        this.supplierMapper = supplierMapper;
        this.warehouseMapper = warehouseMapper;
        this.locationMapper = locationMapper;
    }

    // -------- 物料分类 --------
    public PageResult<MdMaterialCategory> pageCategories(int pageNo, int pageSize, String keyword) {
        List<MdMaterialCategory> all = categoryMapper.selectList(new QueryWrapper<MdMaterialCategory>()
                .eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) {
            all = all.stream().filter(x -> (x.getCategoryCode() + " " + x.getCategoryName()).contains(keyword)).toList();
        }
        return paginate(all, pageNo, pageSize);
    }

    public MdMaterialCategory getCategory(Long id) { return mustGetCategory(id); }

    public MdMaterialCategory saveCategory(MdMaterialCategory x) {
        x.setOrgId(ORG_ID); if (x.getStatus() == null) x.setStatus("DISABLED"); if (x.getApprovalStatus() == null) x.setApprovalStatus("DRAFT");
        ensureUniqueCategoryCode(x.getId(), x.getCategoryCode());
        if (x.getId() == null) { x.setDelFlag(0); categoryMapper.insert(x); } else categoryMapper.updateById(x);
        return x;
    }

    public MdMaterialCategory submitCategory(Long id) {
        MdMaterialCategory x = mustGetCategory(id);
        x.setApprovalStatus("PENDING");
        categoryMapper.updateById(x);
        return x;
    }

    public MdMaterialCategory auditCategory(Long id, AuditReq req) {
        MdMaterialCategory x = mustGetCategory(id);
        x.setApprovalStatus(Boolean.TRUE.equals(req.getApproved()) ? "APPROVED" : "REJECTED");
        categoryMapper.updateById(x);
        return x;
    }

    public MdMaterialCategory enableCategory(Long id) {
        MdMaterialCategory x = mustGetCategory(id);
        if (!"APPROVED".equals(x.getApprovalStatus())) throw new BizException(400, "未审核通过，不能启用");
        x.setStatus("ENABLED");
        categoryMapper.updateById(x);
        return x;
    }

    public MdMaterialCategory disableCategory(Long id) {
        MdMaterialCategory x = mustGetCategory(id);
        x.setStatus("DISABLED");
        categoryMapper.updateById(x);
        return x;
    }

    public int importCategories(List<MdMaterialCategory> list) {
        int cnt = 0;
        for (MdMaterialCategory x : list == null ? List.<MdMaterialCategory>of() : list) {
            MdMaterialCategory old = categoryMapper.selectOne(new QueryWrapper<MdMaterialCategory>().eq("org_id", ORG_ID).eq("category_code", x.getCategoryCode()).eq("del_flag", 0).last("limit 1"));
            if (old != null) x.setId(old.getId());
            saveCategory(x); cnt++;
        }
        return cnt;
    }

    // -------- 物料 --------
    public PageResult<MdMaterial> pageMaterials(int pageNo, int pageSize, String keyword) {
        List<MdMaterial> all = materialMapper.selectList(new QueryWrapper<MdMaterial>()
                .eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(x -> (x.getMaterialCode() + " " + x.getMaterialName()).contains(keyword)).toList();
        return paginate(all, pageNo, pageSize);
    }

    public MdMaterial getMaterial(Long id) { return mustGetMaterial(id); }

    public MdMaterial saveMaterial(MdMaterial x) {
        x.setOrgId(ORG_ID); if (x.getStatus() == null) x.setStatus("DISABLED"); if (x.getApprovalStatus() == null) x.setApprovalStatus("DRAFT");
        ensureUniqueMaterialCode(x.getId(), x.getMaterialCode());
        if (x.getId() == null) { x.setDelFlag(0); materialMapper.insert(x); } else materialMapper.updateById(x);
        return x;
    }

    public MdMaterial submitMaterial(Long id) {
        MdMaterial x = mustGetMaterial(id);
        x.setApprovalStatus("PENDING");
        x.setSubmitTime(LocalDateTime.now());
        materialMapper.updateById(x);
        return x;
    }

    public MdMaterial auditMaterial(Long id, AuditReq req) {
        MdMaterial x = mustGetMaterial(id);
        x.setApprovalStatus(Boolean.TRUE.equals(req.getApproved()) ? "APPROVED" : "REJECTED");
        x.setAuditTime(LocalDateTime.now());
        x.setAuditRemark(req.getRemark());
        materialMapper.updateById(x);
        return x;
    }

    public MdMaterial enableMaterial(Long id) {
        MdMaterial x = mustGetMaterial(id);
        if (!"APPROVED".equals(x.getApprovalStatus())) throw new BizException(400, "未审核通过，不能启用");
        x.setStatus("ENABLED");
        materialMapper.updateById(x);
        return x;
    }

    public MdMaterial disableMaterial(Long id) {
        MdMaterial x = mustGetMaterial(id);
        x.setStatus("DISABLED");
        materialMapper.updateById(x);
        return x;
    }

    public int importMaterials(List<MdMaterial> list) {
        int cnt = 0;
        for (MdMaterial x : list == null ? List.<MdMaterial>of() : list) {
            MdMaterial old = materialMapper.selectOne(new QueryWrapper<MdMaterial>().eq("org_id", ORG_ID).eq("material_code", x.getMaterialCode()).eq("del_flag", 0).last("limit 1"));
            if (old != null) x.setId(old.getId());
            saveMaterial(x); cnt++;
        }
        return cnt;
    }

    // -------- 客户 --------
    public PageResult<MdCustomer> pageCustomers(int pageNo, int pageSize, String keyword) {
        List<MdCustomer> all = customerMapper.selectList(new QueryWrapper<MdCustomer>().eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(x -> (x.getCustomerCode() + " " + x.getCustomerName()).contains(keyword)).toList();
        return paginate(all, pageNo, pageSize);
    }

    public MdCustomer getCustomer(Long id) { return mustGetCustomer(id); }

    public MdCustomer saveCustomer(MdCustomer x) {
        x.setOrgId(ORG_ID); if (x.getStatus() == null) x.setStatus("DISABLED"); if (x.getApprovalStatus() == null) x.setApprovalStatus("DRAFT");
        ensureUniqueCustomerCode(x.getId(), x.getCustomerCode());
        if (x.getId() == null) { x.setDelFlag(0); customerMapper.insert(x); } else customerMapper.updateById(x);
        return x;
    }

    public MdCustomer submitCustomer(Long id) {
        MdCustomer x = mustGetCustomer(id); x.setApprovalStatus("PENDING"); x.setSubmitTime(LocalDateTime.now()); customerMapper.updateById(x); return x;
    }

    public MdCustomer auditCustomer(Long id, AuditReq req) {
        MdCustomer x = mustGetCustomer(id);
        x.setApprovalStatus(Boolean.TRUE.equals(req.getApproved()) ? "APPROVED" : "REJECTED");
        x.setAuditTime(LocalDateTime.now()); x.setAuditRemark(req.getRemark());
        customerMapper.updateById(x); return x;
    }

    public MdCustomer enableCustomer(Long id) {
        MdCustomer x = mustGetCustomer(id); if (!"APPROVED".equals(x.getApprovalStatus())) throw new BizException(400, "未审核通过，不能启用");
        x.setStatus("ENABLED"); customerMapper.updateById(x); return x;
    }

    public MdCustomer disableCustomer(Long id) {
        MdCustomer x = mustGetCustomer(id); x.setStatus("DISABLED"); customerMapper.updateById(x); return x;
    }

    public int importCustomers(List<MdCustomer> list) {
        int cnt = 0;
        for (MdCustomer x : list == null ? List.<MdCustomer>of() : list) {
            MdCustomer old = customerMapper.selectOne(new QueryWrapper<MdCustomer>().eq("org_id", ORG_ID).eq("customer_code", x.getCustomerCode()).eq("del_flag", 0).last("limit 1"));
            if (old != null) x.setId(old.getId());
            saveCustomer(x); cnt++;
        }
        return cnt;
    }

    // -------- 供应商 --------
    public PageResult<MdSupplier> pageSuppliers(int pageNo, int pageSize, String keyword) {
        List<MdSupplier> all = supplierMapper.selectList(new QueryWrapper<MdSupplier>().eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(x -> (x.getSupplierCode() + " " + x.getSupplierName()).contains(keyword)).toList();
        return paginate(all, pageNo, pageSize);
    }

    public MdSupplier getSupplier(Long id) { return mustGetSupplier(id); }

    public MdSupplier saveSupplier(MdSupplier x) {
        x.setOrgId(ORG_ID); if (x.getStatus() == null) x.setStatus("DISABLED"); if (x.getApprovalStatus() == null) x.setApprovalStatus("DRAFT");
        ensureUniqueSupplierCode(x.getId(), x.getSupplierCode());
        if (x.getId() == null) { x.setDelFlag(0); supplierMapper.insert(x); } else supplierMapper.updateById(x);
        return x;
    }

    public MdSupplier submitSupplier(Long id) { MdSupplier x = mustGetSupplier(id); x.setApprovalStatus("PENDING"); x.setSubmitTime(LocalDateTime.now()); supplierMapper.updateById(x); return x; }

    public MdSupplier auditSupplier(Long id, AuditReq req) {
        MdSupplier x = mustGetSupplier(id);
        x.setApprovalStatus(Boolean.TRUE.equals(req.getApproved()) ? "APPROVED" : "REJECTED");
        x.setAuditTime(LocalDateTime.now()); x.setAuditRemark(req.getRemark()); supplierMapper.updateById(x); return x;
    }

    public MdSupplier enableSupplier(Long id) {
        MdSupplier x = mustGetSupplier(id); if (!"APPROVED".equals(x.getApprovalStatus())) throw new BizException(400, "未审核通过，不能启用");
        x.setStatus("ENABLED"); supplierMapper.updateById(x); return x;
    }

    public MdSupplier disableSupplier(Long id) { MdSupplier x = mustGetSupplier(id); x.setStatus("DISABLED"); supplierMapper.updateById(x); return x; }

    public int importSuppliers(List<MdSupplier> list) {
        int cnt = 0;
        for (MdSupplier x : list == null ? List.<MdSupplier>of() : list) {
            MdSupplier old = supplierMapper.selectOne(new QueryWrapper<MdSupplier>().eq("org_id", ORG_ID).eq("supplier_code", x.getSupplierCode()).eq("del_flag", 0).last("limit 1"));
            if (old != null) x.setId(old.getId());
            saveSupplier(x); cnt++;
        }
        return cnt;
    }

    // -------- 仓库 --------
    public PageResult<MdWarehouse> pageWarehouses(int pageNo, int pageSize, String keyword) {
        List<MdWarehouse> all = warehouseMapper.selectList(new QueryWrapper<MdWarehouse>().eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id"));
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(x -> (x.getWarehouseCode() + " " + x.getWarehouseName()).contains(keyword)).toList();
        return paginate(all, pageNo, pageSize);
    }

    public MdWarehouse getWarehouse(Long id) { return mustGetWarehouse(id); }

    public MdWarehouse saveWarehouse(MdWarehouse x) {
        x.setOrgId(ORG_ID); if (x.getStatus() == null) x.setStatus("DISABLED"); if (x.getApprovalStatus() == null) x.setApprovalStatus("DRAFT");
        ensureUniqueWarehouseCode(x.getId(), x.getWarehouseCode());
        if (x.getId() == null) { x.setDelFlag(0); warehouseMapper.insert(x); } else warehouseMapper.updateById(x);
        return x;
    }

    public MdWarehouse submitWarehouse(Long id) { MdWarehouse x = mustGetWarehouse(id); x.setApprovalStatus("PENDING"); x.setSubmitTime(LocalDateTime.now()); warehouseMapper.updateById(x); return x; }

    public MdWarehouse auditWarehouse(Long id, AuditReq req) {
        MdWarehouse x = mustGetWarehouse(id);
        x.setApprovalStatus(Boolean.TRUE.equals(req.getApproved()) ? "APPROVED" : "REJECTED");
        x.setAuditTime(LocalDateTime.now()); x.setAuditRemark(req.getRemark()); warehouseMapper.updateById(x); return x;
    }

    public MdWarehouse enableWarehouse(Long id) {
        MdWarehouse x = mustGetWarehouse(id); if (!"APPROVED".equals(x.getApprovalStatus())) throw new BizException(400, "未审核通过，不能启用");
        x.setStatus("ENABLED"); warehouseMapper.updateById(x); return x;
    }

    public MdWarehouse disableWarehouse(Long id) {
        MdWarehouse x = mustGetWarehouse(id); x.setStatus("DISABLED"); warehouseMapper.updateById(x); return x;
    }

    public int importWarehouses(List<MdWarehouse> list) {
        int cnt = 0;
        for (MdWarehouse x : list == null ? List.<MdWarehouse>of() : list) {
            MdWarehouse old = warehouseMapper.selectOne(new QueryWrapper<MdWarehouse>().eq("org_id", ORG_ID).eq("warehouse_code", x.getWarehouseCode()).eq("del_flag", 0).last("limit 1"));
            if (old != null) x.setId(old.getId());
            saveWarehouse(x); cnt++;
        }
        return cnt;
    }

    // -------- 库位 --------
    public PageResult<MdLocation> pageLocations(int pageNo, int pageSize, String keyword, Long warehouseId) {
        QueryWrapper<MdLocation> qw = new QueryWrapper<MdLocation>().eq("org_id", ORG_ID).eq("del_flag", 0).orderByDesc("id");
        if (warehouseId != null) qw.eq("warehouse_id", warehouseId);
        List<MdLocation> all = locationMapper.selectList(qw);
        if (keyword != null && !keyword.isBlank()) all = all.stream().filter(x -> (x.getLocationCode() + " " + x.getLocationName()).contains(keyword)).toList();
        return paginate(all, pageNo, pageSize);
    }

    public MdLocation getLocation(Long id) { return mustGetLocation(id); }

    public MdLocation saveLocation(MdLocation x) {
        x.setOrgId(ORG_ID); if (x.getStatus() == null) x.setStatus("DISABLED"); if (x.getApprovalStatus() == null) x.setApprovalStatus("DRAFT");
        ensureUniqueLocationCode(x.getId(), x.getWarehouseId(), x.getLocationCode());
        if (x.getId() == null) { x.setDelFlag(0); locationMapper.insert(x); } else locationMapper.updateById(x);
        return x;
    }

    public MdLocation submitLocation(Long id) { MdLocation x = mustGetLocation(id); x.setApprovalStatus("PENDING"); x.setSubmitTime(LocalDateTime.now()); locationMapper.updateById(x); return x; }

    public MdLocation auditLocation(Long id, AuditReq req) {
        MdLocation x = mustGetLocation(id);
        x.setApprovalStatus(Boolean.TRUE.equals(req.getApproved()) ? "APPROVED" : "REJECTED");
        x.setAuditTime(LocalDateTime.now()); x.setAuditRemark(req.getRemark()); locationMapper.updateById(x); return x;
    }

    public MdLocation enableLocation(Long id) {
        MdLocation x = mustGetLocation(id); if (!"APPROVED".equals(x.getApprovalStatus())) throw new BizException(400, "未审核通过，不能启用");
        x.setStatus("ENABLED"); locationMapper.updateById(x); return x;
    }

    public MdLocation disableLocation(Long id) { MdLocation x = mustGetLocation(id); x.setStatus("DISABLED"); locationMapper.updateById(x); return x; }

    public int importLocations(List<MdLocation> list) {
        int cnt = 0;
        for (MdLocation x : list == null ? List.<MdLocation>of() : list) {
            ensureUniqueLocationCode(x.getId(), x.getWarehouseId(), x.getLocationCode());
            if (x.getId() == null) { x.setDelFlag(0); x.setOrgId(ORG_ID); locationMapper.insert(x); }
            else saveLocation(x);
            cnt++;
        }
        return cnt;
    }

    // -------- 导出骨架 --------
    public Map<String, Object> export(String module) {
        Map<String, Object> x = new LinkedHashMap<>();
        x.put("module", module);
        x.put("status", "TODO");
        x.put("message", "导出接口骨架已预留，后续接文件流导出");
        return x;
    }

    // -------- 校验与工具 --------
    private void ensureUniqueMaterialCode(Long id, String code) {
        if (code == null || code.isBlank()) throw new BizException(400, "物料编码不能为空");
        MdMaterial old = materialMapper.selectOne(new QueryWrapper<MdMaterial>().eq("org_id", ORG_ID).eq("material_code", code).eq("del_flag", 0).last("limit 1"));
        if (old != null && !Objects.equals(old.getId(), id)) throw new BizException(400, "物料编码重复: " + code);
    }

    private void ensureUniqueCategoryCode(Long id, String code) {
        if (code == null || code.isBlank()) throw new BizException(400, "分类编码不能为空");
        MdMaterialCategory old = categoryMapper.selectOne(new QueryWrapper<MdMaterialCategory>().eq("org_id", ORG_ID).eq("category_code", code).eq("del_flag", 0).last("limit 1"));
        if (old != null && !Objects.equals(old.getId(), id)) throw new BizException(400, "分类编码重复: " + code);
    }

    private void ensureUniqueCustomerCode(Long id, String code) {
        if (code == null || code.isBlank()) throw new BizException(400, "客户编码不能为空");
        MdCustomer old = customerMapper.selectOne(new QueryWrapper<MdCustomer>().eq("org_id", ORG_ID).eq("customer_code", code).eq("del_flag", 0).last("limit 1"));
        if (old != null && !Objects.equals(old.getId(), id)) throw new BizException(400, "客户编码重复: " + code);
    }

    private void ensureUniqueSupplierCode(Long id, String code) {
        if (code == null || code.isBlank()) throw new BizException(400, "供应商编码不能为空");
        MdSupplier old = supplierMapper.selectOne(new QueryWrapper<MdSupplier>().eq("org_id", ORG_ID).eq("supplier_code", code).eq("del_flag", 0).last("limit 1"));
        if (old != null && !Objects.equals(old.getId(), id)) throw new BizException(400, "供应商编码重复: " + code);
    }

    private void ensureUniqueWarehouseCode(Long id, String code) {
        if (code == null || code.isBlank()) throw new BizException(400, "仓库编码不能为空");
        MdWarehouse old = warehouseMapper.selectOne(new QueryWrapper<MdWarehouse>().eq("org_id", ORG_ID).eq("warehouse_code", code).eq("del_flag", 0).last("limit 1"));
        if (old != null && !Objects.equals(old.getId(), id)) throw new BizException(400, "仓库编码重复: " + code);
    }

    private void ensureUniqueLocationCode(Long id, Long warehouseId, String code) {
        if (warehouseId == null) throw new BizException(400, "仓库ID不能为空");
        if (code == null || code.isBlank()) throw new BizException(400, "库位编码不能为空");
        MdLocation old = locationMapper.selectOne(new QueryWrapper<MdLocation>().eq("org_id", ORG_ID).eq("warehouse_id", warehouseId).eq("location_code", code).eq("del_flag", 0).last("limit 1"));
        if (old != null && !Objects.equals(old.getId(), id)) throw new BizException(400, "同仓库下库位编码重复: " + code);
    }

    private MdMaterial mustGetMaterial(Long id) { MdMaterial x = materialMapper.selectById(id); if (x == null || x.getDelFlag() != 0) throw new BizException(404, "物料不存在"); return x; }
    private MdMaterialCategory mustGetCategory(Long id) { MdMaterialCategory x = categoryMapper.selectById(id); if (x == null || x.getDelFlag() != 0) throw new BizException(404, "分类不存在"); return x; }
    private MdCustomer mustGetCustomer(Long id) { MdCustomer x = customerMapper.selectById(id); if (x == null || x.getDelFlag() != 0) throw new BizException(404, "客户不存在"); return x; }
    private MdSupplier mustGetSupplier(Long id) { MdSupplier x = supplierMapper.selectById(id); if (x == null || x.getDelFlag() != 0) throw new BizException(404, "供应商不存在"); return x; }
    private MdWarehouse mustGetWarehouse(Long id) { MdWarehouse x = warehouseMapper.selectById(id); if (x == null || x.getDelFlag() != 0) throw new BizException(404, "仓库不存在"); return x; }
    private MdLocation mustGetLocation(Long id) { MdLocation x = locationMapper.selectById(id); if (x == null || x.getDelFlag() != 0) throw new BizException(404, "库位不存在"); return x; }

    private <T> PageResult<T> paginate(List<T> all, int pageNo, int pageSize) {
        int from = Math.max(0, (pageNo - 1) * pageSize);
        int to = Math.min(all.size(), from + pageSize);
        List<T> rows = from >= all.size() ? List.of() : all.subList(from, to);
        return PageResult.of(all.size(), pageNo, pageSize, rows);
    }
}
