package com.erp.masterdata.web;

import com.erp.common.dto.PageResult;
import com.erp.common.dto.Result;
import com.erp.masterdata.dto.AuditReq;
import com.erp.masterdata.entity.*;
import com.erp.masterdata.service.MasterDataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masterdata")
public class MasterDataController {

    private final MasterDataService service;

    public MasterDataController(MasterDataService service) {
        this.service = service;
    }

    // ----- 物料分类 -----
    @GetMapping("/material-categories")
    public Result<PageResult<MdMaterialCategory>> categoryList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                               @RequestParam(defaultValue = "20") Integer pageSize,
                                                               @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageCategories(pageNo, pageSize, keyword));
    }

    @GetMapping("/material-categories/{id}")
    public Result<MdMaterialCategory> categoryDetail(@PathVariable Long id) { return Result.ok(service.getCategory(id)); }

    @PostMapping("/material-categories")
    public Result<MdMaterialCategory> categorySave(@RequestBody MdMaterialCategory req) { return Result.ok(service.saveCategory(req)); }

    @PostMapping("/material-categories/{id}/submit")
    public Result<MdMaterialCategory> categorySubmit(@PathVariable Long id) { return Result.ok(service.submitCategory(id)); }

    @PostMapping("/material-categories/{id}/audit")
    public Result<MdMaterialCategory> categoryAudit(@PathVariable Long id, @RequestBody AuditReq req) { return Result.ok(service.auditCategory(id, req)); }

    @PostMapping("/material-categories/{id}/enable")
    public Result<MdMaterialCategory> categoryEnable(@PathVariable Long id) { return Result.ok(service.enableCategory(id)); }

    @PostMapping("/material-categories/{id}/disable")
    public Result<MdMaterialCategory> categoryDisable(@PathVariable Long id) { return Result.ok(service.disableCategory(id)); }

    @PostMapping("/material-categories/import")
    public Result<Map<String, Object>> categoryImport(@RequestBody List<MdMaterialCategory> req) {
        return Result.ok(Map.of("count", service.importCategories(req)));
    }

    @GetMapping("/material-categories/export")
    public Result<Map<String, Object>> categoryExport() { return Result.ok(service.export("material-category")); }

    // ----- 物料 -----
    @GetMapping("/materials")
    public Result<PageResult<MdMaterial>> materialList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                       @RequestParam(defaultValue = "20") Integer pageSize,
                                                       @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageMaterials(pageNo, pageSize, keyword));
    }

    @GetMapping("/materials/{id}")
    public Result<MdMaterial> materialDetail(@PathVariable Long id) { return Result.ok(service.getMaterial(id)); }

    @PostMapping("/materials")
    public Result<MdMaterial> materialSave(@RequestBody MdMaterial req) { return Result.ok(service.saveMaterial(req)); }

    @PostMapping("/materials/{id}/submit")
    public Result<MdMaterial> materialSubmit(@PathVariable Long id) { return Result.ok(service.submitMaterial(id)); }

    @PostMapping("/materials/{id}/audit")
    public Result<MdMaterial> materialAudit(@PathVariable Long id, @RequestBody AuditReq req) { return Result.ok(service.auditMaterial(id, req)); }

    @PostMapping("/materials/{id}/enable")
    public Result<MdMaterial> materialEnable(@PathVariable Long id) { return Result.ok(service.enableMaterial(id)); }

    @PostMapping("/materials/{id}/disable")
    public Result<MdMaterial> materialDisable(@PathVariable Long id) { return Result.ok(service.disableMaterial(id)); }

    @PostMapping("/materials/import")
    public Result<Map<String, Object>> materialImport(@RequestBody List<MdMaterial> req) { return Result.ok(Map.of("count", service.importMaterials(req))); }

    @GetMapping("/materials/export")
    public Result<Map<String, Object>> materialExport() { return Result.ok(service.export("material")); }

    // ----- 客户 -----
    @GetMapping("/customers")
    public Result<PageResult<MdCustomer>> customerList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                       @RequestParam(defaultValue = "20") Integer pageSize,
                                                       @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageCustomers(pageNo, pageSize, keyword));
    }

    @GetMapping("/customers/{id}")
    public Result<MdCustomer> customerDetail(@PathVariable Long id) { return Result.ok(service.getCustomer(id)); }

    @PostMapping("/customers")
    public Result<MdCustomer> customerSave(@RequestBody MdCustomer req) { return Result.ok(service.saveCustomer(req)); }

    @PostMapping("/customers/{id}/submit")
    public Result<MdCustomer> customerSubmit(@PathVariable Long id) { return Result.ok(service.submitCustomer(id)); }

    @PostMapping("/customers/{id}/audit")
    public Result<MdCustomer> customerAudit(@PathVariable Long id, @RequestBody AuditReq req) { return Result.ok(service.auditCustomer(id, req)); }

    @PostMapping("/customers/{id}/enable")
    public Result<MdCustomer> customerEnable(@PathVariable Long id) { return Result.ok(service.enableCustomer(id)); }

    @PostMapping("/customers/{id}/disable")
    public Result<MdCustomer> customerDisable(@PathVariable Long id) { return Result.ok(service.disableCustomer(id)); }

    @PostMapping("/customers/import")
    public Result<Map<String, Object>> customerImport(@RequestBody List<MdCustomer> req) { return Result.ok(Map.of("count", service.importCustomers(req))); }

    @GetMapping("/customers/export")
    public Result<Map<String, Object>> customerExport() { return Result.ok(service.export("customer")); }

    // ----- 供应商 -----
    @GetMapping("/suppliers")
    public Result<PageResult<MdSupplier>> supplierList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                       @RequestParam(defaultValue = "20") Integer pageSize,
                                                       @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageSuppliers(pageNo, pageSize, keyword));
    }

    @GetMapping("/suppliers/{id}")
    public Result<MdSupplier> supplierDetail(@PathVariable Long id) { return Result.ok(service.getSupplier(id)); }

    @PostMapping("/suppliers")
    public Result<MdSupplier> supplierSave(@RequestBody MdSupplier req) { return Result.ok(service.saveSupplier(req)); }

    @PostMapping("/suppliers/{id}/submit")
    public Result<MdSupplier> supplierSubmit(@PathVariable Long id) { return Result.ok(service.submitSupplier(id)); }

    @PostMapping("/suppliers/{id}/audit")
    public Result<MdSupplier> supplierAudit(@PathVariable Long id, @RequestBody AuditReq req) { return Result.ok(service.auditSupplier(id, req)); }

    @PostMapping("/suppliers/{id}/enable")
    public Result<MdSupplier> supplierEnable(@PathVariable Long id) { return Result.ok(service.enableSupplier(id)); }

    @PostMapping("/suppliers/{id}/disable")
    public Result<MdSupplier> supplierDisable(@PathVariable Long id) { return Result.ok(service.disableSupplier(id)); }

    @PostMapping("/suppliers/import")
    public Result<Map<String, Object>> supplierImport(@RequestBody List<MdSupplier> req) { return Result.ok(Map.of("count", service.importSuppliers(req))); }

    @GetMapping("/suppliers/export")
    public Result<Map<String, Object>> supplierExport() { return Result.ok(service.export("supplier")); }

    // ----- 仓库 -----
    @GetMapping("/warehouses")
    public Result<PageResult<MdWarehouse>> warehouseList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                         @RequestParam(defaultValue = "20") Integer pageSize,
                                                         @RequestParam(required = false) String keyword) {
        return Result.ok(service.pageWarehouses(pageNo, pageSize, keyword));
    }

    @GetMapping("/warehouses/{id}")
    public Result<MdWarehouse> warehouseDetail(@PathVariable Long id) { return Result.ok(service.getWarehouse(id)); }

    @PostMapping("/warehouses")
    public Result<MdWarehouse> warehouseSave(@RequestBody MdWarehouse req) { return Result.ok(service.saveWarehouse(req)); }

    @PostMapping("/warehouses/{id}/submit")
    public Result<MdWarehouse> warehouseSubmit(@PathVariable Long id) { return Result.ok(service.submitWarehouse(id)); }

    @PostMapping("/warehouses/{id}/audit")
    public Result<MdWarehouse> warehouseAudit(@PathVariable Long id, @RequestBody AuditReq req) { return Result.ok(service.auditWarehouse(id, req)); }

    @PostMapping("/warehouses/{id}/enable")
    public Result<MdWarehouse> warehouseEnable(@PathVariable Long id) { return Result.ok(service.enableWarehouse(id)); }

    @PostMapping("/warehouses/{id}/disable")
    public Result<MdWarehouse> warehouseDisable(@PathVariable Long id) { return Result.ok(service.disableWarehouse(id)); }

    @PostMapping("/warehouses/import")
    public Result<Map<String, Object>> warehouseImport(@RequestBody List<MdWarehouse> req) { return Result.ok(Map.of("count", service.importWarehouses(req))); }

    @GetMapping("/warehouses/export")
    public Result<Map<String, Object>> warehouseExport() { return Result.ok(service.export("warehouse")); }

    // ----- 库位 -----
    @GetMapping("/locations")
    public Result<PageResult<MdLocation>> locationList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                       @RequestParam(defaultValue = "20") Integer pageSize,
                                                       @RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) Long warehouseId) {
        return Result.ok(service.pageLocations(pageNo, pageSize, keyword, warehouseId));
    }

    @GetMapping("/locations/{id}")
    public Result<MdLocation> locationDetail(@PathVariable Long id) { return Result.ok(service.getLocation(id)); }

    @PostMapping("/locations")
    public Result<MdLocation> locationSave(@RequestBody MdLocation req) { return Result.ok(service.saveLocation(req)); }

    @PostMapping("/locations/{id}/submit")
    public Result<MdLocation> locationSubmit(@PathVariable Long id) { return Result.ok(service.submitLocation(id)); }

    @PostMapping("/locations/{id}/audit")
    public Result<MdLocation> locationAudit(@PathVariable Long id, @RequestBody AuditReq req) { return Result.ok(service.auditLocation(id, req)); }

    @PostMapping("/locations/{id}/enable")
    public Result<MdLocation> locationEnable(@PathVariable Long id) { return Result.ok(service.enableLocation(id)); }

    @PostMapping("/locations/{id}/disable")
    public Result<MdLocation> locationDisable(@PathVariable Long id) { return Result.ok(service.disableLocation(id)); }

    @PostMapping("/locations/import")
    public Result<Map<String, Object>> locationImport(@RequestBody List<MdLocation> req) { return Result.ok(Map.of("count", service.importLocations(req))); }

    @GetMapping("/locations/export")
    public Result<Map<String, Object>> locationExport() { return Result.ok(service.export("location")); }
}
