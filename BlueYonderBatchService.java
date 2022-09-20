package com.ft.publish.service;

import com.ft.publish.config.PropConfig;
import com.ft.publish.constants.AppConstants;
import com.ft.publish.dto.*;
import com.ft.publish.dto.output.BlueYonderOutput;
import com.ft.publish.dto.output.generic.FTProductByStoreOutput;
import com.ft.publish.util.AppUtil;
import com.ft.publish.util.CustomHeaderMappingStrategy;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlueYonderBatchService {


    @Autowired
    private PropConfig propConfig;

    @Autowired
    private ClientAPIServices clientAPIServices;

    @Autowired
    private UnifiedFeedServices unifiedFeedServices;

    @Autowired
    private AppUtil appUtil;

    @Value("${products}")
    private String productsFileName;

    @Value("${}")
    private String blueYonderArchiveLocation;

    @Value("${}")
    private String localDestDir;

    @Value("${}")
    private String localDownloadLocation;


    public void generateProducts(boolean publish) {
        log.info("BlueYonderBatchService.generateProducts.start");
        boolean archiveFlag = false;
        String fileName = productsFileName + appUtil.getCurrentTimestampForBatchFile()
                + AppConstants.PSV_FILE_EXTENSION;
        String localDestDir = propConfig.getFtEcomCategoryBatchLocalDownloadLocation() + fileName;
        try {
            Map<String, VendorResponseItem> vendorsMap = clientAPIServices.getAllVendors().getVendorsMap();
            List<Long> filteredRecords = unifiedFeedServices.getAllFilteredRecords();
            StoreAPIResponse storeAPIResponse = clientAPIServices.getStoreDetails();
            List<StoreItem> filteredStoreList = filterStores(storeAPIResponse);
            Map<String, OperationalCategoryResponseItem> operationalCategoryResponseItemMap = unifiedFeedServices.getAllCategoryRecords();

            List<BlueYonderOutput> records = new ArrayList<>();
            for (Long itemId : filteredRecords) {
                BlueYonderOutput.BlueYonderOutputBuilder builder = BlueYonderOutput.builder();

                ProductResponse itemMaster = clientAPIServices.getItemMasterById(itemId.toString(), propConfig.getGroup());

                builder.upcWoCd(itemMaster.getUpcEan());


                builder.productTitle(itemMaster.getMasterItemDescription());
                builder.sellSize(itemMaster.getMasterData().getSellSize());
                builder.unitOfMeasure(itemMaster.getMasterData().getUnitOfMeasure());


                String category = itemMaster.getMasterData().getCategory();
                OperationalCategoryResponseItem operationalCategory = operationalCategoryResponseItemMap.get(category);
                if(operationalCategory != null){
                    builder.depDescripPosArea(operationalCategory.getDescription());
                    builder.catClassDescrip(operationalCategory.getDescription());
                    builder.catFamilyDescrip(operationalCategory.getDescription());
                }

                builder.brandDescrip(itemMaster.getMasterData().getBrandDescription());
                builder.brandCode(itemMaster.getMasterData().getBrandCode());

//                builder.FtGlutenFree(itemMaster.getMasterData().getGlutenFree());
                builder.glutenFree(null);
                builder.itemAddedDate(itemMaster.getMasterData().getItemAddedDate());
                builder.manufacturer(AppConstants.EMPTY_STRING);
                builder.imStatusIndicate(itemMaster.getMasterData().getImStatusIndicate());
                builder.recordStatus(null);
//                builder.ImStatusIndicateDate(itemMaster.getMasterData().getIm);

                builder.posFlagId(itemMaster.getMasterData().getPosFlagsId());

                List<VendorItemData> vendorItems = itemMaster.getVendorItems();
                for (VendorItemData vid : vendorItems) {
                    if (vendorsMap.containsKey(vid.getViId())) {
                        VendorResponseItem vendorResponseItem = vendorsMap.get(vid.getViId());
                        if (vendorResponseItem.getDeliveryStatus() == 2) {
                            BlueYonderOutput.BlueYonderOutputBuilder copyBuilder = builder.build().toBuilder();
                            copyBuilder.viId(vid.getViId()); //send multiple records
                            copyBuilder.casePack(vid.getCasePack());
                            records.add(copyBuilder.build());
                        }
                    }
                }
                records.add(builder.build());
            }

            archiveFlag = true;

            generateProductsPSVFile(records);

            if(publish){
//                appUtil.copyFileToFTP()
            }

        } catch (Exception e) {
            log.error("BlueYonderBatchService.generateProducts.Exception occurred:" + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (archiveFlag) {
                File sourceDir = new File(localDestDir);
                File destDir = new File(blueYonderArchiveLocation + fileName);
                try {
                    appUtil.archiveLocalFile(sourceDir, destDir, propConfig.getFtEcomCategoryBatchArchiveLocation());
                } catch (IOException e) {
                    log.error("BlueYonderBatchService.generateProducts.IO Exception occurred while archival: "
                            + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        log.info("BlueYonderBatchService.generateProducts.end");
    }

    private void generateProductsPSVFile(List<BlueYonderOutput> productByStoreOutputList)

            throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

        log.info("FTBatchServices.generateProductsPSVFile.start");

        if (!new File(localDownloadLocation).exists()) {
            new File(localDownloadLocation).mkdirs();
        }

        FileWriter file = new FileWriter(localDestDir);
        final CustomHeaderMappingStrategy<BlueYonderOutput> mappingStrategy = new CustomHeaderMappingStrategy<BlueYonderOutput>(
                AppConstants.BY_PRODUCTS_HEADERS);
        mappingStrategy.setType(BlueYonderOutput.class);

        StatefulBeanToCsv<BlueYonderOutput> sbc = new StatefulBeanToCsvBuilder(file)
                .withMappingStrategy(mappingStrategy).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(AppConstants.DELIMITER_PIPE).build();

        if (productByStoreOutputList != null && productByStoreOutputList.size() > 0) {
            sbc.write(productByStoreOutputList);
        }
        file.close();
        log.info("FTBatchServices.generateProductsPSVFile.end");
    }

    private List<StoreItem> filterStores(StoreAPIResponse storeAPIResponse) {

        log.info("BlueYonderBatchService.filterStores.start");
        List<StoreItem> filteredStoreItemList = new ArrayList<StoreItem>();

        List<Long> testStores = propConfig.getTestStores();

        if (storeAPIResponse != null && storeAPIResponse.getStores() != null
                && storeAPIResponse.getStores().size() > 0) {

            List<StoreItem> storeItemList = storeAPIResponse.getStores();

            filteredStoreItemList = storeItemList.stream().filter(e ->
                    !testStores.contains(e.getStoreNumber()) && e.getHasEcommerce() == 1 && e.getStoreStatus() == 0
            ).collect(Collectors.toList());
//            getBlueYonderDeliveryEnabled

        }
        log.info("FTBatchServices.filterStores.end");
        return filteredStoreItemList;
    }


    public void publishAll(boolean publish) {
        generateProducts(publish );
    }
}
