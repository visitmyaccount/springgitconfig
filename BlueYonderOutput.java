package com.ft.publish.dto.output;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public class BlueYonderOutput {

    @CsvBindByName(column = "vi_id")
    private Long viId;

    @CsvBindByName(column = "upc_wo_cd")
    private String upcWoCd;

    @CsvBindByName(column = "vendor_item")
    private Long vendorItem;

    @CsvBindByName(column = "product_title")
    private String productTitle;

    @CsvBindByName(column = "size")
    private float sellSize;

    @CsvBindByName(column = "unit_of_measure")
    private String  unitOfMeasure;

    @CsvBindByName(column = "case_pack")
    private float  casePack;

    @CsvBindByName(column = "major_dept_desc")
    private String  depDescripPosArea;

    @CsvBindByName(column = "minor_dept_desc")
    private String  subCategory;

    @CsvBindByName(column = "major_category_desc")
    private String catClassDescrip;

    @CsvBindByName(column = "minor_category_desc")
    private String catFamilyDescrip;



    //pending
    @CsvBindByName(column = "brand")
    private String brandDescrip;

    @CsvBindByName(column = "gluten_free")
    private Long glutenFree; //no

    @CsvBindByName(column = "heart_healthy")
    private String heartHealthy; // no

    @CsvBindByName(column = "local")
    private String locallyGrown; // no

    @CsvBindByName(column = "usa_certified_organic")
    private String certifiedOrganic; // no

    @CsvBindByName(column = "wic_eligible")
    private String iposWicEligible; //no

    @CsvBindByName(column = "item_added_date")
    private String itemAddedDate;

    @CsvBindByName(column = "brand_code")
    private String brandCode;

    @CsvBindByName(column = "manufacturer")
    private String manufacturer;

    @CsvBindByName(column = "vendor")
    private String vendor;

    @CsvBindByName(column = "item_status_indicate")
    private long imStatusIndicate;

    @CsvBindByName(column = "record_status")
    private String recordStatus; // no


    @CsvBindByName(column = "product_of")
    private String productOf;

    @CsvBindByName(column = "keto")
    private String keto;

    @CsvBindByName(column = "nongmo")
    private String nongmo;

    @CsvBindByName(column = "paleo")
    private String paleo;
    @CsvBindByName(column = "free_from")
    private String freeFrom;

    @CsvBindByName(column = "paraben_free")
    private String parabenFree;

    @CsvBindByName(column = "plant_based")
    private String plantBased;


    @CsvBindByName(column = "unit_width")
    private String unitWidth;

    @CsvBindByName(column = "unit_height")
    private String unitHeight;

    @CsvBindByName(column = "unit_depth")
    private String unitDepth;

    @CsvBindByName(column = "status_indicate_date")
    private String imStatusIndicateDate;

    @CsvBindByName(column = "posflagid")
    private Long posFlagId;

    @CsvBindByName(column = "ship_weight")
    private Integer shipWeight;

    @CsvBindByName(column = "average_price")
    private Integer averagePrice;

    @CsvBindByName(column = "average_cost")
    private Integer averageCost;

    @CsvBindByName(column = "tray_width")
    private Integer trayWidth;

    @CsvBindByName(column = "tray_height")
    private Integer trayHeight;

    @CsvBindByName(column = "tray_depth")
    private Integer trayDepth;

    @CsvBindByName(column = "case_width")
    private Integer caseWidth;

    @CsvBindByName(column = "case_height")
    private Integer caseHeight;

    @CsvBindByName(column = "case_depth")
    private Integer caseDepth;

    @CsvBindByName(column = "division")
    private Integer division;


    @CsvBindByName(column = "authorized_stores")
    private String authorizedStores;

    @CsvBindByName(column = "item_type")
    private String itemType;

}
